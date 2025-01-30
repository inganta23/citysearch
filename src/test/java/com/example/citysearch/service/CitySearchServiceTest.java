package com.example.citysearch.service;

import com.example.citysearch.config.CitySearchProperties;
import com.example.citysearch.loader.CityDataLoader;
import com.example.citysearch.model.City;
import com.example.citysearch.model.Suggestion;
import com.example.citysearch.trie.CitySearchTrie;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class CitySearchServiceTest {

  @Mock
  private CityDataLoader dataLoader;

  @Mock
  private CitySearchProperties citySearchProperties;

  @InjectMocks
  private CitySearchService citySearchService;

  private CitySearchTrie mockTrie;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    mockTrie = new CitySearchTrie();
    when(dataLoader.getTrie()).thenReturn(mockTrie);
  }

  @Test
  void testSearch_WithValidQueryAndCoordinates() {
    String query = "Toronto";
    Double latitude = 43.7;
    Double longitude = -79.42;

    City city = new City("Toronto", 43.7, -79.42);
    mockTrie.insert("Toronto", city);

    when(citySearchProperties.getLimitData()).thenReturn(10);
    when(citySearchProperties.getTextScoreWeight()).thenReturn(0.5);
    when(citySearchProperties.getLocationScoreWeight()).thenReturn(0.5);

    List<Suggestion> suggestions = citySearchService.search(query, latitude, longitude);

    assertEquals(1, suggestions.size());
    assertEquals("Toronto", suggestions.get(0).getName());
  }

  @Test
  void testSearch_WithEmptyQuery() {
    String query = "";
    Double latitude = 43.7;
    Double longitude = -79.42;

    City city = new City("Toronto", 43.7, -79.42);
    mockTrie.insert("Toronto", city);

    when(citySearchProperties.getLimitData()).thenReturn(10);

    List<Suggestion> suggestions = citySearchService.search(query, latitude, longitude);

    assertEquals(1, suggestions.size());
    assertEquals("Toronto", suggestions.get(0).getName());
  }

  @Test
  void testSearch_WithNoMatchingCities() {
    String query = "NonExistentCity";
    Double latitude = 43.7;
    Double longitude = -79.42;

    when(citySearchProperties.getLimitData()).thenReturn(10);

    List<Suggestion> suggestions = citySearchService.search(query, latitude, longitude);

    assertTrue(suggestions.isEmpty());
  }

  @Test
  void testSearch_WithValidCoordinates_NoQuery() {
    String query = null;
    Double latitude = 43.7;
    Double longitude = -79.42;

    City city = new City("Toronto", 43.7, -79.42);
    mockTrie.insert("Toronto", city);

    when(citySearchProperties.getLimitData()).thenReturn(10);

    List<Suggestion> suggestions = citySearchService.search(query, latitude, longitude);

    assertEquals(1, suggestions.size());
    assertEquals("Toronto", suggestions.get(0).getName());
  }

  @Test
  void testSearch_ScoringMechanism() {
    String query = "Toronto";
    Double latitude = 43.7;
    Double longitude = -79.42;

    City city1 = new City("Toronto", 43.7, -79.42);
    City city2 = new City("Toronto 2", 44.0, -80.0);
    mockTrie.insert("Toronto", city1);
    mockTrie.insert("Toronto 2", city2);

    when(citySearchProperties.getLimitData()).thenReturn(10);
    when(citySearchProperties.getTextScoreWeight()).thenReturn(0.7);
    when(citySearchProperties.getLocationScoreWeight()).thenReturn(0.3);

    List<Suggestion> suggestions = citySearchService.search(query, latitude, longitude);

    assertEquals(2, suggestions.size());
    assertEquals("Toronto", suggestions.get(0).getName());
    assertTrue(suggestions.get(0).getScore() > suggestions.get(1).getScore());
  }

  @Test
  void testLoadFromMultipartFile_ValidFile() throws Exception {
    MockMultipartFile file = new MockMultipartFile("file", "cities.tsv", "text/tab-separated-values",
        "name\tlat\tlong\nToronto\t43.7\t-79.42".getBytes());

    doNothing().when(dataLoader).loadFromMultipartFile(any());

    assertDoesNotThrow(() -> citySearchService.loadFromMultipartFile(file));
    verify(dataLoader, times(1)).loadFromMultipartFile(file);
  }

}