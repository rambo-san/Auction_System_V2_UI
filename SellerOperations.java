import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class SellerOperations {
    public static int login(String username, String password) {
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT * FROM seller WHERE username = ? AND password = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, username);
                stmt.setString(2, password);
                ResultSet rs = stmt.executeQuery();
                if(rs.next()) {
                   return rs.getInt("seller_id");
                } else {
                    return -1;
                }
            }
        } catch (Exception e) {
            System.out.println("Database error.");
        }
        return -1;
    }

    public static boolean signup(String username, String password) {
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "INSERT INTO seller (username, password) VALUES (?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, username);
                stmt.setString(2, password);
                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected > 0) {
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Database error or username already exists.");
        }
        return false;
    }
   
    
}
