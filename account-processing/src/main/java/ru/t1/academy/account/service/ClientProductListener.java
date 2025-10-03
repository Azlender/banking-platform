package ru.t1.academy.account.service;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.t1.academy.account.dto.kafka.ClientProductEvent;
import ru.t1.academy.account.dto.kafka.ClientAccountEvent;
import ru.t1.academy.account.model.Account;
import ru.t1.academy.account.model.enums.Status;
import ru.t1.academy.account.model.repository.AccountRepository;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class ClientProductListener {

    private final AccountRepository accountRepository;
    private final KafkaTemplate<String, ClientAccountEvent> kafkaTemplate;

    private static final String CLIENT_ACCOUNTS_TOPIC = "client_accounts";

    @KafkaListener(topics = "client_products", groupId = "account-processing")
    public void handleClientProduct(ClientProductEvent event) {
        // Создаём аккаунт для клиента
        Account account = Account.builder()
                .clientId(event.getClientId())
                .productId(event.getProductId())
                .balance(BigDecimal.ZERO)       // начальный баланс
                .interestRate(0)
                .isRecalc(true)
                .cardExist(false)
                .status(Status.ACTIVE)
                .build();

        accountRepository.save(account);

        // Отправляем событие о созданном аккаунте
        ClientAccountEvent accountEvent = ClientAccountEvent.builder()
                .clientId(event.getClientId())
                .accountId(account.getId())
                .productId(account.getProductId())
                .balance(account.getBalance())
                .build();

        kafkaTemplate.send(CLIENT_ACCOUNTS_TOPIC, accountEvent);
    }
}
