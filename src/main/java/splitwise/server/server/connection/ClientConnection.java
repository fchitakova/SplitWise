package splitwise.server.server.connection;


import org.apache.log4j.Logger;
import splitwise.server.exceptions.ClientConnectionException;
import splitwise.server.server.SplitWiseServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;


public class ClientConnection extends Thread{
    private static final String ERROR_READING_SOCKET_INPUT = "Error reading client socket input.";
    private static final String ERROR_DURING_GETTING_SOCKET_IO_STREAMS = "Error occurred during getting client socket I/O streams.";
    private static final String WELCOME_MESSAGE = "Welcome to SplitWise!";
    private static final String ERROR_CLOSING_SOCKET = "Cannot close client socket connection because of I/O exception.";

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
            throw new ClientConnectionException(ERROR_DURING_GETTING_SOCKET_IO_STREAMS, e);
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
            LOGGER.info(ERROR_READING_SOCKET_INPUT);
            LOGGER.error(ERROR_READING_SOCKET_INPUT, e);
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
            LOGGER.error(ERROR_CLOSING_SOCKET, e);
        }
    }

}
