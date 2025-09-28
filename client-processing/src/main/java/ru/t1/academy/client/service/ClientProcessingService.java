package ru.t1.academy.client.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.t1.academy.client.dto.ClientRegistrationRequest;
import ru.t1.academy.client.dto.ClientRegistrationResponse;
import ru.t1.academy.client.model.Client;
import ru.t1.academy.client.model.User;
import ru.t1.academy.client.model.enums.DocumentType;
import ru.t1.academy.client.repository.BlacklistRepository;
import ru.t1.academy.client.repository.ClientRepository;
import ru.t1.academy.client.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class ClientProcessingService {

    private final UserRepository userRepository;
    private final ClientRepository clientRepository;
    private final BlacklistRepository blacklistRepository;

    @Transactional
    public ClientRegistrationResponse registerClient(ClientRegistrationRequest request) {
        // 1. Проверка черного списка
        boolean blacklisted = blacklistRepository
                .existsByDocumentTypeAndDocumentId(request.getDocumentType(), request.getDocumentId());
        if (blacklisted) {
            throw new IllegalStateException("Документ находится в черном списке");
        }

        // 2. Создаем User
        User user = User.builder()
                .login(request.getLogin())
                .password(request.getPassword())
                .email(request.getEmail())
                .build();
        userRepository.save(user);

        // 3. Генерация clientId (пример формата XXFFNNNNNNNN)
        String clientId = generateClientId();

        // 4. Создаем Client
        Client client = Client.builder()
                .user(user)
                .clientId(Long.valueOf(clientId))
                .firstName(request.getFirstName())
                .middleName(request.getMiddleName())
                .lastName(request.getLastName())
                .dateOfBirth(request.getDateOfBirth())
                .documentType(DocumentType.valueOf(request.getDocumentType()))
                .documentId(request.getDocumentId())
                .documentPrefix(request.getDocumentPrefix())
                .documentSuffix(request.getDocumentSuffix())
                .build();
        clientRepository.save(client);

        // 5. Возвращаем ответ
        return ClientRegistrationResponse.builder()
                .userId(user.getId())
                .login(user.getLogin())
                .email(user.getEmail())
                .clientId(String.valueOf(client.getClientId()))
                .build();
    }

    private String generateClientId() {
        // XX - регион (18), FF - подразделение (00), NNNNNNNN - случайные цифры
        long randomSuffix = (long) (Math.random() * 1_000_000_00L);
        return String.format("1800%08d", randomSuffix);
    }
}
