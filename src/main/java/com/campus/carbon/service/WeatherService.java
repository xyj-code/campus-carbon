package com.campus.carbon.service;

import com.campus.carbon.model.dto.AgentWeatherVO;

public interface WeatherService {
    AgentWeatherVO getCurrentWeather(Double latitude, Double longitude);
}
