package ru.abradox.battlegateway.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import ru.abradox.battlegateway.service.MessageService;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final RabbitTemplate rabbitTemplate;

    @Override
    public boolean checkUser(String botName, String botToken) {
        if (StringUtils.isBlank(botName)) return false;
        if (StringUtils.isBlank(botToken)) return false;
        var tokenOpt = parseUUID(botToken);
        if (tokenOpt.isEmpty()) return false;
        // TODO сделать запрос в сервис токенов с кэшем
        return true;
    }

    @Override
    public void handleUserMessage(UUID botToken, String userMessage) {
        log.info("От пользователя {} получено сообщение {}", botToken, userMessage);
        rabbitTemplate.convertAndSend("player-actions", "", userMessage);
    }

    private Optional<UUID> parseUUID(String str) {
        try {
            return Optional.of(UUID.fromString(str));
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
