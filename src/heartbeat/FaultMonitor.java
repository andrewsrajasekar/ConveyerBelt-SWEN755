package heartbeat;

import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

public class FaultMonitor {
    private static HeartbeatConnection backUpConnection;

    public static void printHeader() {
        System.out.printf("%-15s%-30s%-30s%-15s%n", "Conveyor ID", "Last Updated Time", "Current Time", "Status");
    }

    public static void notifyUserFailure(int id, Long lastUpdatedMilliseconds, long currentMillisecond) {
        String status = "FAILED";
        displayStatus(id, lastUpdatedMilliseconds, currentMillisecond, status);
        if (backUpConnection == null) {
            try {
                backUpConnection = new HeartbeatConnection(Boolean.TRUE);
            } catch (Exception ex) {
                System.out.println("Exception ::: " + ex);
            }
        }
        backUpConnection.triggerBackup(id);
    }

    public static void notifyUserSuccess(int id, Long lastUpdatedMilliseconds, long currentMillisecond) {
        String status = "RUNNING";
        displayStatus(id, lastUpdatedMilliseconds, currentMillisecond, status);
    }

    private static void displayStatus(int id, Long lastUpdatedMilliseconds, long currentMillisecond, String status) {
        System.out.printf("%-15d%-30s%-30s%-15s%n", id, convertToTime(lastUpdatedMilliseconds),
                convertToTime(currentMillisecond), status);
    }

    private static String convertToTime(Long milliseconds) {
        Date date = new Date(milliseconds);
        DateFormat formatter = new SimpleDateFormat("HH:mm:ss.SSS");
        formatter.setTimeZone(TimeZone.getTimeZone("EST"));
        return formatter.format(date);
    }

    public static void main(String[] args) {
        printHeader(); // Print the table header once
        // Assuming the notifyUser and notifyUserSuccess methods will be called when the
        // status of a conveyor belt changes
    }
}
