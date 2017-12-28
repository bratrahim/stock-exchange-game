package stock.socket;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.Scanner;

import stock.Config;
import stock.game.Card;
import stock.game.Game;
import stock.game.Player;
import stock.game.Stock;

/**
* Service is a Runnable class, which keeps players session
*/

public class Service implements Runnable {
    private Scanner in;
    public PrintWriter out;
    private Game game;
    private Player player;
    private boolean login;

    //Initialization
    public Service(Game game, Socket socket) {
        this.game = game;
        login = false;
        try {
            in = new Scanner(socket.getInputStream());
            out = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }




    //Threads run method
    @Override
    public void run() {
        login();
        //Giving admin privileges to the first player
        if(player.getId() ==0)
        {
            try {
                //adding players to the game
                game.addPlayers(getInteger("Please enter number of players (1-4)",0,Config.MAX_NUMBER_OF_PLAYERS));
                //adding bots to the game
                game.addBots(getInteger("How many bots do you want to add?",0,Config.MAX_NUMBER_OF_BOTS));


                //echo round info and do the first move of bots
                out.println(game.getRoundInfo());
                game.botsMove();


                // empty line terminator
                out.println();
            } catch (Exception e) {
                out.println("error");
            }

        }
        //Echo current round info when a player connects
        else
            out.println(game.getRoundInfo());



        while (login) {
            //player's move
            try {
                Request request = Request.parse(in.nextLine());
                String response = execute(game, request);
                out.println(response + "\r\n");
                game.checkState();
            } catch (NoSuchElementException e) {
                login = false;
            }
        }
        logout();
    }


    //login function asks for player's id
    public void login() {
        out.println("Please enter your player id\n");
        out.println("Available : "+game.getRealPlayers());

        try {
            String input = in.nextLine().trim(); //TODO This is different on lab pc's
            if (game.getPlayers().contains(input)) {
                out.println("logging in");
                player = game.players.get(Integer.parseInt(input));
                out.println("Welcome player " + player.getId() + "!");
                System.out.println("Login: " + player.getId());
                login = true;
            } else {
                out.println("Invalid login attempt!");
            }
            //empty line terminator!
            out.println();
        } catch (NoSuchElementException e) {
        }
    }

    //logout function. Closes out stream and scanner
    public void logout() {
        if (player != null) {
            System.out.println("Logout: " + player.getId());
        }
        try {
            Thread.sleep(15000);
            in.close();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //Execute functio executes commands and returns feedback
    public String execute(Game game, Request request) {
        try {
            Stock stock;
            int amount;
            switch (request.type) {
                case PROFILE:
                    return "\r\nPlayer ID "+player.getId()+
                            "\r\nBalance\t\t" + game.getCash(player.getId())+
                            "\r\nMy shares \t"+player.getSharesMap().toString();
                case PRICES:
                    int[] prices= game.getPrices();
                    String output="STOCK PRICES";
                    for(int i=0;i<prices.length;i++)
                    {
                        output+="\r\n"+game.companies.get(i).getStock()+"\t"+prices[i];
                    }
                    output+="\r\nAlso there is a fee of 3 for every buy";
                    return output;
                case CARDS:
                    Card[] cards = game.getCards();
                    output="NEXT CARD EFFECTS";
                    for(int i=0;i<cards.length;i++)
                    {
                        output+="\r\n"+game.companies.get(i).getStock()+"\t"+cards[i].effect;
                    }
                    return output;
                case BUY:
                    try{
                        stock = Stock.parseFullString(request.params[0]);
                        amount = Integer.parseInt(request.params[1]);
                    }
                    catch (Exception e)
                    {
                        return "Stock name or number of units is incorrect";
                    }

                    return game.buy(player.getId(),stock,amount);
                case SELL:
                    try{
                        stock = Stock.parseFullString(request.params[0]);
                        amount = Integer.parseInt(request.params[1]);
                    }
                    catch (Exception e)
                    {
                        return "Stock name or number of units is incorrect";
                    }

                    return game.sell(player.getId(),stock,amount);
                case VOTE:
                    try{
                        stock = Stock.parseFullString(request.params[0]);
                    }
                    catch (Exception e)
                    {
                        return "Stock name is incorrect";
                    }
                    boolean yes=false;
                    if(request.params[1].toUpperCase().equals("YES") )
                    {
                        yes = true;
                    }
                    else if (request.params[1].toUpperCase().equals("NO"))
                    {
                        yes = false;
                    }
                    else return "Title of stock or vote is incorrect. Type YES/NO";

                    return game.vote(player.getId(),stock,yes);
                case SKIP:
                    game.skippingVoting(player.getId());
                    return "You have skipped voting. Wait until others finish voting";
                case HELP:
                    String help="\n\n*******************************************************\r\n";
                    help+="This is the stock simulator game based on Java Sockets\r\n";
                    help+="Available commands:\r\n\r\n";
                    help+="PROFILE\t\treturns information about you\r\n";
                    help+="PRICES\t\treturns current prices of stocks\r\n";
                    help+="CARDS\t\treturns cards, which can affect next round\r\n";
                    help+="BUY s n\t\tbuys an amount of n of s stocks\r\n";
                    help+="SELL s n\tsells an amount of n of s stocks\r\n";
                    help+="VOTE s YES/NO\tvotes yes/no for s stock\r\n";
                    help+="SKIP\t\tends your turn\r\n";
                    help+="help\t\techos available commands\r\n";
                    return help;
                default:
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }


    //get an integer from scanner
    private int getInteger(String message,int lower,int higher)
    {
        boolean validNum=false;
        String input="";
        while(!validNum)
        {
            out.println(message+"\n");
            input = in.nextLine().trim();
            try{
                if(Integer.parseInt(input)<=higher && Integer.parseInt(input)>=lower)
                {
                    return Integer.parseInt(input);
                }

            }
            catch (Exception e)
            {

            }

        }

        return -1;
    }

}
