package splitwise.server.server;

import org.apache.log4j.Logger;
import splitwise.server.server.connection.ConnectionInfo;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static splitwise.server.server.SplitWiseServer.SEE_LOG_FILE;

public class ActiveUsers {

    private static Logger LOGGER = Logger.getLogger(ActiveUsers.class);

    private Map<Thread, ConnectionInfo> activeUsers;

    public ActiveUsers() {
        this.activeUsers = new ConcurrentHashMap<>();
    }

    public void addActiveUsersConnection(Socket socket) {
        this.activeUsers.put(Thread.currentThread(), new ConnectionInfo(socket));
    }

    public void removeUser() {
        this.activeUsers.remove(Thread.currentThread());
    }

    public String getUsernameOfCurrentConnection() {
        ConnectionInfo currentConnectionInfo = activeUsers.get(Thread.currentThread());

        String username = currentConnectionInfo.getUsername();

        return username;
    }

    public void setUsernameOfCurrentConnection(String username) {
        ConnectionInfo connectionInfo = this.activeUsers.get(Thread.currentThread());
        connectionInfo.setUsername(username);
    }

    public void logoutUser() {
        ConnectionInfo connectionInfo = this.activeUsers.get(Thread.currentThread());
        connectionInfo.setUsername(null);
    }

    public boolean isActive(String username) {
        return activeUsers.values().stream().anyMatch(connectionInfo -> connectionInfo.hasUsername(username));
    }

    public void sendMessageToUser(String username, String message) {
        for (ConnectionInfo connectionInfo : activeUsers.values()) {
            if (connectionInfo.hasUsername(username)) {
                sendMessage(connectionInfo, message);
            }
        }
    }

    private void sendMessage(ConnectionInfo connectionInfo, String message) {
        try {
            Socket socket = connectionInfo.getSocket();
            new PrintWriter(socket.getOutputStream(), true).println(message);
        } catch (IOException e) {
            String exceptionMessage = "Cannot send message to client with username:" + connectionInfo.getUsername() + ". ";
            LOGGER.info(exceptionMessage + SEE_LOG_FILE);
            LOGGER.error(exceptionMessage, e);
        }
    }

    public void sendMessageToAll(String message) {
        for (ConnectionInfo connectionInfo : activeUsers.values()) {
            sendMessage(connectionInfo, message);
        }
    }

}
