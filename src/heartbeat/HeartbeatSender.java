package heartbeat;

import java.util.Random;

public class HeartbeatSender extends Thread {
    int id;
    ConveyorBelt cb = new ConveyorBelt(1, 10, 3);

    public HeartbeatSender(int id, ConveyorBelt cb) {
        this.id = id;
        this.cb = cb;
    }

    public HeartbeatSender(ConveyorBelt cb) {
        this(new Random().nextInt(), cb);
    }

    public static void main(String[] args) throws InterruptedException {
        ConveyorBelt belt = new ConveyorBelt(1, 10, 2);
        HeartbeatSender hs = new HeartbeatSender(1, belt);

        hs.start();
        belt.start();
        belt.join();
        hs.join();

    }

    @Override
    public void run() {
        log("Monitoring Status for " + cb);
        while (true) {

            try {
                Thread.sleep(2000);

            } catch (InterruptedException e) {

            }
            long start = System.nanoTime() / 1000000;
            if (cb.checkStatus()) {
                HeartbeatReceiver.pitAPat(id, start);
                log("Heartbeat Sent");
            } else {
                log("Belt Heartbeat not detected");
            }
        }

    }

    public void log(String message) {
        String output = String.format("[HeartbeatSender: %d][INFO][%s] %s", id, new java.util.Date(), message);
        System.out.println(output);
    }

}
