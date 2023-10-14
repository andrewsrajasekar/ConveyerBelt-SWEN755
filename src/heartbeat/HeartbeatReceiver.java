package heartbeat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

public class HeartbeatReceiver {

    public class ConveyorBeltData {
        private Integer noOfProducts;
        private Long lastUpdatedTimeStamp;
        private Boolean isRunning;

        public Integer getNoOfProducts() {
            return noOfProducts;
        }

        public void setNoOfProducts(Integer noOfProducts) {
            this.noOfProducts = noOfProducts;
        }

        public Long getLastUpdatedTimeStamp() {
            return lastUpdatedTimeStamp;
        }

        public void setLastUpdatedTimeStamp(Long lastUpdatedTimeStamp) {
            this.lastUpdatedTimeStamp = lastUpdatedTimeStamp;
        }

        public Boolean getIsRunning() {
            return isRunning;
        }

        public void setIsRunning(Boolean isRunning) {
            this.isRunning = isRunning;
        }
    }

    private static final HashMap<Integer, ConveyorBeltData> heartbeatMap = new HashMap<>();
    private static final Long senderFreq = 2000L;
    private static final Long senderCheckFreq = senderFreq + 2000L;
    private static ServerSocket serverSocket;
    private static Socket clientSocket;
    private static BufferedReader reader;

    public static void main(String[] args) {
        FaultMonitor.printHeader();
        Timer timer = new Timer();

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                checkAlive();
            }
        };
        timer.scheduleAtFixedRate(task, 0, senderCheckFreq);
        try {
            initialize();
            receiveHeartbeat();
        } catch (IOException exception) {
            System.out.println(exception);
        } finally {
            closeConnection();
        }

    }

    public static boolean pitAPat(int id, Long updatedMilliseconds, Integer noOfProducts) {
        updateTime(id, updatedMilliseconds, noOfProducts);
        return true;
    }

    private static void updateTime(int id, Long updatedMilliseconds, Integer noOfProducts) {
        Long currentMillisecond = System.currentTimeMillis();
        ConveyorBeltData conveyorBeltData = new HeartbeatReceiver().new ConveyorBeltData();
        conveyorBeltData.setLastUpdatedTimeStamp(updatedMilliseconds);
        conveyorBeltData.setNoOfProducts(noOfProducts);
        conveyorBeltData.setIsRunning(Boolean.TRUE);
        heartbeatMap.put(id, conveyorBeltData);
        FaultMonitor.notifyUserSuccess(id, updatedMilliseconds, currentMillisecond);
    }

    private static boolean checkAlive() {
        Long currentMillisecond = System.currentTimeMillis();
        Iterator<Integer> sendIndexVsLastUpdatedTimeStampIter = heartbeatMap.keySet().iterator();
        while (sendIndexVsLastUpdatedTimeStampIter.hasNext()) {
            Integer id = sendIndexVsLastUpdatedTimeStampIter.next();
            ConveyorBeltData conveyorBeltData = heartbeatMap.get(id);
            Long lastUpdatedTimeStamp = conveyorBeltData.getLastUpdatedTimeStamp();
            Boolean isRunning = conveyorBeltData.getIsRunning();
            if (!isRunning) {
                continue;
            }
            if ((currentMillisecond - (senderFreq + 1000L)) > lastUpdatedTimeStamp) {
                FaultMonitor.notifyUserFailure(id, lastUpdatedTimeStamp, currentMillisecond);
                conveyorBeltData.setIsRunning(Boolean.FALSE);
                heartbeatMap.put(id, conveyorBeltData);
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
                        Integer noOfProducts = Integer.parseInt(parts[2]);
                        pitAPat(id, timestamp, noOfProducts);
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
