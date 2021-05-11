package Client;

import javafx.scene.control.Control;

import java.sql.*;

public class ConnectDB {
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
        String sql = "INSERT INTO user(username, password, fullName, gender) VALUES ( ?, ?, ?, ?)";

        try {
            conn.setAutoCommit(false);
            PreparedStatement statement = conn.prepareStatement(sql);
            if (conn != null) {
                //statement.setInt(1, 20000);
                statement.setString(1, user.getName());
                statement.setString(2, user.getPassword());
                statement.setString(3, user.getFullName());
                statement.setString(4, user.getGender());
                int rowindex = statement.executeUpdate();
                conn.commit();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static String getStatus(String user) {
        var sql = "select * from user where username = ?";
        PreparedStatement statement = null;
        try {
            statement = conn.prepareStatement(sql);
            statement.setString(1, user);
            var resultSet = statement.executeQuery();
            if (!resultSet.next()) return null;
            String status = resultSet.getString("status");
            return status;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    public static void setStatus(String temp, String user) {
        String sql = "UPDATE user SET status = ? WHERE username = ?";

        try {
            conn.setAutoCommit(false);
            PreparedStatement statement = conn.prepareStatement(sql);
            if (conn != null) {
                //statement.setInt(1, 20000);
                statement.setString(1, temp);
                statement.setString(2, user);
                int rowindex = statement.executeUpdate();
                conn.commit();
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
                String fullname = resultSet.getString("fullName");
                String status = resultSet.getString("status");
                User newUser = new User();
                newUser.setName(username);
                newUser.setPassword(password);
                newUser.setGender(gender);
                newUser.setFullName(fullname);
                newUser.setStatus(status);
                Controller.users.add(newUser);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }
}
