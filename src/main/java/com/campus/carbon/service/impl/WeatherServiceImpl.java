package com.campus.carbon.service.impl;

import com.campus.carbon.model.dto.AgentWeatherVO;
import com.campus.carbon.service.WeatherService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Locale;

@Service
public class WeatherServiceImpl implements WeatherService {

    private static final String DISTRICT_ENDPOINT = "https://restapi.amap.com/v3/geocode/regeo";
    private static final String WEATHER_ENDPOINT = "https://restapi.amap.com/v3/weather/weatherInfo";

    private final OkHttpClient client = new OkHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    @Value("${carbon.amap.web-key:}")
    private String amapWebKey;

    @Override
    public AgentWeatherVO getCurrentWeather(Double latitude, Double longitude) {
        AgentWeatherVO weather = new AgentWeatherVO();
        if (safeText(amapWebKey).isEmpty()) {
            weather.setStatus("config_missing");
            weather.setRecommendation("\u5c1a\u672a\u914d\u7f6e\u9ad8\u5fb7 Web API Key\uff0cAgent \u6682\u65f6\u65e0\u6cd5\u8bfb\u53d6\u5929\u6c14\u3002");
            return weather;
        }
        if (latitude == null || longitude == null) {
            weather.setStatus("location_required");
            weather.setRecommendation("\u83b7\u53d6\u5f53\u524d\u4f4d\u7f6e\u540e\uff0cAgent \u624d\u80fd\u7ed3\u5408\u5f53\u5730\u5929\u6c14\u8c03\u6574\u8fd0\u52a8\u5efa\u8bae\u3002");
            return weather;
        }

        try {
            String adcode = resolveAdcode(latitude, longitude);
            if (adcode.isEmpty()) {
                weather.setStatus("request_failed");
                weather.setRecommendation("\u6682\u65f6\u672a\u80fd\u8bc6\u522b\u5f53\u524d\u533a\u57df\uff0cAgent \u672c\u6b21\u4e0d\u4f7f\u7528\u5929\u6c14\u7ea6\u675f\u3002");
                return weather;
            }
            return queryWeather(adcode);
        } catch (IOException e) {
            weather.setStatus("request_failed");
            weather.setRecommendation("\u5929\u6c14\u8bfb\u53d6\u5931\u8d25\uff0cAgent \u672c\u6b21\u4e0d\u4f7f\u7528\u5929\u6c14\u7ea6\u675f\u3002");
            return weather;
        }
    }

    private String resolveAdcode(Double latitude, Double longitude) throws IOException {
        HttpUrl url = HttpUrl.parse(DISTRICT_ENDPOINT).newBuilder()
                .addQueryParameter("key", amapWebKey)
                .addQueryParameter("location", formatLocation(longitude, latitude))
                .addQueryParameter("extensions", "base")
                .build();
        Request request = new Request.Builder().url(url).get().build();
        try (Response response = client.newCall(request).execute()) {
            ResponseBody body = response.body();
            String text = body == null ? "" : body.string();
            if (!response.isSuccessful()) {
                return "";
            }
            JsonNode root = mapper.readTree(text);
            if (!"1".equals(safeText(root.path("status").asText("")))) {
                return "";
            }
            JsonNode addressComponent = root.path("regeocode").path("addressComponent");
            String adcode = safeText(addressComponent.path("adcode").asText(""));
            if (!adcode.isEmpty()) {
                return adcode;
            }
            return safeText(addressComponent.path("citycode").asText(""));
        }
    }

    private AgentWeatherVO queryWeather(String adcode) throws IOException {
        AgentWeatherVO weather = new AgentWeatherVO();
        HttpUrl url = HttpUrl.parse(WEATHER_ENDPOINT).newBuilder()
                .addQueryParameter("key", amapWebKey)
                .addQueryParameter("city", adcode)
                .addQueryParameter("extensions", "base")
                .build();
        Request request = new Request.Builder().url(url).get().build();
        try (Response response = client.newCall(request).execute()) {
            ResponseBody body = response.body();
            String text = body == null ? "" : body.string();
            if (!response.isSuccessful()) {
                weather.setStatus("request_failed");
                weather.setRecommendation("\u9ad8\u5fb7\u5929\u6c14\u8bf7\u6c42\u5931\u8d25\uff0cHTTP " + response.code());
                return weather;
            }
            JsonNode root = mapper.readTree(text);
            if (!"1".equals(safeText(root.path("status").asText("")))) {
                weather.setStatus("request_failed");
                weather.setRecommendation(buildAmapError(root));
                return weather;
            }
            JsonNode lives = root.path("lives");
            if (!lives.isArray() || lives.isEmpty()) {
                weather.setStatus("no_result");
                weather.setRecommendation("\u6682\u65f6\u6ca1\u6709\u8bfb\u5230\u5f53\u5730\u5b9e\u65f6\u5929\u6c14\uff0cAgent \u672c\u6b21\u4e0d\u4f7f\u7528\u5929\u6c14\u7ea6\u675f\u3002");
                return weather;
            }

            JsonNode live = lives.get(0);
            weather.setStatus("ready");
            weather.setCity(safeText(live.path("city").asText("")));
            weather.setWeather(safeText(live.path("weather").asText("")));
            weather.setTemperature(safeText(live.path("temperature").asText("")));
            weather.setWindDirection(safeText(live.path("winddirection").asText("")));
            weather.setWindPower(safeText(live.path("windpower").asText("")));
            weather.setReportTime(safeText(live.path("reporttime").asText("")));
            decorateWeatherAdvice(weather);
            return weather;
        }
    }

