package stock.game;


/**
*	Card class contains information of
*	an effect card.
*/

/**
 * THE CLASS SKELETON IS TAKEN FROM THE MODULE SITE
 */
public final class Card implements Comparable<Card> {


	public int effect;

	public Card(int effect) {
		this.effect = effect;
	}

	//Empty constructor for Jersey
	public Card()
	{

	}

	@Override
	public String toString() {
		return "" + effect;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + effect;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		return effect == ((Card) obj).effect;
	}

	@Override
	public int compareTo(Card other) {
		return Integer.compare(effect, other.effect); 
	}

	public static Card parse(String s) { 
		int effect = Integer.parseInt(s);
		return new Card(effect);
	}
}
