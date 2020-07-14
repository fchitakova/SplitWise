package splitwise.server.server;

import org.apache.log4j.Logger;
import splitwise.server.commands.Command;
import splitwise.server.exceptions.ClientConnectionException;
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
    public static final String CONNECTION_CANNOT_BE_ESTABLISHED = "Error during establishing client connection. ";
    public static final String SERVER_STARTED = "SplitWise server started!";
    public static final String CLOSING_SERVER_SOCKET_FAILED = "IO error while trying to close server socket.";
    public static final String SERVER_STOPPED = "server stopped";

    private static Logger LOGGER = Logger.getLogger(ClientConnection.class);

    private ServerSocket serverSocket;
    private ActiveClients activeClients;
    private CommandFactory commandFactory;
    private ExecutorService executorService;

    public SplitWiseServer(ServerSocket serverSocket, ActiveClients activeClients, CommandFactory commandFactory) {
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


    private void acceptClientConnections() {
        try {
            Socket clientSocket = serverSocket.accept();
            ClientConnection clientConnection = new ClientConnection(clientSocket, this);
            executorService.execute(clientConnection);
        } catch (IOException | ClientConnectionException e) {
            if (serverSocket.isClosed()) {
                return;
            }
            LOGGER.info(CONNECTION_CANNOT_BE_ESTABLISHED + SEE_LOG_FILE);
            LOGGER.error(e.getMessage(), e);
        }
    }



    public void stop(){
        notifyActiveUsersForShutdown();
        executorService.shutdownNow();
        try {
            serverSocket.close();
        } catch (IOException e) {
            LOGGER.info(CLOSING_SERVER_SOCKET_FAILED + SEE_LOG_FILE);
            LOGGER.error(CLOSING_SERVER_SOCKET_FAILED, e);
        }
    }


    private void notifyActiveUsersForShutdown() {
        activeClients.sendMessageToAll(SERVER_STOPPED);
    }

    public void addActiveClientConnection(Socket socket) {
        this.activeClients.addClient(socket);
    }

    public void removeClientConnection() {
        activeClients.removeClient();
    }

    public String executeUserCommand(String userInput) {
        String trimmedUserInput = userInput.trim();
        Command command = commandFactory.createCommand(trimmedUserInput);
        String commandExecutionResult = command.execute();
        return commandExecutionResult;
    }

}
