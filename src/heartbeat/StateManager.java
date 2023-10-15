package heartbeat;

import java.io.IOException;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class StateManager {
    private static HashMap<Integer, ConveyorBelt> primaryBelts = new HashMap<>();
    private static HashMap<Integer, ConveyorBeltBackup> backupBelts = new HashMap<>();
    private static HashMap<Integer, HeartbeatSender> senders = new HashMap<>();
    private static HashMap<Integer, Checkpoint> checkpoints = new HashMap<>();



    public static void registerBelt(ConveyorBelt belt){
        primaryBelts.put(belt.getBeltId(), belt);
    }

    public static void registerBackupBelt(int beltId, ConveyorBeltBackup belt){
        backupBelts.put(beltId, belt);
    }

    public static void registerSender(int beltId, HeartbeatSender sender){
        senders.put(beltId, sender);
    }

    public static ConveyorBelt getBelt(int beltId){
        return primaryBelts.get(beltId);
    }

    public static ConveyorBeltBackup getBackupBelt(int beltId){
        return backupBelts.get(beltId);
    }

    public static HeartbeatSender getSender(int beltId){
        return senders.get(beltId);
    }

    public static void startAll(){
        for (ConveyorBelt belt:primaryBelts.values()) {
            belt.start();
        }
        for (HeartbeatSender sender:senders.values()) {
            sender.start();
        }
    }

    public static void joinAll() throws InterruptedException{
        for (ConveyorBelt belt:primaryBelts.values()) {
            belt.join();
        }
        for (HeartbeatSender sender:senders.values()) {
            sender.join();
        }
    }


    public static void activateBackup(int beltId){
        ConveyorBeltBackup backupBelt = backupBelts.get(beltId);
        if (backupBelt != null) {
            System.out.println("Activating backup belt for " + beltId);
            int productCount = getProductCountFromCheckpoint(beltId);
           backupBelt.activate(productCount);

           primaryBelts.put(beltId, backupBelt);
            backupBelts.remove(beltId);
            backupBelt.start();
           HeartbeatSender sender = senders.get(beltId);
           sender.setBelt(backupBelt);
        }else {
            System.out.println("No backup belt found for " + beltId);
        }
    }

    public static int getProductCountFromCheckpoint(int beltId){
        return checkpoints.get(beltId).getProductCount();
    }

    public static void saveCheckpoints(){
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                for (ConveyorBelt belt : primaryBelts.values()) {
                    int beltId = belt.getBeltId();
                    int productCount = belt.getProductCount();
                    Checkpoint checkpoint = new Checkpoint(beltId, productCount, System.currentTimeMillis());
                    checkpoints.put(beltId, checkpoint);
                    System.out.println("Checkpoint saved for " + beltId+ " at " + checkpoint.getTimestamp());
                    System.out.println(checkpoint);
                }
            }
        }, 0, 500);
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        ConveyorBeltFactory factory = new ConveyorBeltFactory();
        factory.createConveyorBelt(1);
        factory.createConveyorBelt(2);
        StateManager.startAll();
        StateManager.saveCheckpoints();
        StateManager.joinAll();
        factory.connection.close();
    }


}
