package ru.t1.academy.client.service;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.t1.academy.client.dto.CardCreationRequest;
import ru.t1.academy.client.dto.kafka.ClientCardEvent;

@Service
@RequiredArgsConstructor
public class CardService {

    private final KafkaTemplate<String, ClientCardEvent> kafkaTemplate;

    private static final String CLIENT_CARDS_TOPIC = "client_cards";

    public void createCard(CardCreationRequest request) {
        ClientCardEvent event = ClientCardEvent.builder()
                .clientId(request.getClientId())
                .accountId(request.getAccountId())
                .cardType(request.getCardType())
                .build();

        kafkaTemplate.send(CLIENT_CARDS_TOPIC, event);
    }
}
