package Client;

import java.io.*;
import java.net.Socket;

public class ChatClient {
    private int serverPort;
    private String serverName;
    private Socket socket;

    private InputStream serverIn;
    private OutputStream serverOut;
    private BufferedReader bufferedIn;

    private UserStatusListener userStatusListener = new UserStatusListener() {
        @Override
        public void online(String login) {
        }

        @Override
        public void offline(String login) {

        }
    };
    
    private MessageListener messageListener = new MessageListener() {
        @Override
        public void onMessage(String fromLogin, String msgBody) {
            
        }
    };


    public ChatClient(int serverPort, String serverName) {
        this.serverName = serverName;
        this.serverPort = serverPort;
    }

    public boolean login(String login, String password) throws IOException {
        String cmd = "login " + login + " " + password + "\n";
        serverOut.write(cmd.getBytes());

        String response = bufferedIn.readLine();
        System.out.println("Response Line:" + response);

        if ("ok login".equalsIgnoreCase(response)) {
            startMessageReader();
            return true;
        } else {
            return false;
        }
    }

    private void startMessageReader() {
        Thread t = new Thread() {
            @Override
            public void run() {
                readMessageLoop();
            }
        };
        t.start();
    }

    private void readMessageLoop() {
        try {
            String line;
            while ((line = bufferedIn.readLine()) != null) {
                String[] tokens = line.split(" ");
                if (tokens != null && tokens.length > 0) {
                    String cmd = tokens[0];
                    if ("online".equalsIgnoreCase(cmd)) {
                        handleOnline(tokens);
                    } else if ("offline".equalsIgnoreCase(cmd)) {
                        handleOffline(tokens);
                    } else if ("msg".equalsIgnoreCase(cmd)) {
                        String [] tokensMsg = line.split(" ", 3);
                        handleMessage(tokensMsg);
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void msg(String sendTo, String body) throws IOException {
        String msg = "msg " + sendTo + " " + body + "\n";
        serverOut.write(msg.getBytes());
    }

    public void off(String username) {
        String msg = "logout\n";
        try {
            serverOut.write(msg.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleMessage(String[] tokensMsg) {
        String sendTo = tokensMsg[1];
        String body = tokensMsg[2];

        messageListener.onMessage(sendTo, body);
    }

    private void handleOffline(String[] tokens) {
        String userName = tokens[1];
        userStatusListener.offline(userName);
    }

    private void handleOnline(String[] tokens) {
        String userName = tokens[1];
        userStatusListener.online(userName);
    }


    public boolean connect() {
        try {
            this.socket = new Socket(serverName, serverPort);
            System.out.println("Client port is " + socket.getLocalPort());
            this.serverOut = socket.getOutputStream();
            this.serverIn = socket.getInputStream();
            this.bufferedIn = new BufferedReader(new InputStreamReader(serverIn));
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void addUserStatusListener(UserStatusListener listener) {
        userStatusListener = listener;
    }

    public void addMessageListener(MessageListener listener) {
        messageListener = listener;
    }

    public Socket getSocket() {
        return this.socket;
    }

}
