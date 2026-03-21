package com.campus.carbon.service.impl;

import com.campus.carbon.model.AiSuggest;
import com.campus.carbon.service.AiService;
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

    @Override
    public AiSuggest getCarbonSuggestion(String carbonFootprint) {
        OkHttpClient client = new OkHttpClient();

        MediaType mediaType = MediaType.parse("application/json");
        String requestBody = "{" +
                "\"model\": \"ep-20260320071354-kx7wx\","
                + "\"messages\": ["
                + "{\"role\": \"system\", \"content\": \"你是一个校园低碳生活顾问，根据用户的碳足迹情况，提供具体的减排建议。\"},"
                + "{\"role\": \"user\", \"content\": \"我的碳足迹是：" + carbonFootprint + "\"}"
                + "]}";

        Request request = new Request.Builder()
                .url(endpoint)
                .header("Authorization", "Bearer " + apiKey)
                .post(RequestBody.create(requestBody, mediaType))
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            String responseBody = response.body().string();
            // 解析响应，提取建议内容
            // 这里简化处理，实际需要根据百炼API的响应格式进行解析
            AiSuggest aiSuggest = new AiSuggest();
            aiSuggest.setCarbonFootprint(carbonFootprint);
            aiSuggest.setSuggestion(responseBody);
            return aiSuggest;
        } catch (IOException e) {
            e.printStackTrace();
            AiSuggest aiSuggest = new AiSuggest();
            aiSuggest.setCarbonFootprint(carbonFootprint);
            aiSuggest.setSuggestion("获取AI建议失败，请稍后重试");
            return aiSuggest;
        }
    }
}