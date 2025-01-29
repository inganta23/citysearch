package com.example.citysearch.loader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.example.citysearch.model.City;
import com.example.citysearch.trie.CitySearchTrie;

@Component
public class CityDataLoader {
  private final CitySearchTrie trie = new CitySearchTrie();

  @Value("classpath:cities_canada-usa.tsv")
  private Resource citiesResource;

  public void loadFromMultipartFile(MultipartFile file) throws IOException {
    try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
      String line;
      String headerLine = reader.readLine();
      if (headerLine == null) {
        throw new IOException("The file is empty.");
      }
      String[] headers = headerLine.split("\t");
      Map<String, Integer> columnIndexMap = new HashMap<>();
      for (int i = 0; i < headers.length; i++) {
        columnIndexMap.put(headers[i].toLowerCase(), i);
      }

      if (!columnIndexMap.containsKey("name") ||
          !columnIndexMap.containsKey("lat") ||
          !columnIndexMap.containsKey("long")) {
        throw new IOException("Required columns (name, latitude, longitude) are missing.");
      }

      while ((line = reader.readLine()) != null) {
        String[] parts = line.split("\t");
        String name = parts[columnIndexMap.get("name")];
        double latitude = Double.parseDouble(parts[columnIndexMap.get("lat")]);
        double longitude = Double.parseDouble(parts[columnIndexMap.get("long")]);

        trie.insert(name, new City(name, latitude, longitude));
      }
    }
  }

  public void loadFromTsv(String filePath) throws IOException {
    try (InputStream inputStream = citiesResource.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
      String line;
      String headerLine = reader.readLine();
      if (headerLine == null) {
        throw new IOException("The file is empty.");
      }
      String[] headers = headerLine.split("\t");
      Map<String, Integer> columnIndexMap = new HashMap<>();
      for (int i = 0; i < headers.length; i++) {
        columnIndexMap.put(headers[i].toLowerCase(), i);
      }

      if (!columnIndexMap.containsKey("name") ||
          !columnIndexMap.containsKey("lat") ||
          !columnIndexMap.containsKey("long")) {
        throw new IOException("Required columns (name, latitude, longitude) are missing.");
      }

      while ((line = reader.readLine()) != null) {
        String[] parts = line.split("\t");
        String name = parts[columnIndexMap.get("name")];
        double latitude = Double.parseDouble(parts[columnIndexMap.get("lat")]);
        double longitude = Double.parseDouble(parts[columnIndexMap.get("long")]);

        trie.insert(name, new City(name, latitude, longitude));
      }
    }
  }

  public CitySearchTrie getTrie() {
    return trie;
  }

}
