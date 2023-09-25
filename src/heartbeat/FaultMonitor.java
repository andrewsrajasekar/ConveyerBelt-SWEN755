package heartbeat;

import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

public class FaultMonitor {
    public static void notifyUser(int id, Long lastUpdatedMilliseconds, long currentMillisecond) {
        String boldStart = "\033[1m";
        String boldEnd = "\033[0m";
        System.out.println("HeartBeat Failed for Conveyer Belt of id " + (boldStart + id + boldEnd)
                + ", its last updated time was : "
                + (boldStart + (convertToTime(lastUpdatedMilliseconds)) + boldEnd) + ", " + " Current Time :"
                + (boldStart + (convertToTime(currentMillisecond)) + boldEnd));
    }

    public static void notifyUserSuccess(int id, Long lastUpdatedMilliseconds) {
        String boldStart = "\033[1m";
        String boldEnd = "\033[0m";
        System.out.println("HeartBeat Running for Conveyer Belt of id " + (boldStart + id + boldEnd)
                + ", its last updated time was : "
                + (boldStart + (convertToTime(lastUpdatedMilliseconds)) + boldEnd) + " seconds ago.");
    }

    private static String convertToTime(Long milliseconds) {
        Date date = new Date(milliseconds);
        DateFormat formatter = new SimpleDateFormat("HH:mm:ss.SSS");
        formatter.setTimeZone(TimeZone.getTimeZone("EST"));
        return formatter.format(date);
    }
}
