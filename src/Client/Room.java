package Client;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.NodeOrientation;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import static Client.Controller.users;

public class Room extends Thread implements Initializable, MessageListener {
    @FXML
    public Label clientName;
    @FXML
    public Button chatBtn;
    @FXML
    public Pane chat;
    @FXML
    public TextField msgField;
    @FXML
    public TextArea msgRoom;
    @FXML
    public Label online;
    public Circle showProPic;
    ChatClient chatClient;
    public static String oldMessages = "";
    public static Map<String, String> oldMessagesMap = new HashMap<String, String>();

    public void connectSocket() {
        chatClient = UserList.client;
        chatClient.addMessageListener(this);
    }

    public void handleSendEvent(MouseEvent event) throws IOException {
        send();
    }

    public void send() throws IOException {
        String msg = msgField.getText();
        chatClient.msg(UserList.sendTo, msg);
        msgRoom.setNodeOrientation(NodeOrientation.LEFT_TO_RIGHT);
        msgRoom.appendText("Me: " + msg + "\n");
        oldMessages += "Me: " + msg + "\n";
        oldMessagesMap.put(UserList.sendTo, oldMessages);
        msgField.setText("");
        if ((msg.equalsIgnoreCase("offline"))) {
            System.exit(0);
        }
    }

    public void sendMessageByKey(KeyEvent event) throws IOException {
        if (event.getCode().toString().equals("ENTER")) {
            send();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        showProPic.setStroke(Color.valueOf("#90a4ae"));
        Image image;
        if (Controller.gender.equalsIgnoreCase("Male")) {
            image = new Image("icons/user.png", false);
        } else {
            image = new Image("icons/female.png", false);
        }
        showProPic.setFill(new ImagePattern(image));
        clientName.setText(UserList.sendTo + " (online)");
        connectSocket();
        msgRoom.clear();
        oldMessages = "";
        if (oldMessagesMap.containsKey(UserList.sendTo)) {
           msgRoom.appendText(oldMessagesMap.get(UserList.sendTo));
           oldMessages = oldMessagesMap.get(UserList.sendTo);
        }
    }

    @Override
    public void onMessage(String fromUser, String msgBody) {
        if (fromUser.equalsIgnoreCase(UserList.sendTo)) {
            String line = fromUser + ": " + msgBody;
            oldMessages += line + "\n";
            oldMessagesMap.put(UserList.sendTo, oldMessages);
            msgRoom.appendText(line + "\n");
            System.out.println("oldMessages: " + oldMessages);
        }
    }
}
