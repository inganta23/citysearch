package com.example.citysearch.runner;

import org.springframework.stereotype.Component;
import org.springframework.boot.CommandLineRunner;
import com.example.citysearch.loader.CityDataLoader;

@Component
public class ApplicationRunner implements CommandLineRunner {
  private final CityDataLoader dataLoader;

  public ApplicationRunner(CityDataLoader dataLoader) {
    this.dataLoader = dataLoader;
  }

  @Override
  public void run(String... args) throws Exception {
    dataLoader.loadFromTsv("src/main/resources/cities_canada-usa.tsv");

    System.out.println("City data loaded successfully.");
  }
}
