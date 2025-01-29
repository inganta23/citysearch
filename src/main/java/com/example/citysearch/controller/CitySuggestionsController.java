package com.example.citysearch.controller;

import com.example.citysearch.model.Suggestion;
import com.example.citysearch.service.CitySearchService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/suggestions")
public class CitySuggestionsController {
  private final CitySearchService citySearchService;

  public CitySuggestionsController(CitySearchService citySearchService) {
    this.citySearchService = citySearchService;
  }

  @GetMapping
  @Operation(summary = "Get city suggestions", description = "Returns city suggestions based on search query and optional coordinates")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully retrieved suggestions", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Suggestion.class))),
      @ApiResponse(responseCode = "400", description = "Invalid input parameters", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\n"
          +
          "  \"path\": \"/api/v1/suggestions\",\n" +
          "  \"error\": \"Bad Request\",\n" +
          "  \"message\": \"Longitude must be between -180 and 180 degrees\",\n" +
          "  \"status\": 400\n" +
          "}")))

  })
  public Map<String, Object> getSuggestions(
      @RequestParam(required = false) String q,
      @RequestParam(required = false) Double latitude,
      @RequestParam(required = false) Double longitude) {
    if ((latitude == null && longitude != null) || (latitude != null && longitude == null)) {
      throw new IllegalArgumentException("Both latitude and longitude must be provided together");
    }
    if (latitude != null && (latitude < -90 || latitude > 90)) {
      throw new IllegalArgumentException("Latitude must be between -90 and 90 degrees");
    }
    if (longitude != null && (longitude < -180 || longitude > 180)) {
      throw new IllegalArgumentException("Longitude must be between -180 and 180 degrees");
    }

    List<Suggestion> suggestions = citySearchService.search(q, latitude, longitude);
    Map<String, Object> response = new HashMap<>();
    response.put("suggestions", suggestions);
    return response;
  }

  @Operation(summary = "Upload city data", description = "Upload TSV file containing city data")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "File successfully processed", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{ \"message\": \"File uploaded and data loaded into Trie successfully\" }"))),
      @ApiResponse(responseCode = "400", description = "Invalid file or format", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\n"
          +
          "  \"path\": \"/api/v1/suggestions\",\n" +
          "  \"error\": \"Bad Request\",\n" +
          "  \"message\": \"Invalid file type. Please upload a .tsv file.\",\n" +
          "  \"status\": 400\n" +
          "}"))),
      @ApiResponse(responseCode = "413", description = "File too large", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\n"
          +
          "  \"instance\": \"/api/v1/suggestions\",\n" +
          "  \"title\": \"Payload Too Large\",\n" +
          "  \"detail\": \"Maximum upload size exceeded\",\n" +
          "  \"status\": 413\n" +
          "}"))),
      @ApiResponse(responseCode = "500", description = "Processing error", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\n"
          +
          "  \"path\": \"/api/v1/suggestions\",\n" +
          "  \"error\": \"Internal Server Error\",\n" +
          "  \"message\": \"An unexpected error occurred. Please try again later.\",\n" +
          "  \"status\": 500\n" +
          "}")))
  })
  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<Map<String, String>> uploadFile(
      @Parameter(description = "TSV file to upload", required = true, content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE)) @RequestParam MultipartFile file) {
    if (file.isEmpty()) {
      throw new IllegalArgumentException("File is empty");
    }

    String fileName = file.getOriginalFilename();
    if (fileName == null || !fileName.endsWith(".tsv")) {
      throw new IllegalArgumentException("Invalid file type. Please upload a .tsv file.");
    }

    try {
      citySearchService.loadFromMultipartFile(file);
      Map<String, String> response = new HashMap<>();
      response.put("message", "File uploaded and data loaded into Trie successfully");
      return ResponseEntity.ok(response);
    } catch (Exception e) {
      throw new RuntimeException("Failed to load city data from file", e);
    }

  }
}
