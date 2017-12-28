package stock.game;

import org.glassfish.jersey.client.ClientConfig;
import stock.Config;
import stock.socket.Service;
import webserver.GsonMessageBodyHandler;

import javax.ws.rs.client.*;
import javax.ws.rs.core.MediaType;
import java.util.*;
import java.util.stream.Collectors;


/**
 * Game class contains all game logic, which
 * works like a black box.
 */

/**
 * THE CLASS SKELETON IS TAKEN FROM THE MODULE SITE
 */

public class Game {


	private List<Vote> votes;
	private List<Transaction> transactions;
	private int round;
	public List<Company> companies;
	public List<Player> players;
	public List<Service> services;

	// create a random game 
	public Game() {
		companies= new ArrayList<>();
		for(int i=0;i< Config.NUMBER_OF_STOCKS;i++)
		{
			companies.add(new Company(new Deck(Stock.values()[i])));
		}
		players = new ArrayList<>();
		votes = new ArrayList<>();
		transactions = new ArrayList<>();
		services = new ArrayList<>();
		round = 1;

		//add the player with admin privileges
		players.add(new Player(0));
	}

	public void addPlayers(int number)
	{
		if (number ==0)
		{
			players = new ArrayList<>();
			return;
		}
		for(int i=1;i<number;i++)
		{
			players.add(new Player(i));
		}
	}

	public void addBots(int number)
	{
		int size=this.getPlayers().size();
		for(int i=size;i<number+size;i++)
		{
			players.add(new Player(i,true));
			System.out.println("added bot");
		}
		System.out.println("INITIAL STATE OF THE GAME\r\n"+getRoundInfo());
	}


	// create a game with specific initial decks and share holdings
	// used for unit testing
	public Game(Deck[] decks, int[][] shares) {
		companies= new ArrayList<>();
		for(int i=0;i<decks.length;i++)
		{
			companies.add(new Company(decks[i]));
		}

		//saving shares to players
		players = new ArrayList<>();
		int numberOfPlayers=shares.length;
		int numberOfStocks= shares[0].length;


		for(int i=0;i<numberOfPlayers;i++)
		{
			int [] playersShares = new int[numberOfStocks];
			for(int j=0;j<numberOfStocks;j++)
			{
				playersShares[j]=shares[i][j];
			}
			players.add(new Player(i,playersShares));
		}
		votes = new ArrayList<>();
		transactions = new ArrayList<>();
		services = new ArrayList<>();
		round = 1;
	}

	//get balance of a player
	public int getCash(int playerId) {

		return players.get(playerId).getMoney();
	}

	//get shares of a player
	public int[] getShares(int playerId) {
		return players.get(playerId).getShares();
	}

	public int[] getPrices() {
		int [] prices = new int[Config.NUMBER_OF_STOCKS];
		for(int i=0;i<Config.NUMBER_OF_STOCKS;i++)
		{
			prices[i]=companies.get(i).getPrice();
		}
		return prices;
	}

	//get upper cards of all decks
	public Card[] getCards() {
		Card [] cards = new Card[Config.NUMBER_OF_STOCKS];
		for(int i=0;i<Config.NUMBER_OF_STOCKS;i++)
		{
			cards[i]=companies.get(i).getDeck().exposeTop();
		}
		return cards;
	}

	//get players
	public List<String> getPlayers()
	{
		List<String> players = new ArrayList<>();
		for(int i=0;i<this.players.size();i++)
		{
			players.add(Integer.toString(this.players.get(i).getId()));
		}
		return players;
	}

	//get only not bot players
	public List<String> getRealPlayers()
	{
		List<String> players = new ArrayList<>();
		for(int i=0;i<this.players.size();i++)
		{
			if(!this.players.get(i).isBot())
				players.add(Integer.toString(this.players.get(i).getId()));
		}
		return players;
	}


