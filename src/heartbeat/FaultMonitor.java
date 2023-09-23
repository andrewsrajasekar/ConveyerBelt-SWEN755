package heartbeat;

public class FaultMonitor {
    public static void notifyUser(int id, Long lastUpdatedMilliseconds) {
        String boldStart = "\033[1m";
        String boldEnd = "\033[0m";
        System.out.println("HeartBeat Failed for Conveyer Belt of id " + (boldStart + id + boldEnd)
                + ", its last updated time was : "
                + (boldStart + (lastUpdatedMilliseconds / 1000) + boldEnd) + " seconds ago.");
    }
}
