package heartbeat;

import ConveyorBelt.ConveyorBelt;

public class HeartbeatSender extends Thread {
    private final int id;
    private final HeartbeatConnection connection;
    private ConveyorBelt belt;

    public HeartbeatSender(int id, ConveyorBelt belt, HeartbeatConnection connection) {
        this.id = id;
        this.belt = belt;
        this.connection = connection;
    }

    public ConveyorBelt getBelt() {
        return belt;
    }

    public void setBelt(ConveyorBelt belt) {
        this.belt = belt;
    }

    @Override
    public void run() {
        log("Monitoring Status for " + belt);
        while (true) {
            try {
                long heartbeatInterval = 2000;
                Thread.sleep(heartbeatInterval);
                if (belt.checkStatus()) {
                    connection.sendHeartbeat(belt.getBeltId());
                    log("Heartbeat Sent");
                } else {
                    log("Belt Heartbeat not detected");
                    // Thread.currentThread().interrupt();
                    // break;
                }
            } catch (InterruptedException e) {
                // Log the exception and/or re-interrupt the thread
                Thread.currentThread().interrupt();
                break;
            }
        }
    }


    public void log(String message) {
        // Set fixed widths for the columns
        final int timestamp = 30;
        final int levelWidth = 10;
        final int sourceWidth = 40;
        final int messageWidth = 50;

        // Format log components
        String formattedTimestamp = String.format("%-" + timestamp + "s", new java.util.Date());
        String formattedLevel = String.format("%-" + levelWidth + "s", "INFO");
        String formattedSource = String.format("%-" + sourceWidth + "s", "HeartbeatSender[id: " + id + "]");
        String formattedMessage = String.format("%-" + messageWidth + "s", message);

        // Assemble the final log message
        String formattedLog = String.format("%s %s  %s  %s ", formattedTimestamp, formattedLevel, formattedSource, formattedMessage);

        System.out.println(formattedLog);
    }


}
