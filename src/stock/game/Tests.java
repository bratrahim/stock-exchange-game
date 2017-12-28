package stock.game;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

public class Tests {
	
	// Sample round, see FAQ at https://orb.essex.ac.uk/ce/ce303/restricted/assign/ce303assignFaq2017.html 

	public Game game;

	// sample data
	public static Deck[] sampleDecks() {
		return new Deck[] { 
				new Deck(Stock.Apple, -20, 5, 10, -5, -10, 20), 
				new Deck(Stock.BP, 20, 5, 10, -5, -10, -20),
				new Deck(Stock.Cisco, 20, -5, 10, -20, -10, 5), 
				new Deck(Stock.Dell, 5, -20, 10, 20, -10, -5),
				new Deck(Stock.Ericsson, -10, 10, 20, 5, -20, -5) 
		};
	}

	public static int[][] sampleShares() {
		return new int[][] { 
			{ 3, 0, 1, 4, 2 }, 
			{ 2, 2, 5, 0, 1 },
			{ 4, 1, 0, 1, 4 }
		};
	}

	@Before
	public void setup() {
		game = new Game(Tests.sampleDecks(), Tests.sampleShares());
	}

	@Test
	public void tradeP0() {
		game.sell(0, Stock.Apple, 3);
		game.buy(0, Stock.Cisco, 6);
		Assert.assertArrayEquals(game.getShares(0), new int[] { 0, 0, 7, 4, 2 });
		Assert.assertEquals(game.getCash(0), 182);
	}

	@Test
	public void tradeP1() {
		game.buy(1, Stock.BP, 4);
		Assert.assertArrayEquals(game.getShares(1), new int[] { 2, 6, 5, 0, 1 });
		Assert.assertEquals(game.getCash(1), 88);
	}

	@Test
	public void tradeP2() {
		game.sell(2, Stock.Ericsson, 4);
		game.sell(2, Stock.Apple, 4);
		Assert.assertArrayEquals(game.getShares(2), new int[] { 0, 1, 0, 1, 0 });
		Assert.assertEquals(game.getCash(2), 1300);
	}

	@Test
	public void vote() {
		game.vote(0, Stock.Cisco, true);
		game.vote(0, Stock.Ericsson, false);
		game.vote(1, Stock.BP, true);
		game.vote(1, Stock.Cisco, true);
		game.vote(2, Stock.Apple, true);
		game.vote(2, Stock.BP, false);
		game.executeVotes();
		Assert.assertArrayEquals(game.getPrices(), new int[] { 80, 100, 120, 100, 100 });
		Card[] cards = new Card[] { new Card(5), new Card(20), new Card(-5), new Card(5), new Card(10) };
		Assert.assertArrayEquals(game.getCards(), cards);
	}

	@Test
	public void reachedLimitOfTransactions()
	{
		Assert.assertEquals(game.reachedLimitOfTransactions(1), false);
		game.sell(1, Stock.Cisco, 1);
		game.buy(1, Stock.Cisco, 1);
		Assert.assertEquals(game.reachedLimitOfTransactions(1), true);
	}

	@Test
	public void getRealPlayers()
	{
		Assert.assertTrue(game.getRealPlayers().equals(new ArrayList<>(Arrays.asList("0", "1","2"))));
	}

	@Test
	public void getPlayers()
	{
		Assert.assertTrue(game.getPlayers().equals(new ArrayList<>(Arrays.asList("0", "1","2"))));
	}
}
