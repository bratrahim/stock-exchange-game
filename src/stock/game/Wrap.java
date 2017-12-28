package stock.game;

import stock.Config;

/**
 * Wrap class is a container for request
 * sent to the Jersey server. It is transformed
 * into json
 */

public class Wrap
{
    public int money;
    public Card[] cards;
    public int[] shares;
    public int[] prices;
    public Wrap(int money, Card[] cards, int[] shares, int[] prices)
    {
        this.money=money;
        this.cards=cards;
        this.shares=shares;
        this.prices=prices;
    }

    //empty constructor for jersey
    public Wrap()
    {

    }

    public int getMoney() {
        return money;
    }

    public Card[] getCards() {
        return cards;
    }

    public int[] getShares() {
        return shares;
    }

    public int[] getPrices() {
        return prices;
    }
}