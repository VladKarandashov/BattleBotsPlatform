package ru.abradox.platformapi.cardgame.event;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum StatusCode {

    // 1xx - системные
    // 2xx - информирующие
    // 3xx - требующие действия
    // 4xx - ошибочные

    // 40x - ошибки поведения
    // 41x - ошибки атаки
    // 42x - ошибки защиты


    START_ROUND(201, "Начинается новый раунд, ход соперника"),
    WIN(202, "Вы выиграли, раунд завершён"),
    LOST(203, "Вы проиграли, раунд завершён"),
    DRAW(204, "Ничья, раунд завершён"),
    WAIT_OPPONENT_DEFEND(211, "Ждём когда соперник защитится"),
    WAIT_OPPONENT_ATTACK_AFTER_DISCARD(212, "Ждём когда соперник атакует, произошло бито"),
    WAIT_OPPONENT_ATTACK_AFTER_TAKE(213, "Ждём когда соперник атакует, произошло взятие"),

    START_ROUND_WITH_ACTIVE(301, "Начинается новый раунд, ваш ход"),
    NEED_DEFEND(311, "Необходимо защищаться"),
    NEED_ATTACK_AFTER_DISCARD(312, "Необходимо атаковать, произошло бито"),
    NEED_ATTACK_AFTER_TAKE(313, "Необходимо атаковать, произошло взятие"),

    INTERNAL_ERROR(400, "Произошла внезапная ошибка на сервере"),

    ROUND_NOT_FOUND(401, "Раунд не найден"),
    ROUND_ALREADY_FINISHED(402, "Раунд уже завершён"),
    NOT_YOUR_TURN(403, "Сейчас очередь другого игрока"),
    WRONG_ACTION_TYPE(404, "Неправильный тип хода (защита/атака)"),

    NO_CARDS(411, "Необходимо передать больше 0 своих карт"),
    TOO_MANY_CARDS_ON_TABLE(412, "На столе не может быть больше 6 карт"),
    TOO_MANY_CARDS_FOR_OPPONENT(413, "На столе не может быть больше карт, чем есть у защищающегося"),
    NOT_HAVE_CARDS(414, "Нельзя ходить картами, которых нет"),
    WRONG_CARDS(415, "Нельзя подкидывать карты с номерами, которых не было до этого на столе"),
    EMPTY_DISCARD(416, "Нельзя говорить БИТО, когда на столе нет карт"),
    ;

    private final int code;

    private final String message;


    @Override
    public String toString() {
        return this.code + ": " + this.message;
    }
}
