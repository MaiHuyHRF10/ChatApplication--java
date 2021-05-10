package Client;

import javafx.scene.control.Control;

import java.sql.*;

public class UserList {
    public static Connection conn;
    public static void connectDB() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            String url = "jdbc:mysql://localhost:3306/user_chat";// your db name
            String user = "root"; // your db username
            String password = ""; // your db password
            conn = DriverManager.getConnection(url, user, password);
            var sql = "select * from user";
            var statement = conn.prepareStatement(sql);
            var resultSet = statement.executeQuery();
            showResult(resultSet);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    public static void addDatabase(User user) {
        String sql = "INSERT INTO user(username, password, gender) VALUES ( ?, ?, ?)";

        try {
            conn.setAutoCommit(false);
            PreparedStatement statement = conn.prepareStatement(sql);
            if (conn != null) {
                //statement.setInt(1, 20000);
                statement.setString(1, user.getName());
                statement.setString(2, user.getPassword());
                statement.setString(3, user.getGender());
                int rowindex = statement.executeUpdate();
                conn.commit();
                System.out.println("insert ");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private static void showResult(ResultSet resultSet) {
        while (true) {
            try {
                if (!resultSet.next()) break;
                String username = resultSet.getString("username");
                String password = resultSet.getString("password");
                String gender = resultSet.getString("gender");
                User newUser = new User();
                newUser.name = username;
                newUser.password = password;
                newUser.gender = gender;
                Controller.users.add(newUser);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }
}
