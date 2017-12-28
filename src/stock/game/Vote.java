package stock.game;

/**
 * Vote class is a blueprint of a player's
 * vote
 */
public class Vote
{
    int id;
    Stock s;
    boolean yes;
    boolean fake =false;

    public Vote(int id,Stock s, boolean yes)
    {
        this.id= id;
        this.s = s;
        this.yes = yes;
    }
    public Vote(int id)
    {
        this.id=id;
        fake=true;
    }
}
