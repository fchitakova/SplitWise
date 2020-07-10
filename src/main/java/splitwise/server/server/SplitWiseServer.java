package splitwise.server.server;

import org.apache.log4j.Logger;
import splitwise.server.commands.Command;
import splitwise.server.exceptions.*;
import splitwise.server.server.connection.ClientConnection;
import splitwise.server.services.CommandFactory;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class SplitWiseServer {
    public static final int MAXIMUM_CONNECTIONS_COUNT = 100;
    public static final String SEE_LOG_FILE = "See logging.log for more information.";

    private static final String CONNECTION_CANNOT_BE_ESTABLISHED="Connection cannot be established.";
    private static final String SERVER_STARTED = "SplitWise server started!";
    private static final String SOCKET_ACCEPT_ERROR="I/O error occurred while waiting for client connection.";
    private static final String CLOSING_SERVER_SOCKET_FAILED="IO error while trying to close server socket.";
    private static final String SERVER_STOPPED = "server stopped";

    private static Logger LOGGER = Logger.getLogger(ClientConnection.class);

    private ServerSocket serverSocket;
    private ActiveClients activeClients;
    private CommandFactory commandFactory;
    private ExecutorService executorService;

    public SplitWiseServer(ServerSocket serverSocket,ActiveClients activeClients, CommandFactory commandFactory){
        this.serverSocket = serverSocket;
        this.activeClients = activeClients;
        this.commandFactory = commandFactory;

        executorService = Executors.newFixedThreadPool(MAXIMUM_CONNECTIONS_COUNT);
        startServerAdministrationCommandExecutor();
    }

    private void startServerAdministrationCommandExecutor(){
        new Thread(new ServerAdministrationCommandExecutor(this)).start();
    }


    public void start(){
            System.out.println(SERVER_STARTED);
            while (!serverSocket.isClosed()) {
                acceptClientConnections();
            }
    }


    private void acceptClientConnections(){
         try {
            Socket clientSocket = getSocketConnection();
            if(clientSocket!=null) {
                ClientConnection clientConnection = new ClientConnection(clientSocket, this, activeClients);
                executorService.execute(clientConnection);
            }

        }catch (ServerSocketException | ClientConnectionException e){
            String logMessage = CONNECTION_CANNOT_BE_ESTABLISHED+"Reason: "+e.getMessage();
            LOGGER.info(logMessage+SEE_LOG_FILE);
            LOGGER.error(logMessage,e);
        }
    }

    private Socket getSocketConnection() throws ServerSocketException {
        Socket socket = null;
        try {
            socket = serverSocket.accept();
        } catch (IOException e) {
            if(!serverSocket.isClosed()) {
                throw new ServerSocketException(SOCKET_ACCEPT_ERROR, e);
            }
        }
        return socket;
    }


    public void stop(){
        notifyActiveUsersForShutdown();
        executorService.shutdownNow();
        try {
            serverSocket.close();
        } catch (IOException e) {
            LOGGER.info(CLOSING_SERVER_SOCKET_FAILED+SEE_LOG_FILE);
            LOGGER.error(CLOSING_SERVER_SOCKET_FAILED,e);
        }
    }

    private void notifyActiveUsersForShutdown(){
        activeClients.sendMessageToAll(SERVER_STOPPED);
    }

    public String executeUserCommand(String userInput){
        Command command = commandFactory.createCommand(userInput);
        String commandExecutionResult = command.execute();
        return commandExecutionResult;
    }

}
