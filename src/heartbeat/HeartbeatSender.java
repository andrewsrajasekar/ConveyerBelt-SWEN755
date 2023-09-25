package heartbeat;

import java.util.Random;

public class HeartbeatSender extends Thread {
    int id;
    ConveyorBelt cb;

    public HeartbeatSender(int id, ConveyorBelt cb) {
        this.id = id;
        this.cb = cb;
    }

    public HeartbeatSender(ConveyorBelt cb) {
        this(new Random().nextInt(), cb);
    }

    public static void main(String[] args) throws InterruptedException {
        int numberOfBelts = 3;
        ConveyorBelt[] belts = new ConveyorBelt[numberOfBelts];
        HeartbeatSender[] senders = new HeartbeatSender[numberOfBelts];
        for (int i = 0; i < numberOfBelts; i++) {
            ConveyorBelt belt = new ConveyorBelt(i, 10, 5);
            HeartbeatSender sender = new HeartbeatSender(i, belt);
            belts[i] = belt;
            senders[i] = sender;
        }

        for (int i = 0; i < numberOfBelts; i++) {
            ConveyorBelt belt = new ConveyorBelt(i, 10, 5);
            HeartbeatSender sender = new HeartbeatSender(i, belt);
            belts[i] = belt;
            senders[i] = sender;
        }

        ConveyorBelt belt = new ConveyorBelt(1, 10, 2);
        HeartbeatSender hs = new HeartbeatSender(1, belt);
        ConveyorBelt belt2 = new ConveyorBelt(2, 10, 2);
        HeartbeatSender hs2 = new HeartbeatSender(2, belt2);

        belt.start();
        belt2.start();
        hs.start();
        hs2.start();

        belt.join();
        belt2.join();
        hs.join();
        hs2.join();

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

    private void log(String message) {
        String output = String.format("[HeartbeatSender: %d][INFO][%s] %s", id, new java.util.Date(), message);
        System.out.println(output);
    }

}
