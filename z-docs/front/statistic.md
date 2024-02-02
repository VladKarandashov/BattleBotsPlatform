# Раздел статистики

Сейчас этот раздел работает благодаря html-странице: [statistics.html](../../middleware-service/src/main/resources/templates/statistics.html)

## Получение статистики по текущему соревнованию

### GET /middleware/api/v1/competition

Ответ всегда приходит с statusCode=0 и data.

Пример "максимального" ответа:

```json
{
    "statusCode": 0,
    "data": {
        "id": 124,
        "bots": [
            {
                "title": "lera",
                "position": 1,
                "isActive": true
            },
            {
                "title": "vladProd",
                "position": 2,
                "isActive": true
            }
        ],
        "roundInfo": [
            {
                "downBotPosition": 2,
                "topBotPosition": 1,
                "downBotWinCount": 2,
                "topBotWinCount": 3,
                "drawCount": 0,
                "leftCount": 0,
                "isPlay": false
            }
        ]
    }
}
```

Пример минимального ответа:

```json
{
    "statusCode": 0,
    "data": {
        "id": 124,
        "bots": [],
        "roundInfo": []
    }
}
```

Из чего состоит ответ:
* data.id - номер текущего competition (competition - это крупное соревнование когда каждый бот играет N партий с верхним и N партий с нижним игроком)
* bots - рейтинг ботов - как они сейчас выстроены в таблице лидеров
* * bots.[i].title - имя бота 
* * bots.[i].position - на какой позиции в рейтинге в данный момент
* * bots.[i].isActive - активно ли сейчас соединение с этим ботом (если нет - он выключен - можно сделать сереньким)
* roundInfo - между соседями по рейтингу происходят несколько партий - если есть N ботов, то произойдёт 5*(N-1) партий. В этом массиве всегда будет на 1 меньше элементов, чем в bots.
* * roundInfo.[i].topBotPosition и downBotPosition - между какими позициями стоит roundInfo.[i]
* * roundInfo.[i].topBotWinCount и downBotWinCount - кол-ва выигрышей у каждого из играющих
* * roundInfo.[i].drawCount - количество ничьих
* * roundInfo.[i].leftCount - сколько партий из 5 осталось сыграть
* * roundInfo.[i].isPlay - играют ли эти два бота в данный момент

Получается схема отрисовки для примера выше будет:

lera _3__0__2_ vladProd

Таким образом получается что каждый roundInfo[i] размещается между своими ботами (основываясь на topBotPosition и downBotPosition) и описывает в каком состоянии находятся их общие партии 

## Получение статистики по прошедшим competition

### GET /middleware/api/v1/history

Ответ всегда приходит с statusCode=0 и data.

Пример "максимального" ответа (приходят последние 10 соревнований):

```json
{
    "statusCode": 0,
    "data": [
        {
            "id": 136,
            "botTitleByPosition": {
                "1": "lera",
                "2": "vladProd"
            },
            "result": [
                {
                    "downBotPosition": 2,
                    "topBotPosition": 1,
                    "downBotWinCount": 4,
                    "topBotWinCount": 1,
                    "isDownBotWin": true
                }
            ]
        },
        {
            "id": 135,
            "botTitleByPosition": {
                "1": "vladProd",
                "2": "lera"
            },
            "result": [
                {
                    "downBotPosition": 2,
                    "topBotPosition": 1,
                    "downBotWinCount": 4,
                    "topBotWinCount": 1,
                    "isDownBotWin": true
                }
            ]
        }
    ]
}
```

Минимально может прийти вот так:

```json
{
    "statusCode": 0,
    "data": []
}
```

По своей сути ответ очень похож по структуре на ответ о текущем соревновании, однако в более урезанном виде.

* data.[i].id - номер competition (в исторической таблице лучше выстраивать записи по порядку)
* data.[i].botTitleByPosition - как в этом competition были выстроены боты в его начале
* data.[i].result - как каждая пара соседей сыграла в этом competition (кол-во на 1 меньше чем ботов) - аналог roundInfo
* * data.[i].result.topBotPosition и downBotPosition - описывают для какой пары этот result
* * data.[i].result.topBotWinCount и downBotWinCount - сколько раз выиграл верхний и нижний игрок
* * data.[i].result.isDownBotWin - будет ли нижний игрок перемещён вверх (можно обвести зелёненьким, что он молодец)