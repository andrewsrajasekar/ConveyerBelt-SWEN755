package heartbeat;
import java.util.Random;

public class ConveyorBelt {
    private final int id;
    private final int threshold;
    private final int deviation;
    private boolean status;

    public ConveyorBelt(int id, int threshold, int deviation) {
        this.id = id;
        this.threshold = threshold;
        this.deviation = deviation;
        this.status = true;
    }

    public int getId() {
        return id;
    }

    public int getThreshold() {
        return threshold;
    }

    public boolean getStatus() {
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

    private int getProductCount(boolean error){
        Random rand = new Random();
        int multiplier;
        if(error){
            multiplier = 2;
        } else {
            multiplier = 1;
        }
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

    public void run(){
        System.out.println("Starting operation of "+this);
        while(true){
            Random random = new Random();
            int r = random.nextInt(10);
            if(r<8){
                System.out.println("Received products:"+getProductCount(false));
            } else {

                System.out.println("Received products:"+getProductCount(true));
                System.out.println("ERROR");
                if(random.nextBoolean()){
                    System.out.println("STATUS FIXED");
                } else {
                    System.out.println("System could not be fixed");
                    setStatus(false);
                    break;
                }
            }

        }
    }



    public static void main(String[] args) {
        ConveyorBelt belt = new ConveyorBelt(1, 10,3);
        belt.run();
    }
}
