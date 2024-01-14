package ru.abradox.battlegateway.service.impl;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.abradox.battlegateway.service.MessageService;

import java.util.UUID;

@Slf4j
@Service
@NoArgsConstructor
public class MessageServiceImpl implements MessageService {


    @Override
    public boolean checkUser(String userName, UUID userId) {
        return true;
    }

    @Override
    public void handleUserMessage(UUID userId, String userMessage) {
        log.info("От пользователя {} получено сообщение {}", userId, userMessage);
    }
}
