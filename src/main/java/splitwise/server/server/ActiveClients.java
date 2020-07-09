package splitwise.server.server;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static splitwise.server.server.SplitWiseServer.SEE_LOG_FILE;

public class ActiveClients {

    private static Logger LOGGER = Logger.getLogger(ActiveClients.class);

    private Map<Thread, ClientConnectionInfo> activeClients;

    public ActiveClients(){
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

        System.out.println(currentConnectionInfo);

        String username = currentConnectionInfo.getUsername();

        return username;
    }

    public void setUsernameForCurrentClientConnection(String username){
        ClientConnectionInfo clientConnectionInfo = this.activeClients.get(Thread.currentThread());
        clientConnectionInfo.setUsername(username);
    }

    public void logoutClient(){
        ClientConnectionInfo clientConnectionInfo = this.activeClients.get(Thread.currentThread());
        clientConnectionInfo.setUsername(null);
    }


    public void sendMessageToAll(String message) {
        for(ClientConnectionInfo clientConnection:activeClients.values()) {
           sendMessageToActiveClient(clientConnection,message);
        }
    }

    private void sendMessageToActiveClient(ClientConnectionInfo clientConnection,String message){
        Socket clientSocket= clientConnection.getSocket();
        String username = clientConnection.getUsername();

        String exceptionMessage = "Could not send message to client with username:"+username+ ". ";

        try {
            clientSocket.getOutputStream().write(message.getBytes());
        }catch(IOException e){
            LOGGER.info(exceptionMessage+SEE_LOG_FILE);
            LOGGER.error(exceptionMessage,e);
        }
    }

}
