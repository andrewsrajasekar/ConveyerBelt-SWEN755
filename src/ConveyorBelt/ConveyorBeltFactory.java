package ConveyorBelt;

import java.io.IOException;

import heartbeat.HeartbeatConnection;
import heartbeat.HeartbeatSender;

public class ConveyorBeltFactory {
    HeartbeatConnection connection = new HeartbeatConnection(Boolean.FALSE);

    public ConveyorBeltFactory() throws IOException {
    }

    /**
     * Creates a new ConveyorBelt, the corresponding backup conveyor belt and the
     * heartbeat sender with the given id.
     * 
     * @param id The id of the new ConveyorBelt.
     * @return The new ConveyorBelt.
     */
    public ConveyorBelt createConveyorBelt(int id) {
        ConveyorBelt belt = new ConveyorBelt(id);
        ConveyorBeltBackup backup = new ConveyorBeltBackup(id);
        HeartbeatSender sender = new HeartbeatSender(id, belt, connection);
        StateManager.registerBelt(belt);
        StateManager.registerBackupBelt(id, backup);
        StateManager.registerSender(id, sender);
        return belt;
    }

    public void closeConnection() throws IOException {
        this.connection.close();
    }
}
