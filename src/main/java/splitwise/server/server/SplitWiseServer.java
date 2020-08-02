package splitwise.server.server;


import logger.Logger;
import splitwise.server.commands.Command;
import splitwise.server.exceptions.ClientConnectionException;
import splitwise.server.server.connection.ClientConnection;
import splitwise.server.services.CommandFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static splitwise.server.SplitWiseApplication.LOGGER;


public class SplitWiseServer {
    public static final int MAXIMUM_CONNECTIONS_COUNT = 100;

    public static final String SERVER_STARTED = "SplitWise server started!";
    public static final String SERVER_STOPPED = "app stopped";

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
        new ServerAdminCommandExecutor(this).start();
    }


    public void start() {
        System.out.println(SERVER_STARTED);

        while (isRunning()) {
            acceptClientConnections();
        }
    }

    public boolean isRunning() {
        return !serverSocket.isClosed();
    }

    private void acceptClientConnections() {
        try {
            Socket clientSocket = serverSocket.accept();
            ClientConnection clientConnection = new ClientConnection(clientSocket, this);
            executorService.execute(clientConnection);
        } catch (IOException | ClientConnectionException e) {
            if (isRunning()) {
                LOGGER.info("Error during establishing client connection. See error.log for more information.");
                LOGGER.error("Error during establishing client connection." + "Reason: " + e.getMessage(), e);
            }
        }
    }


    public void stop() {
        notifyActiveUsersForShutdown();
        executorService.shutdownNow();

        try {
            serverSocket.close();
        } catch (IOException e) {
            LOGGER.info("IO error while closing server socket.See error.log for more information.");
            LOGGER.error(e.getMessage(), e);
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
        Command command = commandFactory.getCommand(trimmedUserInput);

        String commandExecutionResult = command.execute();

        return commandExecutionResult;
    }

}
