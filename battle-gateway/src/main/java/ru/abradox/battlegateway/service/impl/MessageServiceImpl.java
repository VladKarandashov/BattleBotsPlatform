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
    public void handleUserMessage(UUID botToken, String userMessage) {
        log.info("От пользователя {} получено сообщение {}", botToken, userMessage);
        rabbitTemplate.convertAndSend("player-actions", "", userMessage);
    }
}
