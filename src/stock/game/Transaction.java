package stock.game;

/**
 * Transaction class keeps information
 * about a transaction. The game object
 * keeps an arraylist of transaction
 * objects of a current round
 */
public class Transaction
{
    private boolean buy;
    int playerId;
    public Transaction( int id,boolean buy)
    {
        this.buy = buy;
        this.playerId =id;
    }
}