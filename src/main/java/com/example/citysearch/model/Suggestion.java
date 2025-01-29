package com.example.citysearch.model;

import io.swagger.v3.oas.annotations.media.Schema;

public class Suggestion {

  @Schema(description = "Name of the city", example = "New York")
  private String name;

  @Schema(description = "Latitude of the city", example = "40.7128")
  private double latitude;

  @Schema(description = "Longitude of the city", example = "-74.0060")
  private double longitude;

  @Schema(description = "Relevance score of the suggestion", example = "0.95")
  private double score;

  public Suggestion(String name, double latitude, double longitude, double score) {
    this.name = name;
    this.latitude = latitude;
    this.longitude = longitude;
    this.score = score;
  }

  public String getName() {
    return name;
  }

  public double getLatitude() {
    return latitude;
  }

  public double getLongitude() {
    return longitude;
  }

  public double getScore() {
    return score;
  }
}
