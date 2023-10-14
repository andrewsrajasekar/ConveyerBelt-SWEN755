package heartbeat;

import java.util.Random;

public class ConveyorBelt extends Thread {
    private final int id;
    private final int threshold;
    private final int deviation;
    private final Random random = new Random();
    private boolean status;

    private boolean backup;
    private int productCount;

    public ConveyorBelt(int id){
        this(id,10,3,true,false,10);
    }

    public ConveyorBelt(int id, int threshold, int deviation) {
        this(id, threshold, deviation, true, false, threshold);
    }

    public ConveyorBelt(int id, int threshold, int deviation, boolean status, boolean backup, int productCount) {
        this.id = id;
        this.threshold = threshold;
        this.deviation = deviation;
        this.backup = backup;
        this.status = status;
        this.productCount = productCount;
    }

    public static void main(String[] args) {
        ConveyorBelt belt = new ConveyorBelt(1, 10, 3);
        belt.start();
    }

    public int getBeltId() {
        return id;
    }

    public int getThreshold() {
        return threshold;
    }

    public boolean checkStatus() {
        return status;
    }

    private void setStatus(boolean status) {
        this.status = status;
    }

    public boolean isBackup() {
        return backup;
    }

    public void setBackup(boolean backup) {
        this.backup = backup;
    }

    public int getProductCount() {
        return productCount;
    }

    public void setProductCount(int productCount) {
        this.productCount = productCount;
    }

    @Override
    public String toString() {
        return "ConveyorBelt:" + id + "("+threshold+")";
    }

    private int getProductCount(boolean error) {
        int multiplier = error ? 2 : 1;
        int max = threshold + deviation * multiplier;
        int min = threshold - deviation * multiplier;
        int count = random.nextInt(max - min) + min;
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return count;
    }

    private void handleErrors() {
        setStatus(false);
        log("ERROR", "THRESHOLD VIOLATED");
        log("ERROR", "Attempting to self-repair");
        if (fixConveyorBelt()) {
            setStatus(true);
            log("SUCCESS", "Self-Repair successful. Resuming normal operations");
        } else {
            log("FAILURE", "Self-Repair failed.");
            log("FAILURE", "Terminating for safety.");
            setStatus(false);
        }
    }

    public boolean fixConveyorBelt() {
        return random.nextInt(100) < 80;
    }

    private void log(String level, String message) {
        String s = String.format("[%s][%s] %s", this, level, message);
        System.out.println(s);
    }

    private void log(String message) {
        log("INFO", message);
    }

    @Override
    public void run() {
        log("STARTING OPERATION");
        while (status) {
            boolean normalOperation = random.nextInt(10) < 8;
            if (normalOperation) {
                int count = getProductCount(false);
                setProductCount(count);
                log("Received products: " + count);
            } else {
                int count = getProductCount(true);
                setProductCount(count);
                log("Received products: " + count);
                handleErrors();
                if (!status) break;
            }
        }
    }

}
