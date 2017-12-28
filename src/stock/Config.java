package stock;

import stock.game.Stock;

public class Config {
    public static int NUMBER_OF_STOCKS = Stock.values().length;
    public static int INITIAL_AMOUNT_OF_SHARES_PER_PLAYER = 10;
    public static int INITIAL_MONEY_PER_PLAYER = 500;
    public static int INITIAL_PRICE_OF_STOCK = 100;
    public static int NUMBER_OF_ROUNDS = 5;

    public static final int[] EFFECTS = new int[] { -20, -10, -5, +5, +10, +20 };

    public static int MAX_NUMBER_OF_PLAYERS = 4;
    public static int MAX_NUMBER_OF_BOTS = 4;
}
