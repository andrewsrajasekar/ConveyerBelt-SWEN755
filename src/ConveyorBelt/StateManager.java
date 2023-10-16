package ConveyorBelt;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import heartbeat.HeartbeatSender;

public class StateManager {
    private static HashMap<Integer, ConveyorBelt> primaryBelts = new HashMap<>();
    private static HashMap<Integer, ConveyorBeltBackup> backupBelts = new HashMap<>();
    private static HashMap<Integer, HeartbeatSender> senders = new HashMap<>();
    private static HashMap<Integer, Checkpoint> checkpoints = new HashMap<>();
    private static ServerSocket serverSocket;

    public static void registerBelt(ConveyorBelt belt) {
        primaryBelts.put(belt.getBeltId(), belt);
    }

    public static void registerBackupBelt(int beltId, ConveyorBeltBackup belt) {
        backupBelts.put(beltId, belt);
    }

    public static void registerSender(int beltId, HeartbeatSender sender) {
        senders.put(beltId, sender);
    }

    public static ConveyorBelt getBelt(int beltId) {
        return primaryBelts.get(beltId);
    }

    public static ConveyorBeltBackup getBackupBelt(int beltId) {
        return backupBelts.get(beltId);
    }

    public static HeartbeatSender getSender(int beltId) {
        return senders.get(beltId);
    }

    public static void startAll() {
        for (ConveyorBelt belt : primaryBelts.values()) {
            belt.start();
        }
        for (HeartbeatSender sender : senders.values()) {
            sender.start();
        }
    }

    public static void joinAll() throws InterruptedException {
        for (ConveyorBelt belt : primaryBelts.values()) {
            belt.join();
        }
        for (HeartbeatSender sender : senders.values()) {
            sender.join();
        }

    }

    public static void activateBackup(int beltId) {
        ConveyorBeltBackup backupBelt = backupBelts.get(beltId);
        if (backupBelt != null) {
            backupBelt.log("Activating backup belt");
            backupBelt.log("Loading data from last checkpoint: "+ checkpoints.get(beltId));
            int productCount = getProductCountFromCheckpoint(beltId);
            backupBelt.activate(productCount);

            primaryBelts.put(beltId, backupBelt);
            backupBelts.remove(beltId);
            backupBelt.start();
            HeartbeatSender sender = senders.get(beltId);
            sender.setBelt(backupBelt);
        } else {
            ConveyorBelt primaryBelt = primaryBelts.get(beltId);
            primaryBelt.log("No backup belt found");
        }
    }

    public static int getProductCountFromCheckpoint(int beltId) {
        return checkpoints.get(beltId).getProductCount();
    }

    public static void saveCheckpoints() {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                for (ConveyorBelt belt : primaryBelts.values()) {
                    if(!belt.checkStatus()){
                        continue;
                    }
                    int beltId = belt.getBeltId();
                    int productCount = belt.getProductCount();
                    Checkpoint checkpoint = new Checkpoint(beltId, productCount, System.currentTimeMillis());
                    checkpoints.put(beltId, checkpoint);
                    belt.log("Checkpoint saved: " + checkpoint);
                }
            }
        }, 0, 3000);
    }

    public static void initialize() throws IOException {
        serverSocket = new ServerSocket(8900);
    }

    private static void receiveTriggerSignal() throws IOException {
        while (true) {
            try (Socket clientSocket = serverSocket.accept();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {

                String message;
                while ((message = reader.readLine()) != null) {
                    String[] parts = message.split(":", 2);
                    if (parts.length == 2) {
                        int id = Integer.parseInt(parts[0]);
                        activateBackup(id);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void printHeader() {
        System.out.printf("%-30s%-10s%-50s%-60s%n", "Timestamp", "Log Level", "Source", "Message");
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        printHeader();
        initialize();
        Thread receiveSignalThread = new Thread(() -> {
            try {
                receiveTriggerSignal();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        receiveSignalThread.start();
        ConveyorBeltFactory factory = new ConveyorBeltFactory();
        factory.createConveyorBelt(1);
        factory.createConveyorBelt(2);
        StateManager.startAll();
        StateManager.saveCheckpoints();
        StateManager.joinAll();
        factory.closeConnection();
    }

}
