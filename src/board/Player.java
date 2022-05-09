package board;
import cards.*;
import java.util.*;

public class Player {

    private Hand h;
    private Display d;
    private int score;
    private int handlimit;
    private int sticks;


    public Player() {
        d = new Display();
        h = new Hand();
        score = 0;
        handlimit = 8;
        sticks = 0;
        this.addCardtoDisplay(new Card(CardType.PAN, "pan"));
    }


    public int getScore () {
        return score;
    }
    public int getHandLimit () {
        return handlimit;
    }
    public int getStickNumber() {
        return sticks;
    }


    public void addSticks(int value) {
        for (int i = 0; i < value; i++) {
            this.addCardtoDisplay(new Stick());
        }
        sticks += value;
    }


    public void removeSticks(int value) {
        int copyValue = value;
        while (copyValue > 0) {
            for (int i = 0; i < copyValue; i++) {
                this.getDisplay().removeElement(i);
                break;
            }
            copyValue--;
        }
        sticks -= value;
    }


    public Hand getHand() {
        return h;
    }
    public Display getDisplay () {
        return d;
    }


    public void addCardtoHand(Card cardAddOne) {
        if (cardAddOne.getType().equals(CardType.BASKET)) {
            this.handlimit += 2;
                addCardtoDisplay(cardAddOne);    
        } else {
            h.add(cardAddOne);
            }
    }

    public void addCardtoDisplay(Card cardAddOne) {
        d.add(cardAddOne);
    }


    public boolean takeCardFromTheForest(int position)   {
        int realPosition;
        realPosition = Board.getForest().size() - position;
        Card needOne = Board.getForest().getElementAt(realPosition);

        int needSticks;
        if (position == 1 || position == 2) {
            needSticks = 0;
        } else if (position > 2 && position < 9) {
            needSticks = position - 2;
        } else {
            return false;
        }

        if (getHand().size() + 1 <= handlimit) {
            if (needSticks <= sticks){
                if (needOne.getType().equals(CardType.BASKET)) {
                    handlimit += 2;
                    Board.getForest().removeCard(realPosition);
                    addCardtoDisplay(needOne);
                    removeSticks(needSticks);
                    return true;
                } else {
                    Board.getForest().removeCard(realPosition);
                    addCardtoHand(needOne);
                    removeSticks(needSticks);
                    return true;
                }
            } else {
                return false;
            }
        }else {
            return false;
        }
    }

    
    public boolean takeFromDecay() {
        int basketNumber = 0;
        for (int i = 0; i < Board.getDecayPile().size(); i++) {
            if (Board.getDecayPile().get(i).getType().equals(CardType.BASKET)) {
                basketNumber += 1;
            }
        }

        int finalSizeIfNoBasket = getHand().size() + Board.getDecayPile().size() - basketNumber;
        int sizeAfterTakeDecay = handlimit + basketNumber * 2;
        if (finalSizeIfNoBasket <= sizeAfterTakeDecay) {
            int size = Board.getDecayPile().size();
            for (int j = 0; j < size; j++) {
                Card cardAddOne = Board.getDecayPile().get(j);
                addCardtoHand(cardAddOne);
            }
            Board.getDecayPile().clear();
            return true;
        }
        return false;
    }