    private void decorateWeatherAdvice(AgentWeatherVO weather) {
        String weatherText = safeText(weather.getWeather());
        int temperature = parseInt(weather.getTemperature());
        boolean rainy = containsAny(weatherText, "\u96e8", "\u96ea", "\u96f7", "\u51b0\u96f9");
        boolean hot = temperature >= 33;
        boolean cold = temperature > 0 && temperature <= 8;
        boolean windy = isStrongWind(weather.getWindPower());

        if (rainy) {
            weather.setSuitability("\u5ba4\u5185\u4f18\u5148");
            weather.setOutdoorFriendly(false);
            weather.setIndoorPreferred(true);
            weather.setRecommendation("\u5f53\u524d\u6709\u964d\u6c34\uff0cAgent \u4f1a\u964d\u4f4e\u6237\u5916\u8dd1\u6b65\u548c\u8fdc\u8ddd\u79bb\u6b65\u884c\u4efb\u52a1\u4f18\u5148\u7ea7\u3002");
            return;
        }
        if (hot) {
            weather.setSuitability("\u907f\u5f00\u9ad8\u6e29");
            weather.setOutdoorFriendly(false);
            weather.setIndoorPreferred(true);
            weather.setRecommendation("\u5f53\u524d\u6c14\u6e29\u8f83\u9ad8\uff0cAgent \u4f1a\u4f18\u5148\u63a8\u8350\u5ba4\u5185\u6216\u77ed\u65f6\u4f4e\u5f3a\u5ea6\u52a8\u4f5c\u3002");
            return;
        }
        if (cold || windy) {
            weather.setSuitability("\u8f7b\u91cf\u6237\u5916");
            weather.setOutdoorFriendly(true);
            weather.setIndoorPreferred(false);
            weather.setRecommendation("\u5f53\u524d\u5929\u6c14\u5bf9\u957f\u65f6\u95f4\u6237\u5916\u8fd0\u52a8\u4e0d\u7b97\u7406\u60f3\uff0cAgent \u4f1a\u4f18\u5148\u63a8\u8350\u8f83\u77ed\u8def\u7ebf\u3002");
            return;
        }

        weather.setSuitability("\u9002\u5408\u6237\u5916");
        weather.setOutdoorFriendly(true);
        weather.setIndoorPreferred(false);
        weather.setRecommendation("\u5f53\u524d\u5929\u6c14\u8f83\u9002\u5408\u6237\u5916\u6d3b\u52a8\uff0cAgent \u53ef\u4ee5\u4f18\u5148\u63a8\u8350\u6821\u56ed\u5468\u8fb9\u6b65\u884c\u3001\u8dd1\u6b65\u6216\u7403\u7c7b\u573a\u5730\u3002");
    }

    private boolean isStrongWind(String windPower) {
        String value = safeText(windPower);
        if (value.isEmpty()) {
            return false;
        }
        if (value.contains("\u2264") || value.contains("<")) {
            return false;
        }
        int firstNumber = 0;
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < value.length(); i++) {
            char ch = value.charAt(i);
            if (Character.isDigit(ch)) {
                builder.append(ch);
            } else if (builder.length() > 0) {
                break;
            }
        }
        if (builder.length() > 0) {
            firstNumber = parseInt(builder.toString());
        }
        return firstNumber >= 5;
    }

    private String buildAmapError(JsonNode root) {
        String info = safeText(root.path("info").asText(""));
        String infocode = safeText(root.path("infocode").asText(""));
        if (info.isEmpty() && infocode.isEmpty()) {
            return "\u9ad8\u5fb7\u5929\u6c14\u8bf7\u6c42\u5931\u8d25\uff0c\u8bf7\u7a0d\u540e\u91cd\u8bd5\u3002";
        }
        return "\u9ad8\u5fb7\u5929\u6c14\u8bf7\u6c42\u5931\u8d25\uff1a" + info + (infocode.isEmpty() ? "" : " (" + infocode + ")");
    }

    private boolean containsAny(String value, String... keywords) {
        for (String keyword : keywords) {
            if (value.contains(keyword)) {
                return true;
            }
        }
        return false;
    }

    private String formatLocation(Double longitude, Double latitude) {
        return String.format(Locale.US, "%.6f,%.6f", longitude, latitude);
    }

    private int parseInt(String value) {
        try {
            return Integer.parseInt(safeText(value));
        } catch (NumberFormatException ignored) {
            return 0;
        }
    }

    private String safeText(String value) {
        return value == null ? "" : value.trim();
    }
}
