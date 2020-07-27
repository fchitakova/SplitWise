package splitwise.server.server.connection;


import org.apache.log4j.Logger;
import splitwise.server.exceptions.ClientConnectionException;
import splitwise.server.server.SplitWiseServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicReference;


public class ClientConnection extends Thread{
    private static final String WELCOME_MESSAGE = "Welcome to SplitWise!";

    private static Logger LOGGER = Logger.getLogger(ClientConnection.class);

    private Socket socket;
    private BufferedReader socketInputReader;
    private PrintWriter socketOutputWriter;
    private SplitWiseServer splitWiseServer;

    public ClientConnection(Socket socket, SplitWiseServer splitWiseServer) throws ClientConnectionException {
        setUpSocketConnection(socket);
        this.splitWiseServer = splitWiseServer;

        sendMessageToClient(WELCOME_MESSAGE);
    }

    private void setUpSocketConnection(Socket socket) throws ClientConnectionException {
        try {
            socketInputReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            socketOutputWriter = new PrintWriter(socket.getOutputStream(), true);
            this.socket = socket;
        } catch (IOException e) {
            closeSocketConnection();
            throw new ClientConnectionException("Error occurred during getting client socket I/O streams.", e);
        }
    }

    @Override
    public void run() {
        splitWiseServer.addActiveClientConnection(socket);

        while (isConnectionAlive()) {
            processClientCommands();
        }

        splitWiseServer.removeClientConnection();
    }

    private boolean isConnectionAlive() {
        return !socket.isClosed();
    }

    private void processClientCommands() {
        String userInput = readClientInput();

        if (isValid(userInput) && isConnectionAlive()) {
            String serverResponse = splitWiseServer.executeUserCommand(userInput);
            sendMessageToClient(serverResponse);
        }
    }

    private String readClientInput() {
        String input = "";
        try {
            input = socketInputReader.readLine();
        } catch (IOException e) {
            closeSocketConnection();
            LOGGER.info("Error reading client socket input.");
            LOGGER.error("Error reading client socket input.", e);
        }
        return input;
    }

    private boolean isValid(String userInput) {
        return userInput != null;
    }


    private void sendMessageToClient(String response) {
        socketOutputWriter.println(response);
    }

    private void closeSocketConnection() {
        try {
            socket.close();
        } catch (IOException e) {
            LOGGER.info("I/O exception while closing client socket.");
            LOGGER.error(e.getMessage(), e);
        }
    }

}
