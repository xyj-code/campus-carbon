package com.campus.carbon.service.impl;

import com.campus.carbon.model.AiSuggest;
import com.campus.carbon.model.HealthData;
import com.campus.carbon.service.AiService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@Service
public class AiServiceImpl implements AiService {

    @Value("${carbon.ai.api-key}")
    private String apiKey;

    @Value("${carbon.ai.endpoint}")
    private String endpoint;

    private final OkHttpClient client = new OkHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public AiSuggest getCarbonSuggestion(String carbonFootprint) {
        AiSuggest aiSuggest = new AiSuggest();
        aiSuggest.setCarbonFootprint(carbonFootprint);

        try {
            // 构造请求体
            ObjectNode body = mapper.createObjectNode();
            body.put("model", "qwen-turbo");

            ArrayNode messages = mapper.createArrayNode();
            ObjectNode system = mapper.createObjectNode();
            system.put("role", "system");
            system.put("content", "你是一个低碳生活顾问，根据用户的碳足迹情况，提供具体的减排建议。请分条列出建议，每条建议简洁实用。");
            messages.add(system);

            ObjectNode user = mapper.createObjectNode();
            user.put("role", "user");
            user.put("content", "我的碳足迹情况：" + carbonFootprint);
            messages.add(user);

            body.set("messages", messages);

            RequestBody requestBody = RequestBody.create(
                    mapper.writeValueAsString(body),
                    MediaType.parse("application/json; charset=utf-8")
            );

            Request request = new Request.Builder()
                    .url(endpoint)
                    .header("Authorization", "Bearer " + apiKey)
                    .post(requestBody)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                String responseStr = response.body().string();
                if (!response.isSuccessful()) {
                    aiSuggest.setSuggestion("AI服务暂时不可用，请稍后重试。");
                    return aiSuggest;
                }
                // 提取 choices[0].message.content
                JsonNode root = mapper.readTree(responseStr);
                String content = root.path("choices").get(0).path("message").path("content").asText();
                aiSuggest.setSuggestion(content);
            }
        } catch (IOException e) {
            e.printStackTrace();
            aiSuggest.setSuggestion("获取AI建议失败，请检查网络后重试。");
        }

        return aiSuggest;
    }

    @Override
    public AiSuggest getHealthSuggestion(List<HealthData> healthDataList) {
        AiSuggest aiSuggest = new AiSuggest();

        try {
            // 构建健康数据摘要
            StringBuilder healthSummary = new StringBuilder();
            if (healthDataList != null && !healthDataList.isEmpty()) {
                // 取最新的健康数据
                HealthData latest = healthDataList.get(0);
                healthSummary.append("最新健康数据：");
                if (latest.getHeight() != null) {
                    healthSummary.append("身高").append(latest.getHeight()).append("cm，");
                }
                if (latest.getWeight() != null) {
                    healthSummary.append("体重").append(latest.getWeight()).append("kg，");
                }
                if (latest.getBmi() != null) {
                    healthSummary.append("BMI指数").append(latest.getBmi()).append("，");
                }
                if (latest.getBloodPressure() != null) {
                    healthSummary.append("血压").append(latest.getBloodPressure()).append("，");
                }
                if (latest.getHeartRate() != null) {
                    healthSummary.append("心率").append(latest.getHeartRate()).append("次/分，");
                }
                if (latest.getBloodSugar() != null) {
                    healthSummary.append("血糖").append(latest.getBloodSugar()).append("mmol/L，");
                }
                
                // 分析趋势
                if (healthDataList.size() >= 2) {
                    HealthData previous = healthDataList.get(1);
                    if (latest.getWeight() != null && previous.getWeight() != null) {
                        BigDecimal diff = latest.getWeight().subtract(previous.getWeight());
                        if (diff.compareTo(BigDecimal.ZERO) > 0) {
                            healthSummary.append("体重较上次上升").append(diff).append("kg，");
                        } else if (diff.compareTo(BigDecimal.ZERO) < 0) {
                            healthSummary.append("体重较上次下降").append(diff.abs()).append("kg，");
                        }
                    }
                }
            } else {
                healthSummary.append("暂无健康数据记录。");
            }

            // 构造请求体
            ObjectNode body = mapper.createObjectNode();
            body.put("model", "qwen-turbo");

            ArrayNode messages = mapper.createArrayNode();
            ObjectNode system = mapper.createObjectNode();
            system.put("role", "system");
            system.put("content", "你是一个专业的健康管理顾问，同时也是低碳生活倡导者。请根据用户的健康数据，提供个性化的健康生活建议，并将健康生活方式与低碳环保理念相结合。建议要具体、实用、可操作，分条列出，每条建议简洁明了。同时关注用户的身体健康和环境保护。");
            messages.add(system);

            ObjectNode user = mapper.createObjectNode();
            user.put("role", "user");
            user.put("content", "请根据我的健康数据给出今日健康建议。\n" + healthSummary.toString() + 
                    "\n请从以下几个方面给出建议：\n" +
                    "1. 饮食建议（推荐低碳环保的健康饮食）\n" +
                    "2. 运动建议（推荐绿色出行方式如步行、骑行）\n" +
                    "3. 生活习惯建议\n" +
                    "4. 心理健康建议");
            messages.add(user);

            body.set("messages", messages);

            RequestBody requestBody = RequestBody.create(
                    mapper.writeValueAsString(body),
                    MediaType.parse("application/json; charset=utf-8")
            );

            Request request = new Request.Builder()
                    .url(endpoint)
                    .header("Authorization", "Bearer " + apiKey)
                    .post(requestBody)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                String responseStr = response.body().string();
                if (!response.isSuccessful()) {
                    aiSuggest.setSuggestion("AI健康建议服务暂时不可用，请稍后重试。");
                    return aiSuggest;
                }
                // 提取 choices[0].message.content
                JsonNode root = mapper.readTree(responseStr);
                String content = root.path("choices").get(0).path("message").path("content").asText();
                aiSuggest.setSuggestion(content);
                aiSuggest.setCarbonFootprint(healthSummary.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
            aiSuggest.setSuggestion("获取AI健康建议失败，请检查网络后重试。");
        }

        return aiSuggest;
    }
}
