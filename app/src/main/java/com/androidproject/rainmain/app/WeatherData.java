package com.androidproject.rainmain.app;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Saurabh on 2015-02-02.
 */
public class WeatherData implements Serializable{

    private String cityName;
    private Long currentTemp;
    private Long maxTemp;
    private Long minTemp;
    private Long pressure;
    private Long windSpeed;
    private Long humidity;
    private String mainForecast;
    private String descpForecast;
    private int imageId;

    public String getDescpForecast() {
        return descpForecast;
    }

    public void setDescpForecast(String descpForecast) {
        this.descpForecast = descpForecast;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public long getMainWeatherId() {
        return mainWeatherId;
    }

    public void setMainWeatherId(long mainWeatherId) {
        this.mainWeatherId = mainWeatherId;
    }

    public String getDate() {
        return date;
    }

    private String date;
    private long mainWeatherId;
    public void setDate(String date) {
        this.date = date;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public Long getCurrentTemp() {
        return currentTemp;
    }

    public void setCurrentTemp(Long currentTemp) {
        this.currentTemp = currentTemp;
    }

    public Long getMaxTemp() {
        return maxTemp;
    }

    public void setMaxTemp(Long maxTemp) {
        this.maxTemp = maxTemp;
    }

    public Long getMinTemp() {
        return minTemp;
    }

    public void setMinTemp(Long minTemp) {
        this.minTemp = minTemp;
    }

    public Long getPressure() {
        return pressure;
    }

    public void setPressure(Long pressure) {
        this.pressure = pressure;
    }

    public Long getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(Long windSpeed) {
        this.windSpeed = windSpeed;
    }

    public Long getHumidity() {
        return humidity;
    }

    public void setHumidity(Long humidity) {
        this.humidity = humidity;
    }

    public String getMainForecast() {
        return mainForecast;
    }

    public void setMainForecast(String mainForecast) {
        this.mainForecast = mainForecast;
    }


}
