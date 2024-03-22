package packs; // Ensure this matches the package name of your other classes, if they are intended to be together.

public class BidInfo {
    private Double bidAmount;
    private int buyerId;

    public BidInfo(int buyerId, Double bidAmount) {
        this.buyerId = buyerId;
        this.bidAmount = bidAmount;
    }

    // Getter and setter methods
    public Double getBidAmount() {
        return bidAmount;
    }

    public void setBidAmount(Double bidAmount) {
        this.bidAmount = bidAmount;
    }

    public int getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(int buyerId) {
        this.buyerId = buyerId;
    }
}
