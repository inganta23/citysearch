package com.example.citysearch.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.citysearch.config.CitySearchProperties;
import com.example.citysearch.loader.CityDataLoader;
import com.example.citysearch.model.City;
import com.example.citysearch.model.Suggestion;

@Service
public class CitySearchService {
  private final CityDataLoader dataLoader;
  private final CitySearchProperties citySearchProperties;

  public CitySearchService(CityDataLoader dataLoader, CitySearchProperties citySearchProperties) {
    this.dataLoader = dataLoader;
    this.citySearchProperties = citySearchProperties;
  }

  public List<Suggestion> search(String query, Double latitude, Double longitude) {
    List<City> matchedCities;
    List<Suggestion> suggestions = new ArrayList<>();

    if (query == null || query.isEmpty()) {
      matchedCities = dataLoader.getTrie().getAllCities(citySearchProperties.getLimitData());
    } else {
      matchedCities = dataLoader.getTrie().searchByPrefix(query, citySearchProperties.getLimitData());
    }

    for (City city : matchedCities) {
      double textScore = (query == null || query.isEmpty()) ? 0 : calculateTextScore(query, city.getName());
      double locationScore = (latitude != null && longitude != null)
          ? calculateProximityScore(latitude, longitude, city.getLatitude(), city.getLongitude())
          : 0;
      double totalScore = (textScore * citySearchProperties.getTextScoreWeight())
          + (locationScore * citySearchProperties.getLocationScoreWeight());
      suggestions.add(new Suggestion(city.getName(), city.getLatitude(), city.getLongitude(), totalScore));
    }

    suggestions.sort((a, b) -> Double.compare(b.getScore(), a.getScore()));
    return suggestions;
  }

  public void loadFromMultipartFile(MultipartFile file) throws IOException {
    dataLoader.loadFromMultipartFile(file);
  }

  private double calculateTextScore(String query, String cityName) {
    query = query.toLowerCase();
    cityName = cityName.toLowerCase();
    return cityName.startsWith(query) ? 1.0 : 0.0;
  }

  private double calculateProximityScore(double lat1, double lon1, double lat2, double lon2) {
    final int R = 6371;

    double latDistance = Math.toRadians(lat2 - lat1);
    double lonDistance = Math.toRadians(lon2 - lon1);
    double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
        + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
            * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    double distance = R * c;
    double maxDistance = 20000;
    return Math.max(0.0, Math.min(1.0, 1.0 - (distance / maxDistance)));
  }
}
