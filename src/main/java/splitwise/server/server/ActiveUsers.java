package splitwise.server.server;

import splitwise.server.server.connection.ClientConnectionInfo;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import static splitwise.server.SplitWiseApplication.LOGGER;

public class ActiveUsers {
    
    private final Map<Thread, ClientConnectionInfo> activeUsers;
    
    public ActiveUsers() {
	this.activeUsers = new HashMap<>();
    }
    
    public void addActiveUsersConnection(Socket socket) {
	this.activeUsers.put(Thread.currentThread(), new ClientConnectionInfo(socket));
    }
    
    public void removeUser() {
	this.activeUsers.remove(Thread.currentThread());
    }
    
    public String getUsernameOfCurrentConnection() {
	ClientConnectionInfo currentConnectionInfo = activeUsers.get(Thread.currentThread());
	String username = currentConnectionInfo.getUsername();
	
	return username;
    }
    
    public void setUsernameOfCurrentConnection(String username) {
	ClientConnectionInfo connectionInfo = this.activeUsers.get(Thread.currentThread());
	connectionInfo.setUsername(username);
    }
    
    public void logoutUser() {
	ClientConnectionInfo connectionInfo = this.activeUsers.get(Thread.currentThread());
	connectionInfo.setUsername(null);
    }
    
    public boolean isActive(String username) {
	return activeUsers.values().stream().anyMatch(connectionInfo -> connectionInfo.hasUsername(username));
    }
    
    public void sendMessageToUser(String username, String message) {
	for(Map.Entry<Thread, ClientConnectionInfo> activeUser : activeUsers.entrySet()) {
	    ClientConnectionInfo user = activeUser.getValue();
	    
	    if(user.hasUsername(username)) {
		sendMessage(user.getSocket(), message);
	    }
	}
    }
    
    private void sendMessage(Socket socket, String message) {
	synchronized(socket) {
	    try {
		OutputStream clientConnectionOutput = socket.getOutputStream();
		PrintWriter outputWriter = new PrintWriter(clientConnectionOutput, true);
		outputWriter.println(message);
	    } catch(IOException e) {
		LOGGER.info("Sending notification message to user failed. See error.log for more information.");
		LOGGER.error("Sending notification failed.", e);
	    }
	}
    }
    
    public void sendMessageToAll(String message) {
	for(ClientConnectionInfo activeUser : activeUsers.values()) {
	    Socket userSocket = activeUser.getSocket();
	    sendMessage(userSocket, message);
	}
    }
}
