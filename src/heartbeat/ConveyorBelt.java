package heartbeat;

import java.util.Random;

public class ConveyorBelt extends Thread {
    private final int id;
    private final int threshold;
    private final int deviation;
    private final Random random = new Random();
    private boolean status;

    public ConveyorBelt(int id, int threshold, int deviation) {
        this.id = id;
        this.threshold = threshold;
        this.deviation = deviation;
        this.status = true;
    }

    public static void main(String[] args) {
        ConveyorBelt belt = new ConveyorBelt(1, 10, 3);
        belt.start();
    }

    @Override
    public long getId() {
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

    @Override
    public int hashCode() {
        return (int) getId();
    }

    @Override
    public String toString() {
        return "ConveyorBelt:" + id;
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
        return (random.nextBoolean() || random.nextBoolean());
    }

    public void log(String level, String message) {
        String s = String.format("[%s][%s] %s", this, level, message);
        System.out.println(s);
    }

    public void log(String message) {
        log("INFO", message);
    }

    @Override
    public void run() {
        log("STARTING OPERATION");
        while (status) {
            boolean normalOperation = random.nextInt(10) < 8;
            if (normalOperation) {
                log("Received products: " + getProductCount(false));
            } else {
                log("Received products: " + getProductCount(true));
                handleErrors();
                if (!status) break;
            }
        }
    }

}
