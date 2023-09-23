package heartbeat;

<<<<<<< HEAD
import java.util.Random;

public class HeartbeatSender extends Thread{
=======
public class HeartbeatSender extends Thread {
>>>>>>> 0fe171ea2b02d946ccb0fa3882cca7f7148289dd

    Random random = new Random();
    int id = random.nextInt(100);;
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
