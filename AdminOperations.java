import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
//import java.util.concurrent.TimeUnit;

public class AdminOperations {
    
    private boolean auctionActive = false;
    private ExecutorService clientHandlerExecutor;
    private ConcurrentHashMap<Integer, BidInfo> bids = new ConcurrentHashMap<>();
    private ServerSocket serverSocket = null;  // Initialize to null
    private boolean running = true;
    private volatile int currentBidId = -1; // Default to -1 indicating no active auction
    private int item_id =-1;

    public static void main(String[] args) {
        AdminOperations server = new AdminOperations();
        server.initializeServer();
    }

    public void initializeServer() {
        try {
            // Start server if not already running
            if (serverSocket == null) {
                clientHandlerExecutor = Executors.newCachedThreadPool();
                serverSocket = new ServerSocket(12345); // Use your desired port
                startServer();
            }
        } catch (IOException e) {
            System.out.println("Could not listen on the specified port: " + e.getMessage());
        }
    }

    private void startServer() {
        Thread serverThread = new Thread(() -> {
            while (running) {
                try {
                    Socket clientSocket = serverSocket.accept();
                   System.out.println("Client connected.");
                        ClientHandler clientHandler = new ClientHandler(clientSocket, bids, this);
                        clientHandlerExecutor.execute(clientHandler);  // Submit to thread pool*/
                } catch (IOException e) {
                    System.out.println("Error accepting client connection: " + e.getMessage());
                }
            }
        });
        serverThread.start();
    }

    public void startAuction( ) {
        System.out.println("ID2 : "+AuctionBase.Item_id);
        
        item_id=AuctionBase.Item_id;
        if (!auctionActive) {
            System.out.println("ID3 : "+AuctionBase.Item_id);
            
            auctionActive = true;
            currentBidId = generateBidId();
            bids.put(currentBidId, new BidInfo(-1, 0.0));

            // Start server if not already running (redundant check for clarity)
            if (serverSocket == null) {
                initializeServer();
            }
        } else {
            System.out.println("Auction is already in progress.");
        }
    }

    private int generateBidId() {
        return Math.abs(UUID.randomUUID().hashCode());
    }


    public String endAuction() {
         String str;
        auctionActive = false;
        currentBidId = -1; // Reset the currentBidId indicating no active auction
        System.out.println("Auction ended.");

        // Find the winning bid
        Optional<BidInfo> winningBid = bids.values().stream()
                .max(Comparator.comparingDouble(BidInfo::getBidAmount));

        if (winningBid.get().getBuyerId()!=-1) {
           str  = "Winner is Buyer ID " + winningBid.get().getBuyerId() + " with a bid of " + winningBid.get().getBidAmount();
            String query = "UPDATE item SET status = 1 WHERE item_id ="+ item_id;
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {
                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Item marked as sold.");
                    query="insert into bid (buyer_id,bid_amount	,item_id)   values("+winningBid.get().getBuyerId()+","+winningBid.get().getBidAmount()+","+item_id+")";
                    try (PreparedStatement st = conn.prepareStatement(query)) {
                        int rowsAffected1 = st.executeUpdate();
                        if (rowsAffected1 > 0) {
                            System.out.println("Bid added to database.");
                        } else {
                            System.out.println("Failed to add bid to database.");
                        }
                    }
                    catch(SQLException e){
                        System.out.println("Database error: " + e.getMessage());
                    }
                } else {
                    System.out.println("Failed to mark item as sold.");
                }
            } catch (SQLException e) {
                System.out.println("Database error: " + e.getMessage());
            }
            return str;
        } else {
            str="No bids received for this auction.";
        }

        bids.clear();
        return str;
        
        
    }

    public int getCurrentBidId() {
        return currentBidId;
    }
    public void logout() {
        System.out.println("Admin logged out.");
        //running = false;
        System.exit(0);
    }
}

