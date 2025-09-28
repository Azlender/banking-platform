package ru.t1.academy.client.service;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.t1.academy.client.dto.kafka.ClientProductEvent;
import ru.t1.academy.client.model.Client;
import ru.t1.academy.client.model.ClientProduct;
import ru.t1.academy.client.model.Product;
import ru.t1.academy.client.model.enums.ClientProductStatus;
import ru.t1.academy.client.model.enums.ProductKey;
import ru.t1.academy.client.repository.ClientProductRepository;
import ru.t1.academy.client.repository.ClientRepository;
import ru.t1.academy.client.repository.ProductRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ClientProductService {

    private final ClientProductRepository clientProductRepository;
    private final ClientRepository clientRepository;
    private final ProductRepository productRepository;
    private final KafkaTemplate<String, ClientProductEvent> kafkaTemplate;

    private static final String CLIENT_PRODUCTS_TOPIC = "client_products";
    private static final String CLIENT_CREDIT_PRODUCTS_TOPIC = "client_credit_products";

    @Transactional
    public ClientProduct createClientProduct(String clientId, Long productDbId, LocalDate openDate) {
        Client client = clientRepository.findByClientId(Long.valueOf(clientId))
                .orElseThrow(() -> new IllegalArgumentException("Client not found"));

        Product product = productRepository.findById(productDbId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        // Создаем новый ClientProduct с ACTIVE статусом по умолчанию
        ClientProduct clientProduct = ClientProduct.builder()
                .client(client)
                .product(product)
                .openDate(openDate)
                .status(ClientProductStatus.ACTIVE)
                .build();

        clientProductRepository.save(clientProduct);

        sendKafkaEvent(clientProduct);

        return clientProduct;
    }

    private void sendKafkaEvent(ClientProduct clientProduct) {
        ProductKey key = clientProduct.getProduct().getKey();
        ClientProductEvent event = ClientProductEvent.builder()
                .clientId(clientProduct.getClient().getClientId())
                .productId(Long.valueOf(clientProduct.getProduct().getProductId()))
                .key(key)
                .openDate(clientProduct.getOpenDate())
                .closeDate(clientProduct.getCloseDate())
                .status(clientProduct.getStatus())
                .build();

        if (key == ProductKey.DC || key == ProductKey.CC || key == ProductKey.NS || key == ProductKey.PENS) {
            kafkaTemplate.send(CLIENT_PRODUCTS_TOPIC, event);
        } else if (key == ProductKey.IPO || key == ProductKey.PC || key == ProductKey.AC) {
            kafkaTemplate.send(CLIENT_CREDIT_PRODUCTS_TOPIC, event);
        }
    }

    public List<ClientProduct> getAllClientProducts() {
        return clientProductRepository.findAll();
    }

    public Optional<ClientProduct> getClientProduct(Long id) {
        return clientProductRepository.findById(id);
    }

    public void deleteClientProduct(Long id) {
        clientProductRepository.deleteById(id);
    }
}
