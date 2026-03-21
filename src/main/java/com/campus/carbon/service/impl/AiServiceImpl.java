package com.campus.carbon.service.impl;

import com.campus.carbon.model.AiSuggest;
import com.campus.carbon.service.AiService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

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
            system.put("content", "你是一个校园低碳生活顾问，根据用户的碳足迹情况，提供具体的减排建议。请分条列出建议，每条建议简洁实用。");
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
}