	//execute votes and start next round
	public void executeVotes() {
		Map<Stock,Integer> voteResults= new TreeMap<>();
		for(Stock stock:Stock.values())
		{
			voteResults.put(stock,0);
		}

		for(Vote vote:votes)
		{
			if(!vote.fake)
			{
				if(vote.yes)
				{
					voteResults.put(vote.s,voteResults.get(vote.s)+1);
				}
				else
				{
					voteResults.put(vote.s,voteResults.get(vote.s)-1);
				}
			}
		}
		for (Stock stock : voteResults.keySet()) {
			if(voteResults.get(stock)>0)
			{
				int effect = companies.get(Stock.getIndex(stock)).getDeck().removeTop().effect;
				companies.get(Stock.getIndex(stock)).adjust(effect);
			}
			else if(voteResults.get(stock)<0)
			{
				companies.get(Stock.getIndex(stock)).getDeck().removeTop();
			}
		}
		votes= new ArrayList<>();
		transactions = new ArrayList<>();
		round++;
		if(round>=Config.NUMBER_OF_ROUNDS)
		{
			finishGame();
			return;
		}
		for(Service service:services)
		{
			service.out.println(getRoundInfo());
		}
		botsMove();
	}
	public String getRoundInfo()
	{
		String out="";
		out+="\r\n***************************";
		out+="\r\nROUND "+round+" HAS STARTED";
		out+="\r\n***************************";

		int[] prices= getPrices();
		out+="\r\nCURRENT STOCK PRICES";
		for(int i=0;i<prices.length;i++)
		{
			out+="\r\n"+companies.get(i).getStock()+"\t"+prices[i];
		}
		out+="\r\nAlso there is a fee of 3 for every buy"+"\r\n\n";

		Card[] cards = getCards();
		out+="\r\nVISIBLE CARDS";
		for(int i=0;i<cards.length;i++)
		{
			out+="\r\n"+companies.get(i).getStock()+"\t"+cards[i].effect;
		}

		for(Player player:players)
			out+="\r\n\nPlayer ID "+player.getId()+
					"\r\nBalance\t\t" + getCash(player.getId())+
					"\r\nShares \t"+player.getSharesMap().toString();

		out+="\r\n********************************";
		out+="\r\nUse 'HELP' to get available commands";
		return out;

	}

