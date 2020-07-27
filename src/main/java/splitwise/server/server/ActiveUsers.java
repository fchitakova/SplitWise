package splitwise.server.server;

import org.apache.log4j.Logger;
import splitwise.server.server.connection.ConnectionInfo;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;



public class ActiveUsers {

    private static Logger LOGGER = Logger.getLogger(ActiveUsers.class);

    private Map<Thread, ConnectionInfo> activeUsers;

    public ActiveUsers() {
        this.activeUsers = new HashMap<>();
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
        return activeUsers.values().
                stream().
                anyMatch(connectionInfo -> connectionInfo.hasUsername(username));
    }

    public void sendMessageToUser(String username, String message) {
        this.activeUsers.
                values().
                stream().
                filter(connectionInfo -> connectionInfo.hasUsername(username)).
                forEach(connectionInfo -> sendMessage(connectionInfo, username));
    }

    private void sendMessage(ConnectionInfo connectionInfo, String message) {
        synchronized (connectionInfo) {
            try {
                OutputStream clientConnectionOutput = connectionInfo.getClientConnectionOutputStream();
                PrintWriter outputWriter = new PrintWriter(clientConnectionOutput, true);
                outputWriter.println(message);
            } catch (IOException e) {
                String exceptionMessage = "Cannot send message to client with username:" + connectionInfo.getUsername() + ". ";
                LOGGER.info(exceptionMessage + "See logging.log for more information.");
                LOGGER.error(exceptionMessage, e);
            }
        }
    }


    public void sendMessageToAll(String message) {
        for (ConnectionInfo connectionInfo : activeUsers.values()) {
            sendMessage(connectionInfo, message);
        }
    }

}
