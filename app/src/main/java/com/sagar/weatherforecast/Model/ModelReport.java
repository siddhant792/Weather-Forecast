package com.sagar.weatherforecast.Model;

public class ModelReport {
    String temp, temp_min, temp_max, humidity, windspeed, day;

    public ModelReport(String temp, String temp_min, String temp_max, String humidity, String windspeed, String day) {
        this.temp = temp;
        this.temp_min = temp_min;
        this.temp_max = temp_max;
        this.humidity = humidity;
        this.windspeed = windspeed;
        this.day = day;
    }

    public String getTemp() {
        return temp;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }

    public String getTemp_min() {
        return temp_min;
    }

    public void setTemp_min(String temp_min) {
        this.temp_min = temp_min;
    }

    public String getTemp_max() {
        return temp_max;
    }

    public void setTemp_max(String temp_max) {
        this.temp_max = temp_max;
    }

    public String getHumidity() {
        return humidity;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }

    public String getWindspeed() {
        return windspeed;
    }

    public void setWindspeed(String windspeed) {
        this.windspeed = windspeed;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }
}
