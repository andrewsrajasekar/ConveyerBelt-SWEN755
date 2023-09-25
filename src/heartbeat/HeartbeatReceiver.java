package heartbeat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

public class HeartbeatReceiver {
    private static final HashMap<Integer, Long> heartbeatMap = new HashMap<>();
    private static final Long senderFreq = 2000L;
    private static final Long senderCheckFreq = senderFreq + 2000L;
    private static ServerSocket serverSocket;
    private static Socket clientSocket;
    private static InputStream inputStream;
    private static BufferedReader reader;

    public static void main(String[] args){
        Timer timer = new Timer();

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                checkAlive();
            }
        };
        timer.scheduleAtFixedRate(task, 0, senderCheckFreq);
        try{
            initialize();
            receiveHeartbeat();
        } catch (IOException exception){
            System.out.println(exception);
        } finally {
            closeConnection();
        }

    }

    public static boolean pitAPat(int id, Long updatedMilliseconds) {
        updateTime(id, updatedMilliseconds);
        return true;
    }

    private static void updateTime(int id, Long updatedMilliseconds) {
        heartbeatMap.put(id, updatedMilliseconds);
        FaultMonitor.notifyUserSuccess(id, updatedMilliseconds);
    }

    private static boolean checkAlive() {
        Long currentMillisecond = System.currentTimeMillis();
        Iterator<Integer> sendIndexVsLastUpdatedTimeStampIter = heartbeatMap.keySet().iterator();
        while (sendIndexVsLastUpdatedTimeStampIter.hasNext()) {
            Integer id = sendIndexVsLastUpdatedTimeStampIter.next();
            Long lastUpdatedTimeStamp = heartbeatMap.get(id);
            if (lastUpdatedTimeStamp == -1) {
                continue;
            }
            if ((currentMillisecond - (senderFreq + 1000L)) > lastUpdatedTimeStamp) {
                FaultMonitor.notifyUser(id, lastUpdatedTimeStamp, currentMillisecond);
                heartbeatMap.put(id, -1L);
            }
        }
        return true;
    }

    public static void initialize(int port) throws IOException {
        serverSocket = new ServerSocket(port);
    }

    public static void initialize() throws IOException {
        serverSocket = new ServerSocket(8888);
    }



    private static void receiveHeartbeat() throws IOException {
        while (true) {
            try (Socket clientSocket = serverSocket.accept();
                 BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {

                String message;
                while ((message = reader.readLine()) != null) {
                    String[] parts = message.split(":", 2);
                    if (parts.length == 2) {
                        int id = Integer.parseInt(parts[0]);
                        long timestamp = Long.parseLong(parts[1]);
                        pitAPat(id, timestamp);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void closeConnection() {
        try {
            reader.close();
            clientSocket.close();
            serverSocket.close();
        } catch (Exception ex) {

        }

    }

}
