import json
import time
from dataclasses import dataclass
from functools import partial, cmp_to_key

@dataclass
class Card:
    suit: int
    rank: int

    def is_trump(self, trump_suit: int) -> bool:
        return self.suit == trump_suit

    def equal_rank(self, card) -> bool:
        return card.rank == self.rank
    
    def __hash__(self):
        return hash(tuple([self.suit, self.rank]))


def compare_with_trumps(lhs: Card, rhs: Card, trump_suit: int):
    if lhs.is_trump(trump_suit) and not rhs.is_trump(trump_suit):
        return 1
    if not lhs.is_trump(trump_suit) and rhs.is_trump(trump_suit):
        return -1
    return lhs.rank - rhs.rank

def can_beat(trump_suit: int, down: Card, top: Card) -> bool:
    if down.suit == top.suit:
        return down.rank < top.rank
    return top.is_trump(trump_suit)

TOTAL = 36
IN_HAND = 6

class Bot:
    trump_suit: int
    hand_cards: set[Card]
    opponent_cards: set[Card]
    discards: set[Card]
    table_cards: set[Card]
    added: set[Card]
    sended_message: dict
    
    
    def __init__(self):
        self.trump_suit = -1
        self.hand_cards = set()
        self.opponent_cards = set()
        self.discards = set()
        self.table_cards = set()
        self.chosen = set()
        self.sended_message = {}
        
    def handle(self, ws, message):
        
        event = json.loads(message)
        status_code = int(event['statusCode'])
        print(event['message'])
        
        assert status_code < 400, "сервер вернул ошибку"

        if status_code == 112:
            time.sleep(0.7)
            ws.send(json.dumps(self.sended_message))
            return           
                    
        
        if status_code in [201, 301]:
            print()
            print()
            self.__init__()
            print(event['data']['id'])
        
        if 'data' not in event:
            return
        
        data = event['data']
        if not data['isNeedAction']:
            return

        assert self.trump_suit == -1 or self.trump_suit == int(data['lastCard']['suit']), "Козырь сменился"
        self.trump_suit = int(data['lastCard']['suit'])

        hand_cards = set([Card(card['suit'], card['number']) for card in data['handCards']])
        if not self.hand_cards.issubset(hand_cards):
            print(self.hand_cards)
            print(hand_cards)
        assert self.hand_cards.issubset(hand_cards), "Куда-то делись мои карты"
        self.hand_cards = hand_cards

        cards = []
        code = 0

        if status_code in [301, 312, 313]:
            assert len(data['table']) == 0, "Перед атакой на столе лежат карты"
            if status_code == 312:
                # соперник подтвердил 'бито'
                self.discards = self.discards.union(self.table_cards)
                    # часть из них принадлежала противнику
                    # self.opponent_cards = self.opponent_cards.difference(self.table_cards)
            elif status_code == 313:
                # соперник забрал карты себе
                self.opponent_cards = self.opponent_cards.union(self.table_cards)

            self.table_cards = set()
            
            cards = self.attack(int(data['numberOfCardsToAttack']))
            code = 1

        if status_code == 311:
            table_cards = set([Card(pair['placed']['suit'], pair['placed']['number']) for pair in data['table']] + \
                               [Card(pair['beaten']['suit'], pair['beaten']['number']) for pair in data['table'] if pair['beaten'] is not None])
            assert self.table_cards.issubset(table_cards), "Со стола пропали карты"
            self.table_cards = table_cards
            self.opponent_cards = self.opponent_cards.difference(self.table_cards)
            
            cards = self.beat([Card(pair['placed']['suit'], pair['placed']['number']) for pair in data['table'] if pair['beaten'] is None])
            code = 3 if len(cards) > 0 else 4

        if status_code == 314:
            table_cards = set([Card(pair['placed']['suit'], pair['placed']['number']) for pair in data['table']] + \
                              [Card(pair['beaten']['suit'], pair['beaten']['number']) for pair in data['table'] if pair['beaten'] is not None])
            assert self.table_cards.issubset(table_cards), "Со стола пропали карты"
            self.table_cards = table_cards
            self.opponent_cards = self.opponent_cards.difference(self.table_cards)
            
            cards = self.add(int(data['numberOfCardsToAttack']))
            code = 1 if len(cards) > 0 else 2

        # отдаю свои карты
        self.hand_cards = self.hand_cards.difference(cards)
        # добавляю их на стол
        self.table_cards = self.table_cards.union(cards)

        # сброшено в отбой
        if code == 2:
            self.discards = self.discards.union(self.table_cards)
              # часть из них принадлежала противнику
              # self.opponent_cards = self.opponent_cards.difference(self.table_cards)
            # на столе нет карт
            self.table_cards = set()
            
        # забираю все себе
        if code == 4:
            self.hand_cards = self.hand_cards.union(self.table_cards)
               # часть из них принадлежала противнику
               # self.opponent_cards = self.opponent_cards.difference(self.table_cards)
            # на столе нет карт
            self.table_cards = set()
            

        assert len(self.opponent_cards) <= data['opponentLeft'], "Я знаю слишком много карт"

        response = {'roundId': data['id'], 'code': code, 'cards': [{'suit': card.suit, 'number': card.rank} for card in cards]}
        self.sended_message = response
        ws.send(json.dumps(response))

    def attack(self, max_count) -> list[Card]:
        assert len(self.hand_cards) > 0, "Нет карт для атаки"
        return self.choose_min(list(self.hand_cards), max_count)

    def add(self, max_count) -> list[Card]:
        assert len(self.table_cards) > 0, "Не к чему подкидывать"
        
        possible = [card for card in self.hand_cards if card.rank in [card.rank for card in self.table_cards]]
        return self.choose_min(possible, max_count)

    def choose_min(self, cards: list[Card], max_count) -> list[Card]:
        if len(cards) == 0 or max_count == 0:
            return []

        self.sort_with_trumps(cards)

        count = 1
        while count < min(max_count, len(cards)) and cards[count].rank == cards[count - 1].rank:
            count += 1
                
        return cards[:count]

    def beat(self, placed) -> list[Card]:
 
        enabled = list(self.hand_cards)
        self.sort_with_trumps(enabled)
        self.sort_with_trumps(placed)

        assert len(enabled) >= len(placed), "Требуется отбить слишком много карт"

        chosen = []

        while len(placed) > 0:
            i = 0
            while i < len(enabled):
                if can_beat(self.trump_suit, down=placed[0], top=enabled[i]):
                    chosen.append(enabled.pop(i))
                    placed.pop(0)
                    break
                i += 1
            if i == len(enabled):
                return []

        return chosen
        
    def sort_with_trumps(self, cards):
        cards.sort(key=cmp_to_key(partial(compare_with_trumps, trump_suit=self.trump_suit)))


            
        
        
