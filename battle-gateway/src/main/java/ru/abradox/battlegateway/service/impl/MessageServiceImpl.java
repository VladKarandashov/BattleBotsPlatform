package ru.abradox.battlegateway.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import ru.abradox.battlegateway.service.MessageService;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final RabbitTemplate rabbitTemplate;

    @Override
    public boolean checkUser(String userName, UUID userId) {
        return true;
    }

    @Override
    public void handleUserMessage(UUID userId, String userMessage) {
        log.info("От пользователя {} получено сообщение {}", userId, userMessage);
        rabbitTemplate.convertAndSend("player-actions", "", userMessage);
    }
}
