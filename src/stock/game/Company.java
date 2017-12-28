package stock.game;

import stock.Config;

/**
 * Company class contains all information about
 * stock, including stock name, current price
 * of stock, and a deck of effect cards.
 */
public class Company{
    private Stock stock;
    private int price;
    private Deck deck;

    public Company(Deck deck)
    {
        this.deck=deck;
        this.stock = deck.stock;
        price= Config.INITIAL_PRICE_OF_STOCK;
    }

    //Empty condtructor for Jersey
    public Company()
    {
        price= Config.INITIAL_PRICE_OF_STOCK;
    }





    //Setters and getters

    public Deck getDeck() {
        return deck;
    }

    public Stock getStock() {
        return stock;
    }

    public void adjust(int price)
    {
        this.price=this.price+price;
    }
    @Override
    public String toString() {
        return "\nCompany "+stock+"\nprice "+price+"\ndeck "+deck.toString();
    }

    public int getPrice() {
        return price;
    }
}
