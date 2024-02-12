# Раздел токенов

Сейчас этот раздел работает благодаря html-странице [tokens.html](../../middleware-service/src/main/resources/templates/tokens.html)

## Получение существующих токенов пользователя

### GET /middleware/api/v1/token (+ credentials)

Пример "максимального" ответа (в случае отсутствия токенов - data будет отсутствовать):

```json
{
  "statusCode": 0,
  "data": [
    {
      "id": "e72aacb8-f113-4216-b9e9-8eaf7f433d28",
      "userId": 1,
      "title": "vladDev1",
      "type": "DEV",
      "blocked": false
    },
    {
      "id": "183786a2-0a8f-4df5-921c-eeaf0cdd8ffa",
      "userId": 1,
      "title": "vladDev2",
      "type": "DEV",
      "blocked": false
    },
    {
      "id": "308af786-85de-4e78-884d-3d40d55ba743",
      "userId": 1,
      "title": "vladProd",
      "type": "PROD",
      "blocked": false
    }
  ]
}
```

Каждый токен состоит из:

* id - сам токен в формате uuid
* userId - шв пользователя в БД (можно не показывать)
* title - название бота
* type - тип бота = DEV или PROD
* blocked - признак заблокированности (если true - можно выделить)

Пример запроса:

```
fetch('/middleware/api/v1/token', {
    method: 'GET',
    credentials: "include"
})
    .then(response => {
        console.log(response.json())
    })
```

## Создание токена

### POST /middleware/api/v1/token (+ credentials)

Перед выполнением запроса необходимо посмотреть на текущее кол-во токенов.
Допустимо создать два DEV токена, и один PROD токен. 
Если пользователь хочет вылезти за эти лимиты - лучше сразу ему не позволить.

Тело запроса:

```json
{
  "title": "nagibator3000",
  "typeToken": "DEV"
}
```

Ответ на запрос простой statusCode вида:

```json
{
  "statusCode": 0,
  "message": "Текст для пользователя"
}
```

Возможны такие значение statusCode:
* 0 - токен успешно создан - необходимо запросить токены пользователя и обновить страницу
* 2011 - неуспешно - необходимо показать пользователю message, чтобы он понял в чём был неправ

Пример message (когда statusCode=2011):
* Максимальное количество токенов такого типа уже достигнуто
* Бот с таким именем уже зарегистрирован
