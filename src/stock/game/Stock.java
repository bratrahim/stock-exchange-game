package stock.game;

/**
 * THE CLASS IS TAKEN FROM THE MODULE SITE
 */

public enum Stock {
	Apple("A"), BP("B"), Cisco("C"), Dell("D"), Ericsson("E");

	Stock(String symbol) { 
		this.symbol = symbol;
	}

	public final String symbol;

	public static Stock parse(char c) {
		switch (Character.toUpperCase(c)) {
		case 'A':
			return Apple;
		case 'B':
			return BP;
		case 'C':
			return Cisco;
		case 'D':
			return Dell;
		case 'E':
			return Ericsson;
		}
		throw new RuntimeException("Stock parsing failed"); 
	}

	public static int getIndex(Stock stock)
	{
		if(stock.equals(Apple)) return 0;
		if(stock.equals(BP)) return 1;
		if(stock.equals(Cisco)) return 2;
		if(stock.equals(Dell)) return 3;
		else return 4;
	}

	public static Stock parse(String s) {
		return parse(s.charAt(0));
	}

	public static Stock parseFullString(String s) {
		switch (s.toUpperCase()) {
			case "APPLE":
				return Apple;
			case "BP":
				return BP;
			case "CISCO":
				return Cisco;
			case "DELL":
				return Dell;
			case "ERICSSON":
				return Ericsson;
		}
		throw new RuntimeException("Stock parsing failed");
	}
}
