package heartbeat;

public class HeartbeatSender extends Thread {

    int id = 12345;
    ConveyorBelt cb = new ConveyorBelt();
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

}
