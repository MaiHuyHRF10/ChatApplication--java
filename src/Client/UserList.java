package Client;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class UserList implements Initializable {

    @FXML
    ListView<String> username;

    @FXML
    Label user;

    public static String wordClick;
    public static String sendTo;
    public static String status;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        user.setText(Controller.fullName);
        for (User user : Controller.users) {
            if (!Controller.username.equalsIgnoreCase(user.getName())) {
                username.getItems().add(user.getName() + " (" + user.getFullName() + ")");
            }
        }
        username.setOnMouseClicked(event -> {
            wordClick = username.getSelectionModel().getSelectedItem();
            if (wordClick != null) {
                String[] temp = wordClick.split(" ", 2);
                sendTo = temp[0];
                changeWindow();
            }
        });
    }

    private void changeWindow() {
        try {
            Stage stage = (Stage) user.getScene().getWindow();
            Parent root = FXMLLoader.load(this.getClass().getResource("Room.fxml"));
            stage.setScene(new Scene(root, 330, 560));
            stage.setTitle("Chat Application");
            stage.setOnCloseRequest(event -> {
                ConnectDB.setStatus("offline", Controller.username);
                System.exit(0);
            });
            stage.setResizable(false);
            stage.show();
            status = ConnectDB.getStatus(sendTo);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
