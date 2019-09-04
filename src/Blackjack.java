import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class Blackjack {
    public static void main(String[] args){
        System.out.println("*** CMD BLACKJACK ***");
        Game game = new Game();
    }
}

class Card {
    int num;
    String suit;

    Card(int num, String suit) {
        // note there is no check for legality!
        // nums allowed: 1-13, 1 = ace, 11 = jack, 12 = queen, 13 = king
        this.num = num;
        this.suit = suit;
    }

    Card(int num, int suit) {
        // note there is no check for legality!
        // nums allowed: 1-13, 1 = ace, 11 = jack, 12 = queen, 13 = king
        // suits: 1 = hearts, 2 = clubs, 3 = diamonds, 4 = spades
        this.num = num;
        switch (suit){
            case 1:
                this.suit="Hearts";
                break;
            case 2:
                this.suit="Clubs";
                break;
            case 3:
                this.suit="Diamonds";
                break;
            case 4:
                this.suit="Spades";
                break;
            default:
                System.out.println("error?");
                break;
        }
    }

    public void printCard(){
        System.out.println(toString());
    }

    public String toString(){
        String cardstring;
        switch (num) {
            case 1: //if ace
                cardstring=("Ace of " + suit);
                break;
            case 11: //if jack
                cardstring=("Jack of " + suit);
                break;
            case 12: //if queen
                cardstring=("Queen of " + suit);
                break;
            case 13: //if king
                cardstring=("King of " + suit);
                break;
            default: //if number
                cardstring=(num + " of " + suit);
                break;
        }
        return cardstring;
    }

    public int getValue() {
        switch (num) {
            case 1:
                return 11;
            case 11:
                return 10;
            case 12:
                return 10;
            case 13:
                return 10;
            default:
                return num;
        }
    }
}

class Deck {
    static int num_cards = 52;
    ArrayList<Card> cards_deck;

    Deck() {
        cards_deck = initDeck();
    }

    private ArrayList<Card> initDeck() {
        ArrayList<Card> deck_out = new ArrayList<Card>();
        for (int s = 1; s < 5; s++) {
            for (int n = 1; n < 14; n++) {
//                System.out.println(n + " " + s);
                Card card = new Card(n, s);
                deck_out.add(card);
            }
        }
        return deck_out;
    }

    public Card draw() {
        Random rng = new Random();
        int rnd_index = rng.nextInt(cards_deck.size());
        Card card_drawn = cards_deck.get(rnd_index);
        cards_deck.remove(rnd_index);
        System.out.println("Card drawn: "+card_drawn);
        return card_drawn;
    }

    public void showDeck() {
        System.out.println("Showing contents of deck...");
        System.out.println("Number of cards: " + cards_deck.size());
//        System.out.println(deck);
        for (int index = 0; index < cards_deck.size(); index++) {
            cards_deck.get(index).printCard();
        }
        System.out.println("");
    }

    public int getDeckSize() {
        System.out.println("Remaining nr of cards: " + cards_deck.size());
        return cards_deck.size();
    }

    public void shuffleDeck() {
        Random rng = new Random();
        ArrayList<Card> temp_cards_deck = new ArrayList<Card>();
        for(int i=0; i < num_cards-1; i++) {
            int ind = rng.nextInt(cards_deck.size() - 1);
            temp_cards_deck.add(cards_deck.get(ind));
            cards_deck.remove(ind);
        }
        temp_cards_deck.add(cards_deck.get(0)); //add final remaing card
        cards_deck = temp_cards_deck;
    }

    public void resetDeck() {
        cards_deck = initDeck();
    }
}

class Game {
    /**
     Game Object. Allows player to play blackjack. When reaching the end, game can reset itself.
     TO-DO: add score-keeping and gambling.
     */

    Deck deck;

    Game() {
        initGame();
    }

    void initGame(){
        // pre-game
        System.out.println("=== NEW GAME ===");
        deck = new Deck(); //create
        boolean gameover = false; //check var for determining gamestate

        // show deck?
        System.out.println("Show contents of deck? y/n");
        Scanner scan = new Scanner(System.in);
        String user_input = scan.nextLine();
        if(user_input.equals("yes") || user_input.equals("y")){
            deck.showDeck(); //show
        }

        deck.shuffleDeck(); //shuffle

        System.out.println("====== GAME START ======");

        Hand player_hand = new Hand("Player");
        Hand dealer_hand = new Hand("Dealer");

        // TO ADD: INITIAL BET HERE

        // first dealing
        System.out.println("Dealing Player.");
        player_hand.newCard(deck.draw());
        player_hand.newCard(deck.draw());
        player_hand.printHandValue();

        System.out.println("\nDealing Dealer.");
        dealer_hand.newCard(deck.draw());
        dealer_hand.printHandValue();

//        player_hand.value = 21;
        checkBlackjack(player_hand, dealer_hand);

        playerTurn(player_hand);
        dealerTurn(player_hand, dealer_hand);
    }

    String playerTurn(Hand player_hand) {
        System.out.println("\nOptions:\nhit (draw): h\npass (no more draws): p\nquit game: q");
        Scanner scan = new Scanner(System.in);
        String user_input = scan.nextLine();

        switch (user_input) {
            case "h":
                player_hand.newCard(deck.draw());
                checkBust(player_hand);

            case "p":
                System.out.println("Passing.");
                break;

            case "q":
                System.out.println("Goodbye!");
                exitGame();
        }
        return user_input;
    }

    void dealerTurn(Hand player_hand, Hand dealer_hand){
        System.out.println("Dealer's turn."); //fill hand until exceeds/equals 17
        while(dealer_hand.getHandValue() <17){
            dealer_hand.newCard(deck.draw());
        }
        checkDealerBust(dealer_hand);
    }

    void checkBlackjack(Hand player_hand, Hand dealer_hand){
        if(player_hand.getHandValue() == 21){
            if(dealer_hand.getHandValue() >= 10){
                System.out.println("\nYou've got Blackjack! The Dealer now has one draw to reach a stand-off.");
                System.out.println("\nDealing Dealer.");
                dealer_hand.newCard(deck.draw());
                if(dealer_hand.getHandValue() == 21){
                    System.out.println("Dealer also got Blackjack! Too bad, it's a stand-off.");
                }
                else{
                    System.out.println("Final Dealer hand value: "+dealer_hand.getHandValue());
                    System.out.println("\n*** BLACKJACK! YOU WIN ***");
                }
            }
            else{
                System.out.println("\n*** BLACKJACK! YOU WIN ***");
                //NEWGAME
            }
        }

        // TO DO: NEXT GAME??
    }

    void checkBust(Hand player_hand){
        if(player_hand.getHandValue()>21){
            System.out.println("\n*** HAND EXCEEDS 21! YOU LOSE ***");
            //NEWGAME
        }
    }

    void checkDealerBust(Hand dealer_hand){
        if(dealer_hand.getHandValue()>21){
            System.out.println("\n*** DEALER'S HAND EXCEEDS 21! YOU WIN! ***");
            //NEWGAME
        }
    }

    void exitGame(){
        System.out.println("Hope you had fun. Goodbye!");
        System.exit();
    }
}

class Hand {
    private int value = 0;
    private String name;

    Hand(String str){
        name = str;
    }

    void newCard(Card drawn_card){
        value += drawn_card.getValue();
    }

    void printHandValue(){
        System.out.println("Current value of " + name + " hand: "+value);
    }

    int getHandValue(){
        return value;
    }
}