package heartbeat;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

public class HeartbeatReceiver {
    private static HashMap<Integer, Long> sendIndexVsLastUpdatedTimeStamp = new HashMap<>();
    private static Long senderFreq = 2000l;
    private static Long senderCheckFreq = senderFreq + 500l;

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
        System.out.println(id);
        updateTime(id, updatedMilliseconds);
        return true;
    }

    private static void updateTime(int id, Long updatedMilliseconds) {
        sendIndexVsLastUpdatedTimeStamp.put(id, updatedMilliseconds);
    }

    private static boolean checkAlive() {
        Long currentMillisecond = System.currentTimeMillis();
        Iterator<Integer> sendIndexVsLastUpdatedTimeStampIter = sendIndexVsLastUpdatedTimeStamp.keySet().iterator();
        while (sendIndexVsLastUpdatedTimeStampIter.hasNext()) {
            Integer id = sendIndexVsLastUpdatedTimeStampIter.next();
            Long lastUpdatedTimeStamp = sendIndexVsLastUpdatedTimeStamp.get(id);
            if ((currentMillisecond - (senderFreq + 1000l)) > lastUpdatedTimeStamp) {
                FaultMonitor.notifyUser(id, lastUpdatedTimeStamp);
            }
        }
        return true;
    }

}
