package stock.game;


import stock.Config;

import java.util.*;

/***
****	Deck is a stack of cards
 **/

/**
 * THE CLASS SKELETON IS TAKEN FROM THE MODULE SITE
 */

public class Deck {

	public Stock stock;
	public Stack<Card> cards;

	public Deck(Stock stock) {
		this.stock = stock;
		cards = new Stack<>();
		for (int effect : Config.EFFECTS) {
			cards.push(new Card(effect));
			Collections.shuffle(cards);
		}
	}

	public Deck(Stock stock, int... effects) {
		this.stock = stock;
		cards = new Stack<>();
		List<Integer> list = new ArrayList<>();
		for(Integer effect:effects)
		{
			list.add(effect);
		}
		Collections.reverse(list);
		for(int i=0;i<effects.length;i++)
		{
			effects[i]= list.get(i);
		}
		for (int effect : effects) {
			cards.push(new Card(effect));
		}
	}
	
	@Override
	public String toString() {
		return stock + " " + cards.toString();
	}

	//Check the upper card
	public Card exposeTop()
	{
		return cards.peek();
	}

	//Remove the upper card
	public Card removeTop()
	{
		return cards.pop();
	}

	public static void main(String[] args) {
		Deck[] decks = new Deck[Stock.values().length];
		for (Stock s : Stock.values()) {
			decks[s.ordinal()] = new Deck(s);
		}
		for (Deck d : decks) {
			System.out.println(d);
		}
	}

}
