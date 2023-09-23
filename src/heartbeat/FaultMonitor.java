package heartbeat;

public class FaultMonitor {
    public static void notifyUser(int id, Long lastUpdatedMilliseconds) {
        System.out.println("HeartBeat Failed for Conveyer Belt " + id + ", its last updated time was : "
                + (lastUpdatedMilliseconds / 1000) + " seconds ago.");
    }
}
