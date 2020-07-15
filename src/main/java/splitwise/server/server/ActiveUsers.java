package splitwise.server.server;

import org.apache.log4j.Logger;
import splitwise.server.server.connection.ClientConnectionInfo;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static splitwise.server.server.SplitWiseServer.SEE_LOG_FILE;

public class ActiveUsers {

    private static Logger LOGGER = Logger.getLogger(ActiveUsers.class);

    private Map<Thread, ClientConnectionInfo> activeClients;

    public ActiveUsers(){
        this.activeClients = new ConcurrentHashMap<>();
    }

    public void addClient(Socket clientSocket){
        this.activeClients.put(Thread.currentThread(),new ClientConnectionInfo(clientSocket));
    }

    public void removeClient(){
        this.activeClients.remove(Thread.currentThread());
    }

    public String getUsernameOfCurrentClientConnection(){
        ClientConnectionInfo currentConnectionInfo = activeClients.get(Thread.currentThread());

        String username = currentConnectionInfo.getUsername();

        return username;
    }

    public void setUsernameForCurrentClientConnection(String username) {
        ClientConnectionInfo clientConnectionInfo = this.activeClients.get(Thread.currentThread());
        clientConnectionInfo.setUsername(username);
    }

    public void logoutClient() {
        ClientConnectionInfo clientConnectionInfo = this.activeClients.get(Thread.currentThread());
        clientConnectionInfo.setUsername(null);
    }

    public boolean isActive(String username) {
        return activeClients.values().stream().anyMatch(clientConnection -> clientConnection.hasUsername(username));
    }

    public void sendMessageToAll(String message) {
        for (ClientConnectionInfo clientConnection : activeClients.values()) {
            sendMessageToClient(clientConnection, message);
        }
    }

    public void sendMessageToUser(String username, String message) {
        for (ClientConnectionInfo clientConnectionInfo : activeClients.values()) {
            if (clientConnectionInfo.hasUsername(username)) {
                sendMessageToClient(clientConnectionInfo, message);
            }
        }
    }

    private void sendMessageToClient(ClientConnectionInfo clientConnection, String message) {
        Socket clientSocket = clientConnection.getSocket();
        String username = clientConnection.getUsername();
        String exceptionMessage = "Could not send message to client with username:" + username + ". ";

        try {
            new PrintWriter(clientSocket.getOutputStream(), true).println(message);
        } catch (IOException e) {
            LOGGER.info(exceptionMessage + SEE_LOG_FILE);
            LOGGER.error(exceptionMessage, e);
        }
    }

}
