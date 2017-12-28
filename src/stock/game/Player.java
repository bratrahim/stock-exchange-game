package stock.game;

import stock.Config;

import java.util.Arrays;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

public class Player {
    private int money;
    private int id;
    private Map<Stock,Integer> shares;
    private boolean bot;
    public Player(int id)
    {
        money=Config.INITIAL_MONEY_PER_PLAYER;
        this.id=id;
        shares= generateShares();
        bot=false;
    }

    public Player(int id,boolean bot)
    {
        money=Config.INITIAL_MONEY_PER_PLAYER;
        this.id=id;
        this.bot=bot;
        shares= generateShares();
    }

    public Player(int id,int[] shares)
    {
        money=Config.INITIAL_MONEY_PER_PLAYER;
        this.id=id;
        this.shares= new TreeMap<>();
        for(int i=0;i<shares.length;i++)
        {
            this.shares.put(Stock.values()[i],shares[i]);
        }
        bot=false;
    }

    private TreeMap<Stock,Integer> generateShares()
    {
        int[] shares = new int[Config.INITIAL_AMOUNT_OF_SHARES_PER_PLAYER];
        TreeMap<Stock,Integer> result = new TreeMap<>();
        Random random = new Random();
        for(int i=0;i<shares.length;i++)
        {
            shares[i]=random.nextInt(Config.NUMBER_OF_STOCKS);
        }
        for(Stock stock:Stock.values())
        {
            result.put(stock,0);
        }
        for(int i:shares)
        {
            result.put(Stock.values()[i],result.get(Stock.values()[i])+1);
        }
        return result;
    }

    public int getMoney() {
        return money;
    }

    public void buying(int price,Stock stock,int amount) throws NotEnoughMoneyException
    {
        if(money<price) throw new NotEnoughMoneyException();
        money=money-price;
        shares.put(stock,shares.get(stock)+amount);
    }

    public void selling(int price,Stock stock,int amount) throws NotEnoughSharesException
    {
        if(shares.get(stock)<amount) throw new NotEnoughSharesException();
        money=money+price;
        shares.put(stock,shares.get(stock)-amount);
    }

    public int getId() {
        return id;
    }

    public int [] getShares()
    {
        return shares.values().stream().mapToInt(i->i).toArray();
    }

    public Map<Stock,Integer> getSharesMap()
    {
        return shares;
    }
    @Override
    public String toString() {
        return "\r\n\nPLAYER "+id+"\r\nMoney "+money+"\r\nShares "+ Arrays.toString(getShares());
    }

    public boolean isBot() {
        return bot;
    }
}

class NotEnoughMoneyException extends Exception
{
    NotEnoughMoneyException()
    {
        super("Player does not have enough money");
    }

}

class NotEnoughSharesException extends Exception
{
    NotEnoughSharesException()
    {
        super("Player does not have enough money");
    }

}
