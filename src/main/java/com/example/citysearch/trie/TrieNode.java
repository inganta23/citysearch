package com.example.citysearch.trie;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.citysearch.model.City;

public class TrieNode {
  Map<Character, TrieNode> children = new HashMap<>();
  List<City> cities = new ArrayList<>();
  boolean isEndOfWord = false;
}
