package splitwise.server.server;

import org.apache.log4j.Logger;
import splitwise.server.server.connection.UserConnectionInfo;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;



public class ActiveUsers {

    private static final Logger LOGGER = Logger.getLogger(ActiveUsers.class);

    private final Map<Thread, UserConnectionInfo> activeUsers;

    public ActiveUsers() {
        this.activeUsers = new HashMap<>();
    }

    public void addActiveUsersConnection(Socket socket) {
        this.activeUsers.put(Thread.currentThread(), new UserConnectionInfo(socket));
    }

    public void removeUser() {
        this.activeUsers.remove(Thread.currentThread());
    }

    public String getUsernameOfCurrentConnection() {
        UserConnectionInfo currentConnectionInfo = activeUsers.get(Thread.currentThread());
        String username = currentConnectionInfo.getUsername();

        return username;
    }

    public void setUsernameOfCurrentConnection(String username) {
        UserConnectionInfo connectionInfo = this.activeUsers.get(Thread.currentThread());
        connectionInfo.setUsername(username);
    }

    public void logoutUser() {
        UserConnectionInfo connectionInfo = this.activeUsers.get(Thread.currentThread());
        connectionInfo.setUsername(null);
    }

    public boolean isActive(String username) {
        return activeUsers.values().
                stream().
                anyMatch(connectionInfo -> connectionInfo.hasUsername(username));
    }

    public void sendMessageToUser(String username, String message) {
        for (Map.Entry<Thread, UserConnectionInfo> activeUser : activeUsers.entrySet()) {
            UserConnectionInfo user = activeUser.getValue();

            if (user.hasUsername(username)) {
                sendMessage(user.getSocket(), message);
            }
        }
    }

    private void sendMessage(Socket socket, String message) {
        synchronized (socket) {
            try {
                OutputStream clientConnectionOutput = socket.getOutputStream();
                PrintWriter outputWriter = new PrintWriter(clientConnectionOutput, true);
                outputWriter.println(message);
            } catch (IOException e) {
                LOGGER.info("Sending notification message to user failed. See logging.log for more information.");
                LOGGER.error("Sending notification failed.", e);
            }
        }
    }


    public void sendMessageToAll(String message) {
        for (UserConnectionInfo activeUser : activeUsers.values()) {
            Socket userSocket = activeUser.getSocket();
            sendMessage(userSocket, message);
        }
    }

}
