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
import java.util.concurrent.atomic.AtomicReference;


public class SplitWiseServer {
    public static final int MAXIMUM_CONNECTIONS_COUNT = 100;

    public static final String SERVER_STARTED = "SplitWise server started!";
    public static final String SERVER_STOPPED = "app stopped";

    private static Logger LOGGER = Logger.getLogger(ClientConnection.class);

    private ServerSocket serverSocket;
    private ActiveUsers activeUsers;
    private CommandFactory commandFactory;
    private ExecutorService executorService;

    public SplitWiseServer(ServerSocket serverSocket, ActiveUsers activeUsers, CommandFactory commandFactory) {
        this.serverSocket = serverSocket;
        this.activeUsers = activeUsers;
        this.commandFactory = commandFactory;

        executorService = Executors.newFixedThreadPool(MAXIMUM_CONNECTIONS_COUNT);
        startServerAdministrationCommandExecutor();
    }

    private void startServerAdministrationCommandExecutor() {
        new Thread(new ServerAdministrationCommandExecutor(this)).start();
    }


    public void start() {
        System.out.println(SERVER_STARTED);
        while (isServerRunning()) {
            acceptClientConnections();
        }
    }

    private void acceptClientConnections() {
        try {
            Socket clientSocket = serverSocket.accept();
            ClientConnection clientConnection = new ClientConnection(clientSocket, this);
            executorService.execute(clientConnection);
        } catch (IOException | ClientConnectionException e) {
            if (isServerRunning()) {
                LOGGER.info("Error during establishing client connection. See logging.log for more information.");
                LOGGER.error("Error during establishing client connection." + "Reason: " + e.getMessage(), e);
            }
        }
    }


    private boolean isServerRunning() {
        return !serverSocket.isClosed();
    }


    public void stop() {
        notifyActiveUsersForShutdown();
        executorService.shutdownNow();
        try {
            serverSocket.close();
        } catch (IOException e) {
            LOGGER.info("IO error while closing server socket.See logging.log for more information.");
            LOGGER.error(e);
        }
    }


    private void notifyActiveUsersForShutdown() {
        activeUsers.sendMessageToAll(SERVER_STOPPED);
    }

    public void addActiveClientConnection(Socket socket) {
        this.activeUsers.addActiveUsersConnection(socket);
    }

    public void removeClientConnection() {
        activeUsers.removeUser();
    }

    public String executeUserCommand(String userInput) {
        String trimmedUserInput = userInput.trim();
        Command command = commandFactory.createCommand(trimmedUserInput);
        String commandExecutionResult = command.execute();
        return commandExecutionResult;
    }

}
