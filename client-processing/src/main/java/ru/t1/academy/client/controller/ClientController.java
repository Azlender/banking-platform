package ru.t1.academy.client.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.t1.academy.client.dto.ClientRegistrationRequest;
import ru.t1.academy.client.dto.ClientRegistrationResponse;
import ru.t1.academy.client.service.ClientProcessingService;

@RestController
@RequestMapping("/api/clients")
@RequiredArgsConstructor
public class ClientController {

    private final ClientProcessingService clientProcessingService;

    @PostMapping
    public ResponseEntity<ClientRegistrationResponse> registerClient(
            @RequestBody ClientRegistrationRequest request) {
        try {
            ClientRegistrationResponse response = clientProcessingService.registerClient(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalStateException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }
}
