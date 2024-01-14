package ru.abradox.battlegateway.websocket;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class WsProducerTestService {

    private final MessageWebSocketHandler messageWebSocketHandler;

    @Async
    @Scheduled(fixedRate = 5 * 1000)
    public void start() {
        messageWebSocketHandler.getRandomUserId().ifPresentOrElse(userId -> {
            messageWebSocketHandler.sendMessageToUser(userId, "test message");
            log.info("Отправил сообщение");
        }, () -> log.info("Нет подключенного пользователя"));
    }
}