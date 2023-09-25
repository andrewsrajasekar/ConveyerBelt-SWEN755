package heartbeat;

import java.io.IOException;
import java.util.Random;

public class HeartbeatSender extends Thread {
    private final int id;
    private final ConveyorBelt belt;
    private final HeartbeatConnection connection;


    public HeartbeatSender(int id, ConveyorBelt belt, HeartbeatConnection connection) {
        this.id = id;
        this.belt = belt;
        this.connection = connection;
    }


    public static void main(String[] args) throws InterruptedException, IOException {
        HeartbeatConnection connection = new HeartbeatConnection();
        ConveyorBelt belt = new ConveyorBelt(1, 10, 2);
        ConveyorBelt belt2 = new ConveyorBelt(2, 10, 2);
        HeartbeatSender sender = new HeartbeatSender(1,belt,connection);
        HeartbeatSender sender2 = new HeartbeatSender(2,belt2,connection);
        belt.start();
        belt2.start();
        sender.start();
        sender2.start();

        belt.join();
        belt2.join();
        sender.join();
        sender2.join();
        connection.close();

    }

    @Override
    public void run() {
        log("Monitoring Status for " + belt);
        while (true) {
            try {
                long heartbeatInterval = 2000;
                Thread.sleep(heartbeatInterval);
                if (belt.checkStatus()) {
                    connection.sendHeartbeat(belt.getBeltId());
                    log("Heartbeat Sent");
                } else {
                    log("Belt Heartbeat not detected");
                    Thread.currentThread().interrupt();
                    break;
                }
            } catch (InterruptedException e) {
                // Log the exception and/or re-interrupt the thread
                Thread.currentThread().interrupt();
                break;
            }
        }
    }



    public void log(String message) {
        String output = String.format("[HeartbeatSender: %d][INFO][%s] %s", id, new java.util.Date(), message);
        System.out.println(output);
    }

}
