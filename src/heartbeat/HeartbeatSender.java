package heartbeat;

public class HeartbeatSender extends Thread{

    @Override
    public void run(){
        int id = 12345;
        ConveyorBelt cb = new ConveyorBelt();
        HeartbeatReceiver hr = new HeartbeatReceiver();

        while(true){

            try{
                Thread.sleep(2000);

            }
            catch(InterruptedException e){

            }
            long start = System.nanoTime()/1000000;
            if(cb.checkStatus()){
                hr.pitApat(start, id);
            }
            else {
                break;
            }


        }

    }

}
