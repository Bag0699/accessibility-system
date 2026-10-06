package com.bag.accessibility_system.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import java.util.Map;
import java.util.List;
import java.util.HashMap;

@Service
public class TranscriptionTokenService {

    private final RestClient restClient;
    private final String apiKey;
    private final String projectId;

    public TranscriptionTokenService(
            RestClient.Builder restClientBuilder,
            @Value("${deepgram.api-key:}") String apiKey,
            @Value("${deepgram.project-id:}") String projectId) {
        this.restClient = restClientBuilder.baseUrl("https://api.deepgram.com").build();
        this.apiKey = apiKey;
        this.projectId = projectId;
    }

    public String generateEphemeralToken() {
        if (apiKey == null || apiKey.isEmpty() || projectId == null || projectId.isEmpty()) {
            throw new RuntimeException("Deepgram configuration is missing");
        }

        Map<String, Object> payload = new HashMap<>();
        payload.put("comment", "Temporary token for live transcription");
        payload.put("scopes", List.of("usage:write"));
        payload.put("time_to_live_in_seconds", 3600);

        try {
            Map response = restClient.post()
                    .uri("/v1/projects/{projectId}/keys", projectId)
                    .header("Authorization", "Token " + apiKey)
                    .header("Content-Type", "application/json")
                    .body(payload)
                    .retrieve()
                    .body(Map.class);

            if (response != null && response.containsKey("key")) {
                return (String) response.get("key");
            }
            throw new RuntimeException("Could not extract key from Deepgram response");
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate Deepgram token: " + e.getMessage(), e);
        }
    }
}