	//terminate game and close all services
	private void finishGame()
	{
		HashMap<Player,Integer> total = new HashMap<>();

		for(int i=0;i<players.size();i++)
		{
			Player player = players.get(i);
			int money= player.getMoney();
			int[] shares = player.getShares();
			for(int j=0;j<Config.NUMBER_OF_STOCKS;j++)
			{
				money+=shares[j]*getPrices()[j];
			}
			total.put(player,money);
		}
		LinkedHashMap<Player,Integer> sorted =total.entrySet().stream()
				.sorted(Map.Entry.<Player, Integer>comparingByValue().reversed())
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
						(e1, e2) -> e1, LinkedHashMap::new));
		System.out.println("The game has finished");
		int place=1;
		for(Player player:sorted.keySet())
		{
			System.out.println(place+".\t Player "+player.getId()+"\t"+sorted.get(player)+"pts");
			place++;
		}
		for(Service service:services)
		{
			service.out.println("The game has finished");
			place=1;
			for(Player player:sorted.keySet())
			{
				service.out.println(place+".\t Player "+player.getId()+"\t"+sorted.get(player)+"pts");
				place++;
			}
		}
		for(Service service:services)
		{
			try{
				service.logout();
			}
			catch (Exception e)
			{

			}
		}

	}

	//check if a player still can do transactions
	public boolean reachedLimitOfTransactions(int id)
	{
		int occur=0;
		for(Transaction transaction:transactions)
		{
			if(transaction.playerId==id) occur++;
		}
		return occur > 1;
	}

	public synchronized String buy(int id, Stock s, int amount) {
		int stockPrice = companies.get(Stock.getIndex(s)).getPrice();
		int totalPrice = (stockPrice+3) * amount;
		Player player = players.get(id);

		//check if a player has not done
		//2 transactions
		if(reachedLimitOfTransactions(id))
			return "You can do only 2 transactions per round";


		//if player has enough money
		try
		{
			if(player.getMoney()>=totalPrice)
			{
				player.buying(totalPrice,s,amount);
				transactions.add(new Transaction(id,true));
				return "You have bought "+amount+" shares of "+s;

			}
		}
		catch (Exception e)
		{
			return "You do not have enough money";
		}
		return "You do not have enough money";
	}

	public synchronized String sell(int id, Stock s, int amount) {
		int stockPrice = companies.get(Stock.getIndex(s)).getPrice();
		int total = stockPrice * amount;
		Player player = players.get(id);


		//check if a player has not done
		//2 transactions
		if(reachedLimitOfTransactions(id))
			return "You can do only 2 transactions per round";

		//if player has enough money
		try
		{
			if(player.getShares()[Stock.getIndex(s)]>=amount)
			{
				player.selling(total,s,amount);
				transactions.add(new Transaction(id,false));
				return "You have sold "+amount+" shares of "+s;
			}
		}
		catch (Exception e)
		{
			return "You do not have enough shares of "+s;
		}
		return "You do not have enough shares of "+s;
	}

	public synchronized void skippingVoting(int id)
	{
		int occur=0;
		for(Vote vote:votes)
		{
			if(vote.id==id) occur++;
		}
		if(occur==0)
		{
			votes.add(new Vote(id));
			votes.add(new Vote(id));
			return;
		}
		if(occur==1)
		{
			votes.add(new Vote(id));
			return;
		}
	}
	public synchronized String vote(int id, Stock s, boolean yes) {
		int occur=0;
		boolean sameStock=false;
		for(Vote vote:votes)
		{
			if(vote.id==id) occur++;
			if(vote.id==id&&vote.s==s) sameStock=true;
		}
		if(occur>1) return "You cannot vote three times in one round. Wait until others finish voting";
		if(sameStock) return "You cannot vote twice for the same stock in one round";
		votes.add(new Vote(id,s,yes));
		return "You have successfully voted for "+s;
	}

	public void checkState()
	{
		if(votes.size()>=players.size()*2)
		{
			executeVotes();
		}

	}

	public synchronized void botsMove()
	{
		for(Player player:players)
		{
			System.out.println(player.getId());
			if(player.isBot())
			{
				try
				{
					ClientConfig config = new ClientConfig(GsonMessageBodyHandler.class);
					WebTarget target= ClientBuilder.newClient(config).target( "http://localhost:8080/api/rest/bot/");
					String string="";
					string = target.request().post(Entity.entity(new Wrap(player.getMoney(),getCards(),player.getShares(),getPrices()), MediaType.APPLICATION_JSON)).readEntity(String.class);
					//System.out.println(string);

					String[] commands = string.split("\n");
					for(String s:commands)
					{
						String[] arguments = s.split(" ");
						if(arguments[0].equals("buy"))
						{
							System.out.println(buy(player.getId(),Stock.values()[Integer.parseInt(arguments[1])],Integer.parseInt(arguments[2])));

						}
						else if(arguments[0].equals("sell"))
						{
							System.out.println(sell(player.getId(),Stock.values()[Integer.parseInt(arguments[1])],Integer.parseInt(arguments[2])));
						}
						else if (arguments[0].equals("vote"))
						{
							if(arguments[2].equals("yes"))
								System.out.println(vote(player.getId(),Stock.values()[Integer.parseInt(arguments[1])],true));
							else
								System.out.println(vote(player.getId(),Stock.values()[Integer.parseInt(arguments[1])],false));
						}
						else skippingVoting(player.getId());
					}
				}
				catch (NoClassDefFoundError | IllegalArgumentException e)
				{
				}
			}
		}
		System.out.println(getRoundInfo());
	}

	public static void main(String [] args)
	{
		Game game = new Game(Tests.sampleDecks(),Tests.sampleShares());
		game.addBots(5);
		//game.botsMove();
	}

}




