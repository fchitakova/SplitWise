package splitwise.server.server.connection;

import java.net.Socket;

public class ClientConnectionInfo {
    private Socket socket;
    private String username;
    
    public ClientConnectionInfo(Socket clientSocket) {
	this.socket = clientSocket;
    }
    
    public String getUsername() {
	return this.username;
    }
    
    public void setUsername(String username) {
	this.username = username;
    }
    
    public boolean hasUsername(String username) {
	return this.username.equals(username);
    }
    
    public Socket getSocket() {
	return socket;
    }
}
