package heartbeat;

import java.util.Random;

public class HeartbeatSender extends Thread{


    Random random = new Random();
    int id = random.nextInt(100);
    ConveyorBelt cb = new ConveyorBelt(1,10,3);
    HeartbeatReceiver hr = new HeartbeatReceiver();

    @Override
    public void run() {

        while (true) {

            try {
                Thread.sleep(2000);

            } catch (InterruptedException e) {

            }
            long start = System.nanoTime() / 1000000;
            if (cb.checkStatus()) {
                HeartbeatReceiver.pitAPat(id, start);
            } else {
                break;
            }

        }

    }


    public static void main(String[] args) {
        HeartbeatSender hs = new HeartbeatSender();

        hs.start();

    }


}
