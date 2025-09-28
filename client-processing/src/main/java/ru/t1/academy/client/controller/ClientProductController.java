package ru.t1.academy.client.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.t1.academy.client.model.ClientProduct;
import ru.t1.academy.client.service.ClientProductService;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/client-products")
@RequiredArgsConstructor
public class ClientProductController {

    private final ClientProductService clientProductService;

    @PostMapping
    public ResponseEntity<ClientProduct> createClientProduct(
            @RequestParam String clientId,
            @RequestParam Long productId,
            @RequestParam LocalDate openDate
    ) {
        ClientProduct created = clientProductService.createClientProduct(clientId, productId, openDate);
        return ResponseEntity.ok(created);
    }

    @GetMapping
    public ResponseEntity<List<ClientProduct>> getAll() {
        return ResponseEntity.ok(clientProductService.getAllClientProducts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClientProduct> getById(@PathVariable Long id) {
        return clientProductService.getClientProduct(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        clientProductService.deleteClientProduct(id);
        return ResponseEntity.noContent().build();
    }
}
