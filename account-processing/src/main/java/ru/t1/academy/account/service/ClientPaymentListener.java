package ru.t1.academy.account.service;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import ru.t1.academy.account.dto.kafka.ClientPaymentEvent;
import ru.t1.academy.account.model.Account;
import ru.t1.academy.account.model.Payment;
import ru.t1.academy.account.model.repository.AccountRepository;
import ru.t1.academy.account.model.repository.PaymentRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ClientPaymentListener {

    private final AccountRepository accountRepository;
    private final PaymentRepository paymentRepository;

    @KafkaListener(topics = "client_payments", groupId = "account-processing")
    public void handleClientPayment(ClientPaymentEvent event) {
        // Получаем счет
        Account account = accountRepository.findById(event.getAccountId())
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));

        // Получаем все неоплаченные кредитные платежи по счету
        List<Payment> unpaidPayments = paymentRepository.findByAccountId(account.getId()).stream()
                .filter(p -> p.isCredit() && p.getPayedAt() == null)
                .toList();

        // Вычисляем суммарную задолженность
        BigDecimal totalDebt = unpaidPayments.stream()
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Проверяем, совпадает ли сумма платежа с задолженностью
        if (event.getAmount().compareTo(totalDebt) == 0) {
            // Списываем с баланса
            account.setBalance(account.getBalance().subtract(event.getAmount()));
            accountRepository.save(account);

            // Создаём новый платёж
            Payment newPayment = Payment.builder()
                    .accountId(account.getId())
                    .paymentDate(LocalDate.now())
                    .amount(event.getAmount())
                    .isCredit(true)
                    .type(event.getType())
                    .payedAt(LocalDateTime.now())
                    .build();
            paymentRepository.save(newPayment);

            // Обновляем существующие неоплаченные платежи
            for (Payment p : unpaidPayments) {
                p.setPayedAt(LocalDateTime.now());
                paymentRepository.save(p);
            }
        }
    }
}
