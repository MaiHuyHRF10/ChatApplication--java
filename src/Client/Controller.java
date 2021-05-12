package Client;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;


public class Controller {
    @FXML
    public Pane pnSignIn;
    @FXML
    public Pane pnSignUp;
    @FXML
    public Button btnSignUp;
    @FXML
    public Button getStarted;
    @FXML
    public ImageView btnBack;
    @FXML
    public TextField regName;
    @FXML
    public TextField regPass;
    //    @FXML
//    public TextField regEmail;
    @FXML
    public TextField regFulName;
//    @FXML
//    public TextField regPhoneNo;
    @FXML
    public RadioButton male;
    @FXML
    public RadioButton female;
    @FXML
    public Label controlRegLabel;
    @FXML
    public Label success;
    @FXML
    public Label goBack;
    @FXML
    public TextField userName;
    @FXML
    public TextField passWord;
    @FXML
    public Label loginNotifier;
    @FXML
    public Label nameExists;
    @FXML
    public Label checkEmail;
    public static String username, password, gender, fullName;
    public static ArrayList<User> loggedInUser = new ArrayList<>();
    public static ArrayList<User> users = new ArrayList<User>();

    public void registration() {
        if (!regName.getText().equalsIgnoreCase("") && !regFulName.getText().equalsIgnoreCase("")
                && !regPass.getText().equalsIgnoreCase("")
                && (male.isSelected() || female.isSelected())) {
            if (checkUser(regName.getText())) {
                User newUser = new User();
                newUser.setName(regName.getText());
                newUser.setPassword(regPass.getText());
                newUser.setFullName(regFulName.getText());
                if (male.isSelected()) {
                    newUser.setGender("Male");
                } else {
                    newUser.setGender("Female");
                }
                newUser.setStatus("offline");
                users.add(newUser);
                ConnectDB.addDatabase(newUser);
                ConnectDB.setStatus("offline", regName.getText());
                goBack.setOpacity(1);
                success.setOpacity(1);
                makeDefault();
                if (controlRegLabel.getOpacity() == 1) {
                    controlRegLabel.setOpacity(0);
                }
                if (nameExists.getOpacity() == 1) {
                    nameExists.setOpacity(0);
                }
            } else {
                nameExists.setOpacity(1);
                setOpacity(success, goBack, controlRegLabel);
            }
        } else {
            controlRegLabel.setOpacity(1);
            setOpacity(success, goBack, nameExists);
        }

    }

    private void setOpacity(Label a, Label b, Label c) {
        if (a.getOpacity() == 1 || b.getOpacity() == 1 || c.getOpacity() == 1) {
            a.setOpacity(0);
            b.setOpacity(0);
            c.setOpacity(0);
        }
    }


    private boolean checkUser(String username) {
        for (User user : users) {
            if (user.getName().equalsIgnoreCase(username)) {
                return false;
            }
        }
        return true;
    }

    private void makeDefault() {
        regName.setText("");
        regPass.setText("");
        regFulName.setText("");
        male.setSelected(true);
        controlRegLabel.setOpacity(0);
        nameExists.setOpacity(0);
    }


    public void login() {
        username = userName.getText();
        password = passWord.getText();
        boolean login = false;
        for (User x : users) {
            if (x.getName().equalsIgnoreCase(username) && x.getPassword().equalsIgnoreCase(password)) {
                login = true;
                loggedInUser.add(x);
                x.setStatus("online");
                ConnectDB.setStatus("online", username);
                gender = x.getGender();
                fullName = x.getFullName();
                break;
            }
        }
        if (login) {
            changeWindow();
        } else {
            loginNotifier.setOpacity(1);
        }
    }

    public void changeWindow() {
        try {
            Stage stage = (Stage) userName.getScene().getWindow();
            Parent root = FXMLLoader.load(this.getClass().getResource("UserList.fxml"));
//            stage.setScene(new Scene(root, 330, 560));
            stage.setScene(new Scene(root, 341, 468));
            stage.setTitle("Chat Application");
//            stage.setOnCloseRequest(event -> {
//                try {
//                    UserList.client.getSocket().getOutputStream().write(("offline " + username + "\n").getBytes());
//                    ConnectDB.setStatus("offline", username);
//                    System.exit(0);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//
//            });

            stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent e) {
                    System.out.println("User list closed!!");

                    // Send cmd to server side
                    UserList.client.off(username);

                    // Change the data in database
                    User logOutUser = null;
                    for (User x: loggedInUser)  {
                        if (x.getName().equals(username)) {
                            logOutUser = x;
                            break;
                        }
                    }
                    if (logOutUser != null) {
                        loggedInUser.remove(logOutUser);
                        logOutUser.setStatus("offline");
                        ConnectDB.setStatus("offline", username);
                    }
                    System.exit(0);
                }
            });
            stage.setResizable(false);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleButtonAction(ActionEvent event) {
        if (event.getSource().equals(btnSignUp)) {
            //new FadeIn(pnSignUp).play();
            pnSignUp.toFront();
        }
        if (event.getSource().equals(getStarted)) {
            // new FadeIn(pnSignIn).play();
            pnSignIn.toFront();
        }
        loginNotifier.setOpacity(0);
        userName.setText("");
        passWord.setText("");
    }

    @FXML
    private void handleMouseEvent(MouseEvent event) {
        if (event.getSource() == btnBack) {
            // new FadeIn(pnSignIn).play();
            pnSignIn.toFront();
        }
        regName.setText("");
        regPass.setText("");
    }
}
