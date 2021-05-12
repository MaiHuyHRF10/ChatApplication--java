package Server;

import Client.Controller;
import Client.User;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;


public class ClientHandler extends Thread {

    private String userName;
    private Server server;
    private Socket clientSocket;
    private BufferedReader reader;
    private PrintWriter writer;

    public ClientHandler(Socket socket, Server server) {
        this.clientSocket = socket;
        this.server = server;
    }

    @Override
    public void run() {
        try {
            handleClientSocket();
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            try {
                reader.close();
                writer.close();
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private void handleClientSocket() throws IOException {
        this.reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        this.writer = new PrintWriter(clientSocket.getOutputStream(), true);


        String line;
        while ( (line = reader.readLine()) != null) {
            String [] tokens = line.split(" ");
            if (tokens != null && tokens.length > 0) {
                String cmd = tokens[0];
                if ("msg".equalsIgnoreCase(cmd)) {
                    String [] tokenMsg = line.split(" ", 3);
                    handleMessage(tokenMsg);
                } else if ("login".equalsIgnoreCase(cmd)) {
                    handleLogin(tokens);
                } else if ("logOut".equalsIgnoreCase(cmd)) {
                    handleLogOut();
                } else {
                    String msg = "unknow " + cmd;
                    writer.println(msg);
                }
            }
        }

        clientSocket.close();
    }

    private void handleLogOut() throws IOException {
        server.removeClientHandler(this);
        ArrayList<ClientHandler> listClients = server.getListClientsHandler();
        // Bao cho nhung client khac toi da offline
        String offlineMsg = "offline " + userName;
        for(ClientHandler cli : listClients) {
            if (!userName.equals(cli.getUserName())) {
                cli.writer.println(offlineMsg);
            }
        }
        clientSocket.close();
    }

    private void handleLogin(String[] tokens) {
        if (tokens.length == 3) {
            String userName = tokens[1];
            String password = tokens[2];

            if (checkLogin(userName, password)) {
                String msg = "ok login";
                this.writer.println(msg);
                this.userName = userName;
                System.out.println("User logged in succesfully: " + userName);

                ArrayList<ClientHandler> listClients = server.getListClientsHandler();

                // Thong bao cho client biet nhung client nao khac dang online:
                for(ClientHandler cli : listClients) {
                    if (cli.getUserName() != null) {
                        if (!userName.equals(cli.getUserName())) {
                            String msg2 = "online " + cli.getUserName();
                            this.writer.println(msg2);
                        }
                    }
                }

                // Thong bao cho cac client khac:
                String onlineMsg = "online " + userName;
                for(ClientHandler cli : listClients) {
                    if (!userName.equals(cli.getUserName())) {
                        cli.writer.println(onlineMsg);
                    }
                }
            } else {
                String msg = "error login";
                this.writer.println(msg);
                System.err.println("Login failed for " + userName);
            }
        }
    }

    private void handleMessage(String[] tokenMsg) {
        String sendTo = tokenMsg[1];
        String body = tokenMsg[2];

        ArrayList<ClientHandler> listClientsHandler = server.getListClientsHandler();

        for (ClientHandler cli : listClientsHandler) {
            if (sendTo.equalsIgnoreCase(cli.getUserName())) {
                String msg = "msg " + userName + " " + body;
                cli.writer.println(msg);
                break;
            }
        }
    }

    private boolean checkLogin(String username, String password) {
        for (User x : Controller.users) {
            if (x.getName().equalsIgnoreCase(username) && x.getPassword().equalsIgnoreCase(password)) {
                return true;
            }
        }
        return false;
    }
    private String getUserName() {
        return this.userName;
    }

}
