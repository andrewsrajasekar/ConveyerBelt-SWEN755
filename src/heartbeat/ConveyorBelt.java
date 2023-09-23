package heartbeat;

import java.util.Random;

public class ConveyorBelt {
    private final int id;
    private final int threshold;
    private final int deviation;
    private boolean status;
    private final Random random = new Random();

    public ConveyorBelt(int id, int threshold, int deviation) {
        this.id = id;
        this.threshold = threshold;
        this.deviation = deviation;
        this.status = true;
    }

    public static void main(String[] args) {
        ConveyorBelt belt = new ConveyorBelt(1, 10, 3);
        belt.run();
    }

    public int getId() {
        return id;
    }

    public int getThreshold() {
        return threshold;
    }

    public boolean checkStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    @Override
    public int hashCode() {
        return getId();
    }

    @Override
    public String toString() {
        return "ConveyorBelt:" + id;
    }

    private int getProductCount(boolean error) {
        Random rand = new Random();
        int multiplier = error ? 2 : 1;
        int max = threshold + deviation * multiplier;
        int min = threshold - deviation * multiplier;
        int count = rand.nextInt(max - min) + min;
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return count;
    }

    private void handleErrors() {
        System.out.println("ERROR");
        if (random.nextBoolean()) {
            System.out.println("SYSTEM FIXED");
        } else {
            System.out.println("SYSTEM NOT FIXED");
            System.out.println(this+" TERMINATED");
            setStatus(false);
        }
    }

    public void run() {
        System.out.println("Starting operation of " + this);
        while (status) {
            int r = random.nextInt(10);
            if (r < 8) {
                System.out.println("Received products: " + getProductCount(false));
            } else {
                System.out.println("Received products: " + getProductCount(true));
                handleErrors();
                if (!status) break;
            }
        }
    }

}
