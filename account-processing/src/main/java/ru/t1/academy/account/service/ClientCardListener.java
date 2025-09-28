package ru.t1.academy.account.service;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import ru.t1.academy.account.dto.kafka.ClientCardEvent;
import ru.t1.academy.account.model.Account;
import ru.t1.academy.account.model.Card;
import ru.t1.academy.account.model.enums.Status;
import ru.t1.academy.account.model.repository.AccountRepository;
import ru.t1.academy.account.model.repository.CardRepository;

import java.security.SecureRandom;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class ClientCardListener {

    private final AccountRepository accountRepository;
    private final CardRepository cardRepository;

    @KafkaListener(topics = "client_cards", groupId = "account-processing")
    public void handleClientCard(ClientCardEvent event) {
        // Найти счет клиента
        Account account = accountRepository.findById(event.getAccountId())
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));

        // Если счет заблокирован, не создаем карту
        if (account.getStatus() == Status.FROZEN || account.getStatus() == Status.ARRESTED) {
            return;
        }

        // Создаем карту
        Card card = Card.builder()
                .accountId(account.getId())
                .cardId(generateCardId()) // метод генерации уникального номера карты
                .paymentSystem(event.getCardType()) // или маппинг cardType → paymentSystem
                .status(Status.ACTIVE)
                .build();

        cardRepository.save(card);

        // Обновляем признак наличия карты у счета
        account.setCardExist(true);
        accountRepository.save(account);
    }

    private String generateCardId() {
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 12; i++) {
            sb.append(random.nextInt(10)); // цифра от 0 до 9
        }
        return sb.toString();
    }
}
