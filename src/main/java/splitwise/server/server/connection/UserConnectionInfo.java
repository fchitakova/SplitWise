package splitwise.server.server.connection;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class UserConnectionInfo {
    private Socket socket;
    private String username;

    public UserConnectionInfo(Socket clientSocket) {
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
