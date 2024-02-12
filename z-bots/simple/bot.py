import json

class Bot:
    
    def __init__(self):
        self.trash = []
        
    def handle(self, ws, message):
        event = json.loads(message)
        statusCode = int(event["statusCode"])
        message = event["message"]
        print(f"<- {statusCode} {message}")
        data = event["data"]
        if statusCode == 301 or statusCode == 312 or statusCode == 313:
            # атакуем
            cards = data["handCards"]
            
            response = {"roundId": data["id"],
                        "code": 1,
                        "cards": [cards[0]]}
        elif statusCode == 314:
            # необходимо подкинуть или сказать бито
            response = {"roundId": data["id"],
                        "code": 2}
        elif statusCode == 311:
            # необходимо защищаться
            cards = data["handCards"][0:6]
            table = data["table"]
            isPossible = can_beat_with_hand(cards, table)
            if not isPossible:
                response = {"roundId": data["id"],
                            "code": 4}
            else:
                response = {"roundId": data["id"],
                            "code": 3,
                            "cards": cards}
        else:
            return
        response = json.dumps(response)
        print(f"-> {response}")
        ws.send(response)
                

def can_beat_with_hand(cards, table):
    # Создаём множества для карт в руках и для карт на столе, которые надо побить
    hand_cards = set((card["number"], card["suit"]) for card in cards)
    table_cards_to_beat = set((table_card["placed"]["number"], table_card["placed"]["suit"]) 
                              for table_card in table 
                              if table_card["beaten"] is None)
    
    # Если нет карт на столе, которые надо побить, возвращаем True
    if not table_cards_to_beat:
        return True

    for table_card in table_cards_to_beat:
        table_card_number, table_card_suit = table_card
        # Ищем карту в руке, которой переменная table_card_number можно побить
        for hand_card in hand_cards:
            hand_card_number, hand_card_suit = hand_card
            if table_card_suit == hand_card_suit and hand_card_number > table_card_number:
                # Если мы нашли карту, которой можно побить карту с стола, удаляем её из множества руки
                hand_cards.remove(hand_card)
                break
        else:
            # Если мы не нашли подходящую карту в руке, значит, побить все карты на столе невозможно
            return False

    # Если мы нашли для каждой карты с стола бьющуюся и все они удалены, значит, побить можем
    return True
            
            
        
        