    public boolean cookMushrooms(ArrayList<Card> cookingmushrooms) {
        ArrayList<Card> copyList = new ArrayList<>();
        for (int i = 0; i < cookingmushrooms.size(); i++) {
            copyList.add(cookingmushrooms.get(i));
        }

        int panNumber = 0;
        int displaySize = this.getDisplay().size();
        int cookMushroomListSize = cookingmushrooms.size();
        for (int i = 0; i < displaySize; i++) {
            boolean equals = this.getDisplay().getElementAt(i).getType().equals(CardType.PAN);
            if (equals) {
                panNumber++;
            }
        }
        for (int i = 0; i < cookMushroomListSize; i++) {
            boolean equals = cookingmushrooms.get(i).getType().equals(CardType.PAN);
            if (equals) {
                panNumber++;
            }
        }

        Iterator<Card> iterator = cookingmushrooms.iterator();
        while (iterator.hasNext()) {
            if (iterator.next().getType().equals(CardType.PAN)) {
                iterator.remove();
            }
        }
        if (panNumber == 0) {
            return false;
        }

        cookMushroomListSize = cookingmushrooms.size();

        for (int i = 0; i < cookMushroomListSize; i++) {
            if (cookingmushrooms.get(i).getType().equals(CardType.BASKET)) {
                return false;
            }
        }

        cookMushroomListSize = cookingmushrooms.size();

        Map<String, Integer> map = new HashMap<>();

        for (int i = 0; i < cookMushroomListSize; i++) {
            Card card = cookingmushrooms.get(i);
            if (map.containsKey(card.getName())) {
                if (card.getType().equals(CardType.NIGHTMUSHROOM)) {
                    map.put(card.getName(), map.get(card.getName()) + 2);
                } else if (card.getType().equals(CardType.DAYMUSHROOM)) {
                    map.put(card.getName(), map.get(card.getName()) + 1);
                }
            } else {
                if (card.getType().equals(CardType.NIGHTMUSHROOM)) {
                    map.put(card.getName(), 2);
                } else if (card.getType().equals(CardType.DAYMUSHROOM)) {
                    map.put(card.getName(), 1);
                }
            }
        }

        if (map.size() != 1) {
            return false;
        }

        String cardName = "";
        for (Map.Entry<String, Integer> entry: map.entrySet()) {
            cardName = entry.getKey();
        }

        Integer identicalMushroomsSize = map.get(cardName);

        if (identicalMushroomsSize < 3) {
            return false;
        }

        int butterSize = 0;
        int ciderSize = 0;
        for (int i = 0; i <cookMushroomListSize; i++) {
            CardType type = cookingmushrooms.get(i).getType();
            if (type.equals(CardType.BUTTER)) {
                butterSize++;
            }
            if (type.equals(CardType.CIDER)) {
                ciderSize++;
            }
        }
        if (identicalMushroomsSize - butterSize * 4 - ciderSize * 5 < 0) {
            return false;
        }

        EdibleItem identicalCard = null;
        for (int i = 0; i < cookMushroomListSize; i++) {
            Card card = cookingmushrooms.get(i);
            if (card.getName().equals(cardName)) {
                if (card instanceof EdibleItem) {
                    identicalCard = (EdibleItem) card;
                }
                break;
            }
        }
        int point = identicalCard.getFlavourPoints();
        if (identicalCard.getType().equals(CardType.NIGHTMUSHROOM)) {
            point = point / 2;
        }
        this.score += point * identicalMushroomsSize + butterSize * 3 + ciderSize * 5;


        ArrayList<Card> copyHandCardList = new ArrayList<>();
        for (int i = 0; i < this.getHand().size(); i++) {
            copyHandCardList.add(this.getHand().getElementAt(i));
        }


        Iterator<Card> copyHandCardListIter = copyHandCardList.iterator();
        while (copyHandCardListIter.hasNext()) {
            if (copyList.size() == 0) {
                break;
            }
            Card handCard = copyHandCardListIter.next();
            Iterator<Card> copyListIter = copyList.iterator();
            while (copyListIter.hasNext()) {
                Card cookCard = copyListIter.next();
                if (cookCard.getName().equals(handCard.getName()) && cookCard.getType().equals(handCard.getType())) {
                    copyHandCardListIter.remove();
                    copyListIter.remove();
                    break;
                }
            }
        }


        if (this.getHand().size() != 0) {
            int handSize = this.getHand().size();
            for (int i = 0; i < handSize; i++) {
                this.getHand().removeElement(0);
            }
            for (int i = 0; i < copyHandCardList.size(); i++) {
                this.getHand().add(copyHandCardList.get(i));
            }
        }
        return true;
    }


    public boolean sellMushrooms(String cardName, int mushroomNumbers) {
        if (cardName == null || cardName.isEmpty()) {
            return false;
        }
        if (mushroomNumbers < 2) {
            return false;
        }

        cardName = cardName.replaceAll("\\s", "");
        cardName = cardName.toLowerCase();
        
        int count = 0;
        for (int i = 0; i < this.getHand().size(); i++) {
            Card card = this.getHand().getElementAt(i);
            if (card.getName().equals(cardName)) {
                if (card.getType().equals(CardType.NIGHTMUSHROOM)) {
                    count += 2;
                } else {
                    count++;
                }
            }
        }
        if (count < 2) {
            return false;
        }
        if (count < mushroomNumbers) {
            return false;
        }
        // sell mushrooms
        int copyNumber = mushroomNumbers;
        int stickSum = 0;
        while (copyNumber > 0) {
            for (int i = 0; i < this.getHand().size(); i++) {
                Card card = this.getHand().getElementAt(i);
                if (card.getName().equals(cardName)) {
                    if (card instanceof Mushroom) {
                        Mushroom item = (Mushroom) card;
                        // add stick
                        stickSum += item.getSticksPerMushroom();
                        // remove card
                        this.getHand().removeElement(i);
                        // Take into account that night cards account for two mushrooms
                        if (item.getType().equals(CardType.NIGHTMUSHROOM)) {
                            copyNumber--;
                        }
                        break;
                    }
                }
            }
            copyNumber--;
        }
        this.sticks = this.sticks + stickSum;
        // add Stick to display
        for (int i = 0; i < stickSum; i++) {
            addCardtoDisplay(new Stick());
        }
        return true;
    }


   public boolean putPanDown() {
       int panNumber = 0;
       for (int i = 0; i < getHand().size(); i++) {
           if (getHand().getElementAt(i).getType().equals(CardType.PAN)) {
               panNumber += 1;
           }
       }
       if (panNumber == 0) {
           return false;
       } else {
           for (int i = 0; i < getHand().size(); i++) {
               if (getHand().getElementAt(i).getType().equals(CardType.PAN)) {
                   addCardtoDisplay(new Card(CardType.PAN, "pan"));
                   getHand().removeElement(i);
                   return true;
               }
           }
       }
       return false;    
   }


}
