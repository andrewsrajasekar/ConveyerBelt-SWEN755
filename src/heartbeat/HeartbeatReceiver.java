package heartbeat;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
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
    private static final HashMap<Integer, Long> senderIdVsLastUpdatedTimeStamp = new HashMap<>();
    private static final Long senderFreq = 2000L;
    private static final Long senderCheckFreq = senderFreq + 2000L;
    private static ServerSocket serverSocket;
    private static Socket clientSocket;
    private static InputStream inputStream;
    private static BufferedReader reader;

    public static void main(String[] args) {
        Timer timer = new Timer();

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                checkAlive();
            }
        };
        timer.scheduleAtFixedRate(task, 0, senderCheckFreq);
        initializeConnection();
    }

    public static boolean pitAPat(int id, Long updatedMilliseconds) {
        updateTime(id, updatedMilliseconds);
        return true;
    }

    private static void updateTime(int id, Long updatedMilliseconds) {
        senderIdVsLastUpdatedTimeStamp.put(id, updatedMilliseconds);
        FaultMonitor.notifyUserSuccess(id, updatedMilliseconds);
    }

    private static boolean checkAlive() {
        Long currentMillisecond = System.currentTimeMillis();
        Iterator<Integer> sendIndexVsLastUpdatedTimeStampIter = senderIdVsLastUpdatedTimeStamp.keySet().iterator();
        while (sendIndexVsLastUpdatedTimeStampIter.hasNext()) {
            Integer id = sendIndexVsLastUpdatedTimeStampIter.next();
            Long lastUpdatedTimeStamp = senderIdVsLastUpdatedTimeStamp.get(id);
            if (lastUpdatedTimeStamp == -1) {
                continue;
            }
            if ((currentMillisecond - (senderFreq + 1000L)) > lastUpdatedTimeStamp) {
                FaultMonitor.notifyUser(id, lastUpdatedTimeStamp, currentMillisecond);
                senderIdVsLastUpdatedTimeStamp.put(id, -1L);
            }
        }
        return true;
    }

    private static void initializeConnection() {
        try {
            serverSocket = new ServerSocket(8888);
            clientSocket = serverSocket.accept();
            inputStream = clientSocket.getInputStream();
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String receivedData = reader.readLine();
            while (!receivedData.equals("Over")) {
                try {
                    receivedData = reader.readLine();
                    Integer id = -1;
                    Long timeStamp = -1l;
                    String[] keyValuePairs = receivedData
                            .substring(1, receivedData.length() - 1)
                            .split(",\\s*");
                    for (String pair : keyValuePairs) {
                        String[] entry = pair.split("=");
                        if (entry.length == 2) {
                            String key = entry[0].trim();
                            String value = entry[1].trim();
                            if (key.equalsIgnoreCase("id")) {
                                id = Integer.valueOf(value);
                            } else {
                                timeStamp = Long.valueOf(value);
                            }
                        }
                    }
                    if (id > 0) {
                        pitAPat(id, timeStamp);
                    }
                } catch (IOException i) {
                    // System.out.println(i);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeConnection();
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
