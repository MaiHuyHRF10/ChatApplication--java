package Server;

import Client.ConnectDB;
import Client.Controller;
import Client.User;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server extends Thread {
    private int serverPort;
    private ArrayList<ClientHandler> clients = new ArrayList<ClientHandler>();

    public Server(int serverPort) {
        this.serverPort = serverPort;
    }

    @Override
    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(serverPort);
            while(true) {
                System.out.println("Waiting for clients...");
                Socket socket = serverSocket.accept();
                System.out.println("Connected");
                ClientHandler clientThread = new ClientHandler(socket, this);
                clients.add(clientThread);
                clientThread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        Server serverMain = new Server(8888);
        ConnectDB.connectDB();
        serverMain.start();
    }

    public ArrayList<ClientHandler> getListClientsHandler() {
        return this.clients;
    }

    public void removeClientHandler(ClientHandler clientHandler) {
        this.clients.remove(clientHandler);
    }
}
