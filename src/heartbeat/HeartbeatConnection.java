package heartbeat;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class HeartbeatConnection {
    private final Socket socket;
    private final PrintWriter writer;
    private final Object lock = new Object();

    public HeartbeatConnection(Boolean isForBackup) throws IOException {
        this.socket = new Socket("localhost", isForBackup ? 8900 : 8888);
        this.writer = new PrintWriter(socket.getOutputStream(), true);
    }

    public void sendHeartbeat(int id) {
        synchronized (lock) {
            if (socket.isClosed()) {
                throw new IllegalStateException("Connection is closed");
            }
            long timestamp = System.currentTimeMillis();
            String heartbeatMessage = String.format("%d:%d", id, timestamp);
            writer.println(heartbeatMessage);
        }
    }

    public void triggerBackup(int id) {
        synchronized (lock) {
            if (socket.isClosed()) {
                throw new IllegalStateException("Connection is closed");
            }
            long timestamp = System.currentTimeMillis();
            String heartbeatMessage = String.format("%d:%d", id, timestamp);
            writer.println(heartbeatMessage);
        }
    }

    public void close() throws IOException {
        synchronized (lock) {
            writer.close();
            socket.close();
        }
    }
}
