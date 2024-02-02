import websocket
import argparse
from bot import Bot

bot = Bot()

def on_message(ws, message):
    #print(f"Получено сообщение от сервера: {message}")
    global bot
    bot.handle(ws, message)

def on_error(ws, error):
    print(f"При получении сообщения возникла ошибка: {error}")

def on_close(ws, close_status_code, close_msg):
    print(f"Соединение закрыто: {close_status_code} : {close_msg}")

def on_open(ws):
    print(f"Соединение открыто: {ws}")

if __name__ == "__main__":
    #websocket.enableTrace(True)
    
    p = argparse.ArgumentParser()
    p.add_argument('--name', required=True)
    p.add_argument('--token', required=True)

    args = p.parse_args()
    
    headers = {
        "botName": args.name,
        "botToken": args.token
    }
    ws = websocket.WebSocketApp("ws://147.45.107.29:8090/ws/battle",
                                header=headers,
                                on_open=on_open,
                                on_message=on_message,
                                on_error=on_error,
                                on_close=on_close)

    ws.run_forever()
