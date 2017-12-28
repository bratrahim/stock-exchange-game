package stock.socket;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import stock.game.Game;
import stock.game.Tests;


/**
 * Banking server using sockets
 */


public class Server {

    public static final int PORT = 8888;

    @SuppressWarnings("resource")
    public static void main(String[] args) throws IOException {
        Game game = new Game();
        ServerSocket server = new ServerSocket(PORT);
        System.out.println("Started Game server at port " + PORT);
        System.out.println("Waiting for clients to connect...");

        while (true) {
            Socket socket = server.accept();
            System.out.println("Client connected.");
            Service service = new Service(game, socket);
            game.services.add(service);
            new Thread(service).start();
        }
    }
}