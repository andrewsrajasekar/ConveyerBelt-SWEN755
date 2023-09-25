package heartbeat;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

public class HeartbeatSender extends Thread {
    int id;
    ConveyorBelt cb = new ConveyorBelt(1, 10, 3);
    private static Socket clientSocket;
    private static OutputStream outputStream;
    private static PrintWriter writer;
    private static HashMap<Integer, Boolean> threadsRunning = new HashMap<>();

    public HeartbeatSender(int id, ConveyorBelt cb) {
        this.id = id;
        this.cb = cb;
    }

    public HeartbeatSender(ConveyorBelt cb) {
        this(new Random().nextInt(), cb);
    }

    public static void main(String[] args) throws InterruptedException, IOException {
        ConveyorBelt belt = new ConveyorBelt(1, 10, 2);

        belt.start();
        startOrRunThread(belt, 1, true);
        belt.join();
        startOrRunThread(belt, 2, false);
    }

    @Override
    public void run() {
        log("Monitoring Status for " + cb);
        Boolean isRunning = true;
        while (isRunning) {

            try {
                Thread.sleep(2000);

            } catch (InterruptedException e) {

            }
            if (cb.checkStatus()) {
                sendData(id, System.currentTimeMillis());
                log("Heartbeat Sent");
            } else {
                log("Belt Heartbeat not detected");
                isRunning = false;
                threadsRunning.put(id, false);
                checkAndCloseConnection();
            }
        }

    }

    private static void startOrRunThread(ConveyorBelt belt, int id, Boolean isStart)
            throws InterruptedException, IOException {
        HeartbeatSender hs = new HeartbeatSender(id, belt);
        if (isStart) {
            hs.start();
            initializeConnection();
        } else {
            hs.join();
        }
        threadsRunning.put(id, true);
    }

    private static void initializeConnection() throws IOException {
        try {
            clientSocket = new Socket("localhost", 8888);
            outputStream = clientSocket.getOutputStream();

            writer = new PrintWriter(outputStream, true);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static void checkAndCloseConnection() {
        try {
            if (threadsRunning.isEmpty()) {
                closeConnection();
            } else {
                Iterator<Integer> threadsRunningIter = threadsRunning.keySet().iterator();
                Boolean isNotRunning = true;
                while (threadsRunningIter.hasNext()) {
                    Integer id = threadsRunningIter.next();
                    if (threadsRunning.get(id)) {
                        isNotRunning = false;
                        break;
                    }
                }
                if (isNotRunning) {
                    closeConnection();
                }
            }
        } catch (Exception ex) {
            System.out.println("Exception ::: " + ex.getMessage());
        }
    }

    private static void closeConnection() throws IOException {
        clientSocket.close();
        outputStream.close();
        writer.close();
    }

    private static void sendData(int id, Long timeStamp) {
        HashMap<String, String> data = new HashMap<>();
        data.put("id", "" + id);
        data.put("timeStamp", "" + timeStamp);
        String sendData = data.toString();

        writer.println(sendData);
    }

    public void log(String message) {
        String output = String.format("[HeartbeatSender: %d][INFO][%s] %s", id, new java.util.Date(), message);
        System.out.println(output);
    }

}
