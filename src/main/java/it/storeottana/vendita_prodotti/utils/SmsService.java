package it.storeottana.vendita_prodotti.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

import java.util.HashMap;
import java.util.Map;

@Service
public class SmsService {

    @Value("${bulkgate.api.url}")
    private String apiUrl;

    @Value("${bulkgate.api.application_id}")
    private String applicationId;

    @Value("${bulkgate.api.application_token}")
    private String applicationToken;

    private final RestTemplate restTemplate = new RestTemplate();

    public String sendSms(String recipient, String message) {
        // Costruzione del corpo della richiesta con Application ID e Token
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("application_id", applicationId);
        requestBody.put("application_token", applicationToken);
        requestBody.put("number", recipient);
        requestBody.put("text", message);
        requestBody.put("sender_id", "16385"); // Opzionale: personalizza il mittente

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.POST, request, String.class);

        return response.getBody();
    }
}
