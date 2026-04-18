package com.campus.carbon.service.impl;

import com.campus.carbon.model.AiSuggest;
import com.campus.carbon.model.HealthData;
import com.campus.carbon.service.AiService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class AiServiceImpl implements AiService {

    private static final String MODEL_NAME = "qwen-turbo";
    private static final MediaType JSON_MEDIA_TYPE = MediaType.parse("application/json; charset=utf-8");

    private static final String CARBON_SYSTEM_PROMPT =
            "\u4f60\u662f\u4e00\u4e2a\u6821\u56ed\u4f4e\u78b3\u751f\u6d3b\u987e\u95ee\u3002" +
            "\u8bf7\u6839\u636e\u7528\u6237\u63cf\u8ff0\u7ed9\u51fa\u6e05\u6670\u3001\u5177\u4f53\u3001\u53ef\u6267\u884c\u7684\u51cf\u78b3\u5efa\u8bae\u3002" +
            "\u4e25\u683c\u9075\u5b88\u4ee5\u4e0b\u8f93\u51fa\u8981\u6c42\uff1a" +
            "1.\u53ea\u8f93\u51fa\u7eaf\u6587\u672c\uff0c\u4e0d\u8981\u4f7f\u7528#\u3001*\u3001-\u3001Markdown\u6807\u9898\u3001\u52a0\u7c97\u6216\u5206\u5272\u7ebf\u3002" +
            "2.\u603b\u5171\u8f93\u51fa5\u884c\uff0c\u6bcf\u884c\u4e00\u6761\u3002" +
            "3.\u524d4\u884c\u5fc5\u987b\u5206\u522b\u4ee5\u201c\u884c\u52a8\u5efa\u8bae1\uff1a\u201d\u201c\u884c\u52a8\u5efa\u8bae2\uff1a\u201d\u201c\u884c\u52a8\u5efa\u8bae3\uff1a\u201d\u201c\u884c\u52a8\u5efa\u8bae4\uff1a\u201d\u5f00\u5934\u3002" +
            "4.\u7b2c5\u884c\u5fc5\u987b\u4ee5\u201c\u603b\u7ed3\uff1a\u201d\u5f00\u5934\u3002" +
            "5.\u8bed\u53e5\u7b80\u6d01\uff0c\u8d34\u5408\u5927\u5b66\u751f\u6821\u56ed\u573a\u666f\uff0c\u4e0d\u8981\u91cd\u590d\u7528\u6237\u539f\u8bdd\u3002";

    private static final String HEALTH_SYSTEM_PROMPT =
            "\u4f60\u662f\u4e00\u4e2a\u6821\u56ed\u5065\u5eb7\u751f\u6d3b\u987e\u95ee\uff0c\u540c\u65f6\u5173\u6ce8\u4f4e\u78b3\u751f\u6d3b\u65b9\u5f0f\u3002" +
            "\u8bf7\u6839\u636e\u7528\u6237\u5065\u5eb7\u6570\u636e\u7ed9\u51fa\u6bcf\u65e5\u5065\u5eb7\u5efa\u8bae\uff0c\u8981\u6c42\u6e05\u6670\u3001\u4fdd\u5b88\u3001\u53ef\u6267\u884c\uff0c\u4e0d\u505a\u75be\u75c5\u8bca\u65ad\u3002" +
            "\u4e25\u683c\u9075\u5b88\u4ee5\u4e0b\u8f93\u51fa\u8981\u6c42\uff1a" +
            "1.\u53ea\u8f93\u51fa\u7eaf\u6587\u672c\uff0c\u4e0d\u8981\u4f7f\u7528#\u3001*\u3001-\u3001Markdown\u6807\u9898\u3001\u52a0\u7c97\u6216\u5206\u5272\u7ebf\u3002" +
            "2.\u603b\u51714\u884c\uff0c\u6bcf\u884c\u4e00\u6761\u3002" +
            "3.\u56db\u884c\u5fc5\u987b\u5206\u522b\u4ee5\u201c\u996e\u98df\u5efa\u8bae\uff1a\u201d\u201c\u8fd0\u52a8\u5efa\u8bae\uff1a\u201d\u201c\u4f5c\u606f\u5efa\u8bae\uff1a\u201d\u201c\u751f\u6d3b\u5efa\u8bae\uff1a\u201d\u5f00\u5934\u3002" +
            "4.\u8fd0\u52a8\u5efa\u8bae\u4f18\u5148\u63a8\u8350\u6b65\u884c\u3001\u9a91\u884c\u3001\u62c9\u4f38\u7b49\u7eff\u8272\u65b9\u5f0f\u3002" +
            "5.\u6bcf\u884c1\u52302\u53e5\uff0c\u4e0d\u8981\u51fa\u73b0\u591a\u4f59\u524d\u7f00\u548c\u7f16\u53f7\u3002";

    private static final String CARBON_SERVICE_UNAVAILABLE =
            "\u0041\u0049\u670d\u52a1\u6682\u65f6\u4e0d\u53ef\u7528\uff0c\u8bf7\u7a0d\u540e\u91cd\u8bd5\u3002";
    private static final String CARBON_SERVICE_ERROR =
            "\u83b7\u53d6\u0041\u0049\u5efa\u8bae\u5931\u8d25\uff0c\u8bf7\u68c0\u67e5\u7f51\u7edc\u540e\u91cd\u8bd5\u3002";
    private static final String HEALTH_SERVICE_UNAVAILABLE =
            "\u0041\u0049\u5065\u5eb7\u5efa\u8bae\u670d\u52a1\u6682\u65f6\u4e0d\u53ef\u7528\uff0c\u8bf7\u7a0d\u540e\u91cd\u8bd5\u3002";
    private static final String HEALTH_SERVICE_ERROR =
            "\u83b7\u53d6\u0041\u0049\u5065\u5eb7\u5efa\u8bae\u5931\u8d25\uff0c\u8bf7\u68c0\u67e5\u7f51\u7edc\u540e\u91cd\u8bd5\u3002";

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
            ArrayNode messages = createMessages(
                    CARBON_SYSTEM_PROMPT,
                    "\u6211\u7684\u4f4e\u78b3\u884c\u4e3a\u60c5\u51b5\uff1a" + safeText(carbonFootprint)
            );
            String content = requestSuggestion(messages, CARBON_SERVICE_UNAVAILABLE);
            aiSuggest.setSuggestion(normalizeSuggestion(content));
        } catch (IOException e) {
            e.printStackTrace();
            aiSuggest.setSuggestion(CARBON_SERVICE_ERROR);
        }

        return aiSuggest;
    }

    @Override
    public AiSuggest getHealthSuggestion(List<HealthData> healthDataList) {
        AiSuggest aiSuggest = new AiSuggest();

        try {
            String healthSummary = buildHealthSummary(healthDataList);
            ArrayNode messages = createMessages(
                    HEALTH_SYSTEM_PROMPT,
                    "\u8bf7\u6839\u636e\u4ee5\u4e0b\u5065\u5eb7\u4fe1\u606f\uff0c\u7ed9\u51fa\u4eca\u65e5\u5065\u5eb7\u5efa\u8bae\uff1a\n" + healthSummary
            );
            String content = requestSuggestion(messages, HEALTH_SERVICE_UNAVAILABLE);
            aiSuggest.setSuggestion(normalizeSuggestion(content));
            aiSuggest.setCarbonFootprint(healthSummary);
        } catch (IOException e) {
            e.printStackTrace();
            aiSuggest.setSuggestion(HEALTH_SERVICE_ERROR);
        }

        return aiSuggest;
    }

    private ArrayNode createMessages(String systemPrompt, String userPrompt) {
        ArrayNode messages = mapper.createArrayNode();
        messages.add(createMessage("system", systemPrompt));
        messages.add(createMessage("user", userPrompt));
        return messages;
    }

    private ObjectNode createMessage(String role, String content) {
        ObjectNode message = mapper.createObjectNode();
        message.put("role", role);
        message.put("content", content);
        return message;
    }

    private String requestSuggestion(ArrayNode messages, String unavailableMessage) throws IOException {
        ObjectNode body = mapper.createObjectNode();
        body.put("model", MODEL_NAME);
        body.put("temperature", 0.3);
        body.put("top_p", 0.8);
        body.set("messages", messages);

        RequestBody requestBody = RequestBody.create(
                mapper.writeValueAsString(body),
                JSON_MEDIA_TYPE
        );

        Request request = new Request.Builder()
                .url(endpoint)
                .header("Authorization", "Bearer " + apiKey)
                .post(requestBody)
                .build();

        try (Response response = client.newCall(request).execute()) {
            ResponseBody responseBody = response.body();
            String responseText = responseBody == null ? "" : responseBody.string();
            if (!response.isSuccessful()) {
                return unavailableMessage;
            }

            JsonNode root = mapper.readTree(responseText);
            JsonNode contentNode = root.path("choices").path(0).path("message").path("content");
            String content = contentNode.asText("");
            if (content == null || content.trim().isEmpty()) {
                return unavailableMessage;
            }
            return content;
        }
    }

    private String buildHealthSummary(List<HealthData> healthDataList) {
        if (healthDataList == null || healthDataList.isEmpty()) {
            return "\u6682\u65e0\u5065\u5eb7\u6570\u636e\u8bb0\u5f55\uff0c\u8bf7\u7ed9\u51fa\u4fdd\u5b88\u3001\u65e5\u5e38\u3001\u5bb9\u6613\u6267\u884c\u7684\u5065\u5eb7\u5efa\u8bae\u3002";
        }

        HealthData latest = healthDataList.get(0);
        StringBuilder summary = new StringBuilder();
        summary.append("\u6700\u65b0\u5065\u5eb7\u6570\u636e\uff1a");

        appendMetric(summary, "\u8eab\u9ad8", latest.getHeight(), "cm");
        appendMetric(summary, "\u4f53\u91cd", latest.getWeight(), "kg");
        appendMetric(summary, "BMI", latest.getBmi(), "");
        appendMetric(summary, "\u8840\u538b", latest.getBloodPressure(), "");
        appendMetric(summary, "\u5fc3\u7387", latest.getHeartRate(), "\u6b21/\u5206");
        appendMetric(summary, "\u8840\u7cd6", latest.getBloodSugar(), "mmol/L");

        if (healthDataList.size() >= 2) {
            HealthData previous = healthDataList.get(1);
            if (latest.getWeight() != null && previous.getWeight() != null) {
                BigDecimal diff = latest.getWeight().subtract(previous.getWeight());
                if (diff.compareTo(BigDecimal.ZERO) > 0) {
                    summary.append("\u4f53\u91cd\u8f83\u4e0a\u6b21\u4e0a\u5347").append(diff).append("kg\u3002");
                } else if (diff.compareTo(BigDecimal.ZERO) < 0) {
                    summary.append("\u4f53\u91cd\u8f83\u4e0a\u6b21\u4e0b\u964d").append(diff.abs()).append("kg\u3002");
                }
            }
        }

        return summary.toString();
    }

    private void appendMetric(StringBuilder summary, String label, Object value, String unit) {
        if (value == null) {
            return;
        }
        summary.append(label).append(value).append(unit).append("\u3002");
    }

    private String normalizeSuggestion(String content) {
        if (content == null || content.trim().isEmpty()) {
            return "";
        }

        String normalized = content.replace("\r\n", "\n").replace('\r', '\n');
        String[] rawLines = normalized.split("\n");
        List<String> cleanedLines = new ArrayList<>();

        for (String rawLine : rawLines) {
            String line = cleanupLine(rawLine);
            if (!line.isEmpty()) {
                cleanedLines.add(line);
            }
        }

        if (cleanedLines.isEmpty()) {
            String compact = cleanupLine(normalized);
            return compact.isEmpty() ? "" : compact;
        }

        return String.join("\n", cleanedLines);
    }

    private String cleanupLine(String rawLine) {
        if (rawLine == null) {
            return "";
        }

        String line = rawLine.trim();
        if (line.isEmpty()) {
            return "";
        }
        if (line.matches("^[-*_#>`~\\s]+$")) {
            return "";
        }

        line = line.replaceAll("^#{1,6}\\s*", "");
        line = line.replaceAll("^[-*]+\\s*", "");
        line = line.replaceAll("^\\d+[.)]\\s*", "");
        line = line.replace("**", "");
        line = line.replace("__", "");
        line = line.replace("`", "");
        line = line.replace("#", "");
        line = line.replace("*", "");
        line = line.replaceAll("\\s{2,}", " ");
        return line.trim();
    }

    private String safeText(String text) {
        return text == null ? "" : text.trim();
    }
}
