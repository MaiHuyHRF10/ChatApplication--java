package Client;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.*;

import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

public class UserList implements Initializable, UserStatusListener {

    @FXML
    ListView<String> username;

    @FXML
    Label user;

    public static String wordClick;
    public static String sendTo;
    public static String status;

    private Socket socket;

    public static ChatClient client;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        client = new ChatClient(8888, "localhost");
        client.connect();
        try {
            if (client.login(Controller.username, Controller.password)) {
                this.client.addUserStatusListener(this);
                socket = client.getSocket();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        user.setText(Controller.fullName);

        username.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                wordClick = username.getSelectionModel().getSelectedItem();
                if (wordClick != null) {
                    String[] temp = wordClick.split(" ", 1);
                    sendTo = temp[0];
                    changeWindow();
                }
            }
        });

    }

    private void changeWindow() {
        try {

//            Stage stage = (Stage) user.getScene().getWindow();
//            Parent root = FXMLLoader.load(this.getClass().getResource("Room.fxml"));
//            stage.setScene(new Scene(root, 330, 560));
//            stage.setTitle("Chat Application");
//            stage.setResizable(false);
//            stage.show();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("Room.fxml"));
            Parent root = (Parent) loader.load();
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Room chat");
            stage.setResizable(false);
            stage.show();

            status = ConnectDB.getStatus(sendTo);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void online(String userName) {
        username.getItems().add(userName);

         //Test them anh vao listview
        Image testImage = new Image("icons/man1.png");
        username.setCellFactory(username -> new ListCell<String>() {
            private ImageView imageView = new ImageView();
            @Override
            public void updateItem(String userName, boolean empty) {
                super.updateItem(userName, empty);
                if (empty) {
                    setText(null);
                    setGraphic(null);
                } else {
                    imageView.setImage(testImage);
                    setText(userName);
                    setGraphic(imageView);
                }
            }
        });
    }

    @Override
    public void offline(String userName) {
        Platform.runLater(() -> {
            username.getItems().remove(userName);
        });
    }
}
