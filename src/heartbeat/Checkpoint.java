package heartbeat;

public class Checkpoint {
    private int beltId;
    private int productCount;
    private long timestamp;

    public Checkpoint(int beltId, int productCount, long timestamp) {
        this.beltId = beltId;
        this.productCount = productCount;
        this.timestamp = timestamp;
    }

    public int getBeltId() {
        return beltId;
    }

    public int getProductCount() {
        return productCount;
    }

    public long getTimestamp() {
        return timestamp;
    }

}
