import java.sql.*;

public class UserOperations {

    public static boolean addNewUser(String username, String password, String role) {
        String query = "";
        switch (role.toLowerCase()) {
            case "admin":
                query = "INSERT INTO admin (username, password) VALUES (?, ?)";
                break;
            case "seller":
                query = "INSERT INTO seller (username, password) VALUES (?, ?)";
                break;
            case "buyer":
                query = "INSERT INTO buyer (username, password) VALUES (?, ?)";
                break;
            default:
                System.out.println("Invalid role specified.");
                return false;
        }

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setString(1, username);
            stmt.setString(2, password);
            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                System.out.println("User added successfully.");
                return true;
            } else {
                System.out.println("User could not be added.");
                return false;
            }
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
            return false;
        }
    }
    
}
