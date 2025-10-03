package ru.t1.academy.client.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.t1.academy.client.dto.CardCreationRequest;
import ru.t1.academy.client.service.CardService;

@RestController
@RequestMapping("/api/cards")
@RequiredArgsConstructor
public class CardController {

    private final CardService cardService;

    @PostMapping
    public ResponseEntity<Void> createCard(@RequestBody CardCreationRequest request) {
        cardService.createCard(request);
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }
}
