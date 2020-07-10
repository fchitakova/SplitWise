package splitwise.server.server.connection;

import java.net.Socket;

public class ClientConnectionInfo {
    private Socket clientSocket;
    private String username;

    public ClientConnectionInfo(Socket clientSocket){
        this.clientSocket = clientSocket;
    }

    public void setUsername(String username){
        this.username = username;
    }

    public String getUsername(){
        return this.username;
    }

    public Socket getSocket(){
        return clientSocket;
    }

}
