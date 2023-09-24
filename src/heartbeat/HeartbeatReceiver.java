package heartbeat;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

public class HeartbeatReceiver {
    private static final HashMap<Integer, Long> senderIdVsLastUpdatedTimeStamp = new HashMap<>();
    private static final Long senderFreq = 2000L;
    private static final Long senderCheckFreq = senderFreq + 500L;

    // Static initializer block
    static {
        Timer timer = new Timer();

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                checkAlive();
            }
        };
        timer.scheduleAtFixedRate(task, 0, senderCheckFreq);
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
                FaultMonitor.notifyUser(id, lastUpdatedTimeStamp);
                senderIdVsLastUpdatedTimeStamp.put(id, -1L);
            }
        }
        return true;
    }

}
