package heartbeat;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class HeartbeatSender extends Thread {
    private final int id;
    private ConveyorBelt belt;
    private final HeartbeatConnection connection;

    public HeartbeatSender(int id, ConveyorBelt belt, HeartbeatConnection connection) {
        this.id = id;
        this.belt = belt;
        this.connection = connection;
    }

    public ConveyorBelt getBelt() {
        return belt;
    }

    public void setBelt(ConveyorBelt belt) {
        this.belt = belt;
    }

    public static void main(String[] args) throws InterruptedException, IOException {
        int NUM_BELTS = 2;
        HeartbeatConnection connection = new HeartbeatConnection();
        Set<ConveyorBelt> belts = new HashSet<>();
        Set<HeartbeatSender> senders = new HashSet<>();
        for (int i = 0; i < NUM_BELTS; i++) {
            ConveyorBelt belt = new ConveyorBelt(i);
            belts.add(belt);
            HeartbeatSender sender = new HeartbeatSender(i, belt, connection);
            senders.add(sender);
        }
        for (HeartbeatSender sender:senders) {
            sender.getBelt().start();
            sender.start();
        }

        for (HeartbeatSender sender:senders) {
            sender.getBelt().join();
            sender.join();
        }
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
//                    Thread.currentThread().interrupt();
//                    break;
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
