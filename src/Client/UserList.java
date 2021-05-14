package Client;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

import static Client.Controller.users;

public class UserList implements Initializable, UserStatusListener {

    @FXML
    ListView<String> username;

    @FXML
    Label user;

    @FXML
    public Pane userlist;
    @FXML
    public Label fullName;
    @FXML
    public Label email;
    @FXML
    public Label gender;
    @FXML
    public Pane profile;
    @FXML
    public Button profileBtn;
    @FXML
    public TextField fileChoosePath;
    @FXML
    public ImageView proImage;
    @FXML
    public Circle showProPic;
    private FileChooser fileChooser;
    private File filePath;
    public boolean toggleChat = false, toggleProfile = false;
    private String imgAddress;

    public static String wordClick;
    public static String sendTo;
    public static String status;

    private Socket socket;

    public static ChatClient client;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        client = new ChatClient(8888, "localhost");
        client.connect();
        String path = ConnectDB.getImgAddress(Controller.username);
        Image image;
        if (path != null) {
            BufferedImage bufferedImage = null;
            try {
                bufferedImage = ImageIO.read(new File(path));
                image = SwingFXUtils.toFXImage(bufferedImage, null);
                showProPic.setFill(new ImagePattern(image));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            if (Controller.gender.equalsIgnoreCase("Male")) {
                image = new Image("icons/user.png", false);
            } else {
                image = new Image("icons/female.png", false);
                proImage.setImage(image);
            }
            showProPic.setFill(new ImagePattern(image));
        }
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
            stage.setTitle(Controller.username + "'s Room chat");
            stage.setResizable(false);
            stage.show();

            status = ConnectDB.getStatus(sendTo);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void online(String userName) {
        Platform.runLater(() -> {
            username.getItems().add(userName);
        });

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

    public boolean saveControl = false;

    public void chooseImageButton(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        fileChooser = new FileChooser();
        fileChooser.setTitle("Open Image");
        this.filePath = fileChooser.showOpenDialog(stage);
        imgAddress = filePath.getPath();
        fileChoosePath.setText(filePath.getPath());
        saveControl = true;
    }

    public void saveImage() {
        if (saveControl) {
            try {
                BufferedImage bufferedImage = ImageIO.read(filePath);
                Image image = SwingFXUtils.toFXImage(bufferedImage, null);
                proImage.setImage(image);
                showProPic.setFill(new ImagePattern(image));
                saveControl = false;
                fileChoosePath.setText("");
                ConnectDB.setImgAddress(imgAddress, Controller.username);
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
        }
    }

    public void handleProfileBtn(ActionEvent event) {
        if (event.getSource().equals(profileBtn) && !toggleProfile) {
            // new FadeIn(profile).play();
            profile.toFront();
            userlist.toBack();
            toggleProfile = true;
            toggleChat = false;
            profileBtn.setText("Back");
            setProfile();
        } else if (event.getSource().equals(profileBtn) && toggleProfile) {
            //new FadeIn(chat).play();
            userlist.toFront();
            toggleProfile = false;
            toggleChat = false;
            profileBtn.setText("Profile");
        }
    }

    public void setProfile() {
        for (User user : users) {
            if (Controller.username.equalsIgnoreCase(user.getName())) {
                fullName.setText(user.getFullName());
                fullName.setOpacity(1);
                email.setText(user.getEmail());
                email.setOpacity(1);
                gender.setText(user.getGender());
                gender.setOpacity(1);
            }
        }
    }
}
