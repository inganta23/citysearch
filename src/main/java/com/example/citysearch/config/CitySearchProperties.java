package com.example.citysearch.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "citysearch")
public class CitySearchProperties {
  private double textScoreWeight;
  private double locationScoreWeight;
  private int limitData;

  public int getLimitData() {
    return limitData;
  }

  public void setLimitData(int limitData) {
    this.limitData = limitData;
  }

  public double getTextScoreWeight() {
    return textScoreWeight;
  }

  public void setTextScoreWeight(double textScoreWeight) {
    this.textScoreWeight = textScoreWeight;
  }

  public double getLocationScoreWeight() {
    return locationScoreWeight;
  }

  public void setLocationScoreWeight(double locationScoreWeight) {
    this.locationScoreWeight = locationScoreWeight;
  }

}
