package com.example.citysearch.trie;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.example.citysearch.model.City;

public class CitySearchTrie {
  private final TrieNode root = new TrieNode();

  private final ReadWriteLock lock = new ReentrantReadWriteLock();
  private int size = 0;

  public void insert(String cityName, City city) {
    lock.writeLock().lock();

    try {
      if (!cityExists(cityName)) {
        TrieNode node = root;
        for (char c : cityName.toLowerCase().toCharArray()) {
          node.children.putIfAbsent(c, new TrieNode());
          node = node.children.get(c);
        }
        node.isEndOfWord = true;
        node.cities.add(city);
        size++;
      }
    } finally {
      lock.writeLock().unlock();
    }

  }

  public int getSize() {
    lock.readLock().lock();
    try {
      return size;
    } finally {
      lock.readLock().unlock();
    }
  }

  private boolean cityExists(String cityName) {
    TrieNode node = root;
    for (char c : cityName.toLowerCase().toCharArray()) {
      node = node.children.get(c);
      if (node == null) {
        return false;
      }
    }
    return node.isEndOfWord;
  }

  public List<City> searchByPrefix(String prefix, int limit) {
    TrieNode node = root;
    for (char c : prefix.toLowerCase().toCharArray()) {
      node = node.children.get(c);
      if (node == null) {
        return new ArrayList<>();
      }
    }
    List<City> results = new ArrayList<>();
    collectCities(node, results, limit);
    return results;
  }

  public List<City> getAllCities(int limit) {
    List<City> results = new ArrayList<>();
    collectCities(root, results, limit);
    return results;
  }

  private void collectCities(TrieNode node, List<City> results, int limit) {
    if (node == null || results.size() >= limit) {
      return;
    }

    for (City city : node.cities) {
      if (results.size() >= limit) {
        return;
      }
      results.add(city);
    }

    for (TrieNode child : node.children.values()) {
      collectCities(child, results, limit);
      if (results.size() >= limit) {
        return;
      }
    }
  }

  public void printAllCities() {
    printAllCitiesHelper(root, "");
  }

  private void printAllCitiesHelper(TrieNode node, String prefix) {
    if (node.isEndOfWord) {
      for (City city : node.cities) {
        System.out.println("City: " + prefix + ", Lat: " + city.getLatitude() + ", Lon: " + city.getLongitude());
      }
    }
    for (Map.Entry<Character, TrieNode> entry : node.children.entrySet()) {
      printAllCitiesHelper(entry.getValue(), prefix + entry.getKey());
    }
  }

}
