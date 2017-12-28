package webserver;

import stock.game.Card;
import stock.game.Wrap;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;


/**
*	BotServer class describes Jersey server's
*	interfaces and contains bot decision making
*	logic
*/

@Path("/bot")
public class BotServer {
	public BotServer() {
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public String bot(Wrap wrap) {
		Card[] cards = wrap.getCards();
		int[] shares = wrap.getShares();
		int money = wrap.getMoney();
		int [] prices = wrap.getPrices();

		//get the card values
		int min = 100;
		int minIndex=0;
		int max = -100;
		int maxIndex=0;
		for(int i=0;i<cards.length;i++)
		{
			if(cards[i].effect<min){
				min=cards[i].effect;
				minIndex=i;
			}
			if(cards[i].effect>max){
				max=cards[i].effect;
				maxIndex=i;
			}
		}


		String commands = "";

		//sell the shares which will decrease the most
		int amount=shares[minIndex]/2;
		commands+="sell "+ minIndex + " "+amount+"\n";


		//buy the ones which are going to have the most positive effect
		amount=money/(prices[maxIndex]+3);
		commands+="buy "+maxIndex + " "+amount+"\n";




		//vote for the share which will increase
		commands+="vote "+maxIndex+" yes\n";
		commands+="vote "+minIndex+" yes\n";

		//return commands
		return commands;

	}



}