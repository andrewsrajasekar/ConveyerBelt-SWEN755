package heartbeat;

import java.util.Timer;
import java.util.TimerTask;

public class HeartbeatReceiver {
    private static int noOfSenders = 1;
    private static Long[] lastUpdatedTimeStamps = new Long[noOfSenders];
    private static Long senderFreq = 2000l;
    private static Long senderCheckFreq = senderFreq + 500l;

    public static void main(String[] args) {
        Timer timer = new Timer();

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                checkAlive();
            }
        };
        timer.scheduleAtFixedRate(task, 0, senderCheckFreq);
    }

    public boolean pitAPat(int id, Long updatedMilliseconds) {
        updateTime(id, updatedMilliseconds);
        return true;
    }

    private void updateTime(int id, Long updatedMilliseconds) {
        lastUpdatedTimeStamps[id - 1] = updatedMilliseconds;
    }

    private static boolean checkAlive() {
        Long currentMillisecond = System.currentTimeMillis();
        System.out.println(currentMillisecond);
        for (int i = 0; i < noOfSenders; i++) {
            if (lastUpdatedTimeStamps[i] != null) {
                if ((currentMillisecond - (senderFreq + 1000l)) > lastUpdatedTimeStamps[i]) {
                    FaultMonitor.notifyUser((i + 1), lastUpdatedTimeStamps[i]);
                }
            }
        }
        return true;
    }

}
