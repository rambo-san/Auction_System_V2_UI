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

    public static void login(Scanner scanner) {
        System.out.println("Buyer Login");
        System.out.print("Username: ");
        String username = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();

        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT * FROM buyer WHERE username = ? AND password = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, username);
                stmt.setString(2, password);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    System.out.println("Login successful.");
                    buyerMenu(scanner, rs.getInt("buyer_id"));

                } else {
                    System.out.println("Login failed. Incorrect username or password.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Database error.");
        }
    }

    public static void signup(Scanner scanner) {
        System.out.println("Buyer Signup");
        System.out.print("Choose a username: ");
        String username = scanner.nextLine();
        System.out.print("Choose a password: ");
        String password = scanner.nextLine();

        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "INSERT INTO buyer (username, password) VALUES (?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, username);
                stmt.setString(2, password);
                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Signup successful. Please log in.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Database error or username already exists.");
        }
    }

    public static void buyerMenu(Scanner scanner, int buyerId) {
        

        System.out.println("Welcome to the Buyer Dashboard!");
        boolean loggedIn = true;
        while (loggedIn) {
            System.out.println("1. Place a bid");
            System.out.println("2. View my won auctions");
            System.out.println("3. Logout");

            System.out.print("Choose an option: ");
            String option = scanner.nextLine();

            switch (option) {
                case "1":
                    placeBid(scanner, buyerId);
                    break;
                case "2":
                    viewWonAuctions(scanner, buyerId);
                    break;
                case "3":
                    loggedIn = false;
                    System.out.println("You have been logged out.");
                    break;
                default:
                    System.out.println("Invalid option, please choose again.");
            }
        }
    }


    private static void placeBid(Scanner scanner, int buyerId) {
        
            System.out.println("Place a bid");
            System.out.print("Enter the bid amount: ");
            double bidAmount = Double.parseDouble(scanner.nextLine());


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
                    String response = in.readLine();
                    System.out.println(response);
                    socket.close();
                } catch (IOException e){
                    e.printStackTrace();
                    System.out.println("Error sending bid to the server.");
                }
        }
    private static void viewWonAuctions(Scanner scanner, int buyerId) {
        System.out.println("Your won auctions:");
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT bid_id, item_id, bid_amount FROM bid where buyer_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, buyerId);
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    System.out.print("Bid ID: " + rs.getInt("bid_id"));
                    System.out.print("\tItem ID: " + rs.getInt("item_id"));
                    System.out.println("\tBid Amount: " + rs.getDouble("bid_amount"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Database error.");
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome to the Auction System");
        System.out.println("1. Buyer Login\n2. Buyer Sign Up");
        String choice = scanner.nextLine();

        if ("1".equals(choice)) {
            login(scanner);
        } else if ("2".equals(choice)) {
            signup(scanner);
        } else {
            System.out.println("Invalid choice");
        }
    }
}
