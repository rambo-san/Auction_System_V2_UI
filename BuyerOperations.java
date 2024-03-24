import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Scanner;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class BuyerOperations {

    public static int login(String username, String password) {

        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT * FROM buyer WHERE username = ? AND password = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, username);
                stmt.setString(2, password);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    return rs.getInt("buyer_id");

                } else {
                        return -1;
                    }
            }
        } catch (Exception e) {
            
        }
        return -1;
    }

    public static boolean signup(String username, String password) {

        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "INSERT INTO buyer (username, password) VALUES (?, ?)";
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


    public static String placeBid(int buyerId, double bidAmount) {

                String response = "";
                try {
                    Socket socket = new Socket("localhost", 12345);
                    DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    // Create a BidCommand object to send to the server
                    String command = "BID " + buyerId +" " + bidAmount;
                    //Thread.sleep(2000);
                    // Send the bid command to the server
                    System.out.println("Sending bid to the server...");
                    outputStream.writeBytes(command + "\n");
                    outputStream.flush();

                    System.out.println("Bid placed successfully.");
                    response = in.readLine();
                    System.out.println(response);
                    socket.close();
                } catch (IOException e){
                    e.printStackTrace();
                    System.out.println("Error sending bid to the server.");
                    response = "Error sending bid to the server.";
                }
                return response;
        }

}
