package heartbeat;

import java.util.Random;

public class HeartbeatSender extends Thread{

    Random random = new Random();
    int id = random.nextInt(100);;
    ConveyorBelt cb = new ConveyorBelt();
    HeartbeatReceiver hr = new HeartbeatReceiver();
    @Override
    public void run(){


        while(true){

            try{
                Thread.sleep(2000);

            }
            catch(InterruptedException e){

            }
            long start = System.nanoTime()/1000000;
            if(cb.checkStatus()){
                hr.pitAPat(id, start);
            }
            else {
                break;
            }


        }

    }

}
