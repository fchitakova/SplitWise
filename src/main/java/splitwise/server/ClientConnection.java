package splitwise.server;


import org.apache.log4j.Logger;
import splitwise.server.exceptions.ClientConnectionException;

import java.io.*;
import java.net.Socket;



public class ClientConnection implements Runnable{
    private static final String ERROR_READING_SOCKET_INPUT = "Error reading client socket input.";
    private static final String ERROR_DURING_GETTING_SOCKET_IO_STREAMS = "Error occurred during getting client socket I/O streams.";
    private static final String ERROR_DURING_CLIENT_CONNECTION_CONSTRUCTION="ClientConnection cannot be constructed.";
    private static final String WELCOME_MESSAGE = "Welcome to SplitWise!";

    private static Logger LOGGER = Logger.getLogger(ClientConnection.class);

    private Socket socket;
    private BufferedReader socketInputReader;
    private PrintWriter socketOutputWriter;
    private SplitWiseServer splitWiseServer;

    public ClientConnection(Socket socket,SplitWiseServer splitWiseServer) throws ClientConnectionException{
        this.socket = socket;
        this.splitWiseServer = splitWiseServer;
        try {
            initializeSocketIOStreams();
        }catch(IOException e){
            throw new ClientConnectionException(ERROR_DURING_CLIENT_CONNECTION_CONSTRUCTION+e.getMessage(),e);
        }
        sendMessageToClient(WELCOME_MESSAGE);
    }

    private void initializeSocketIOStreams() throws IOException {
        try {
            socketInputReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            socketOutputWriter = new PrintWriter(socket.getOutputStream(), true);
        }catch (IOException e){
            cleanUpConnectionResources();
            throw new IOException(ERROR_DURING_GETTING_SOCKET_IO_STREAMS,e);
        }
    }

    public void run() {
        while (!socket.isClosed()) {
            try {
                String userInput = readClientInput();
                String serverResponse = splitWiseServer.processUserInput(userInput);
                sendMessageToClient(serverResponse);
            } catch (ClientConnectionException e) {
                cleanUpConnectionResources();
                LOGGER.info(e.getMessage());
                LOGGER.error(e.getMessage(), e);
            }
        }
    }

    private String readClientInput() throws ClientConnectionException {
        String input;
        try{
            input = socketInputReader.readLine();
        }catch(IOException e){
            throw new ClientConnectionException(ERROR_READING_SOCKET_INPUT,e);
        }
        return input;
    }

    private void sendMessageToClient(String response) {
        socketOutputWriter.println(response);
    }

    private void cleanUpConnectionResources(){
        UserContextHolder.usernameHolder.remove();
        closeSocketConnection();
    }

    private void closeSocketConnection(){
        try{
             socket.close();
        }catch (IOException e){
            LOGGER.error(SplitWiseServer.ERROR_CLOSING_SOCKET,e);
        }
    }

}
