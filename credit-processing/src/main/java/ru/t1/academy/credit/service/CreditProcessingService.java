package ru.t1.academy.credit.service;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import ru.t1.academy.credit.dto.kafka.ClientCreditProductEvent;
import ru.t1.academy.credit.model.PaymentRegistry;
import ru.t1.academy.credit.model.ProductRegistry;
import ru.t1.academy.credit.repository.PaymentRegistryRepository;
import ru.t1.academy.credit.repository.ProductRegistryRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CreditProcessingService {

    private final ProductRegistryRepository productRegistryRepository;
    private final PaymentRegistryRepository paymentRegistryRepository;

    private final BigDecimal LIMIT = BigDecimal.valueOf(1_500_000); // лимит N, можно вынести в конфиг

    @KafkaListener(topics = "client_credit_products", groupId = "credit-processing")
    public void handleCreditProduct(ClientCreditProductEvent event) {

        // Получаем все кредиты клиента
        List<ProductRegistry> existingProducts = productRegistryRepository.findByClientId(event.getClientId());

        BigDecimal totalExisting = existingProducts.stream()
                .map(p -> paymentRegistryRepository.sumDebtByProductId(p.getId()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal requestedAmount = event.getAmount();
        BigDecimal newTotal = totalExisting.add(requestedAmount);

        // TODO: проверка просрочек по существующим продуктам
        boolean hasOverdue = existingProducts.stream()
                .anyMatch(p -> paymentRegistryRepository.existsByProductRegistryAndExpiredTrue(p));

        if (newTotal.compareTo(LIMIT) > 0 || hasOverdue) {
            // отказ
            System.out.println("Кредит отклонен для clientId=" + event.getClientId());
            return;
        }

        // создаем продукт (ProductRegistry)
        ProductRegistry productRegistry = ProductRegistry.builder()
                .clientId(event.getClientId())
                .accountId(event.getAccountId())
                .productId(event.getProductId())
                .interestRate(event.getInterestRate())
                .openDate(event.getOpenDate())
                .monthCount(event.getMonthCount())
                .build();

        productRegistryRepository.save(productRegistry);

        // создаем график платежей
        generatePaymentSchedule(productRegistry, event.getAmount(), event.getInterestRate(), event.getMonthCount());
    }

    private void generatePaymentSchedule(ProductRegistry productRegistry, BigDecimal principal,
                                         int annualRate, int months) {

        BigDecimal monthlyRate = BigDecimal.valueOf(annualRate).divide(BigDecimal.valueOf(12 * 100), 10, BigDecimal.ROUND_HALF_UP);

        BigDecimal onePlusRPowerN = (BigDecimal.ONE.add(monthlyRate)).pow(months);
        BigDecimal annuity = principal.multiply(monthlyRate).multiply(onePlusRPowerN)
                .divide(onePlusRPowerN.subtract(BigDecimal.ONE), 2, BigDecimal.ROUND_HALF_UP);

        BigDecimal remainingDebt = principal;

        LocalDate paymentDate = productRegistry.getOpenDate().plusMonths(1);

        for (int i = 0; i < months; i++) {
            BigDecimal interest = remainingDebt.multiply(monthlyRate).setScale(2, BigDecimal.ROUND_HALF_UP);
            BigDecimal principalPayment = annuity.subtract(interest).setScale(2, BigDecimal.ROUND_HALF_UP);
            remainingDebt = remainingDebt.subtract(principalPayment);

            PaymentRegistry payment = PaymentRegistry.builder()
                    .productRegistry(productRegistry)
                    .paymentDate(paymentDate)
                    .amount(annuity)
                    .interestRateAmount(interest)
                    .debtAmount(principalPayment)
                    .expired(false)
                    .paymentExpirationDate(paymentDate)
                    .build();

            paymentRegistryRepository.save(payment);
            paymentDate = paymentDate.plusMonths(1);
        }
    }
}
