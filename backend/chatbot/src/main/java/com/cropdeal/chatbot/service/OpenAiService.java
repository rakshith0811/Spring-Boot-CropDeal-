package com.cropdeal.chatbot.service;

import com.cropdeal.chatbot.dto.ChatResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.*;

@Service
public class OpenAiService {

    @Value("${openai.api.key}")
	protected String apiKey;

    protected static final String API_URL = "https://api.openai.com/v1/chat/completions";

    public ChatResponse getChatResponse(String userMessage) {
        RestTemplate restTemplate = new RestTemplate();

        Map<String, Object> message = Map.of("role", "user", "content", userMessage);
        Map<String, Object> body = Map.of(
                "model", "gpt-3.5-turbo",
                "messages", List.of(message)
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(apiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(API_URL, request, Map.class);

        List<Map<String, Object>> choices = (List<Map<String, Object>>) response.getBody().get("choices");
        Map<String, Object> messageResponse = (Map<String, Object>) choices.get(0).get("message");

        return new ChatResponse((String) messageResponse.get("content"));
    }
}
