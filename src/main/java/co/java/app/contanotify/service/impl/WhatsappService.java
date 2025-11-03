package co.java.app.contanotify.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
public class WhatsappService {

    /*private final WebClient client;
    private final String phoneNumberId;
    private final String token;

    public WhatsappService(String phoneNumberId, String token) {
        this.phoneNumberId = phoneNumberId;
        this.token = token;
        this.client = WebClient.builder()
                .baseUrl("https://graph.facebook.com/v17.0/" + phoneNumberId)
                .defaultHeader("Authorization", "Bearer " + token)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

    public Mono<String> sendText(String to, String message) {
        var payload = Map.of(
                "messaging_product", "whatsapp",
                "to", to,
                "type", "text",
                "text", Map.of("body", message)
        );

        return client.post()
                .uri("/messages")
                .bodyValue(payload)
                .retrieve()
                .bodyToMono(String.class);
    }*/
}
