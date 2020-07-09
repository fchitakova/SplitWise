package splitwise.server.server;


import org.apache.log4j.Logger;
import splitwise.server.exceptions.ClientConnectionException;

import java.io.*;
import java.net.Socket;



public class ClientConnection extends Thread{
    private static final String ERROR_READING_SOCKET_INPUT = "Error reading client socket input.";
    private static final String ERROR_DURING_GETTING_SOCKET_IO_STREAMS = "Error occurred during getting client socket I/O streams.";
    private static final String ERROR_DURING_CLIENT_CONNECTION_CONSTRUCTION="ClientConnection cannot be constructed.";
    private static final String WELCOME_MESSAGE = "Welcome to SplitWise!";
    private static final String ERROR_CLOSING_SOCKET = "Cannot close client socket connection because of I/O exception.";

    private static Logger LOGGER = Logger.getLogger(ClientConnection.class);

    private Socket socket;
    private BufferedReader socketInputReader;
    private PrintWriter socketOutputWriter;
    private SplitWiseServer splitWiseServer;
    private ActiveClients activeClients;

    public ClientConnection(Socket socket,SplitWiseServer splitWiseServer,ActiveClients activeClients) throws ClientConnectionException{
        this.socket = socket;
        this.activeClients = activeClients;
        this.splitWiseServer = splitWiseServer;
        try {
            initializeSocketIOStreams();
        }catch(IOException e){
            throw new ClientConnectionException(ERROR_DURING_CLIENT_CONNECTION_CONSTRUCTION+"Reason: "+e.getMessage(),e);
        }
        sendMessageToClient(WELCOME_MESSAGE);
    }

    private void initializeSocketIOStreams() throws IOException {
        try {
            socketInputReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            socketOutputWriter = new PrintWriter(socket.getOutputStream(), true);
        }catch (IOException e){
            closeSocketConnection();
            throw new IOException(ERROR_DURING_GETTING_SOCKET_IO_STREAMS,e);
        }
    }

    @Override
    public void run() {
        activeClients.addClient(socket);

        while (!socket.isClosed()) {
           handleClientCommunication();
        }
        activeClients.removeClient();
    }

    private void handleClientCommunication(){
        try {
            String userInput = readClientInput();
            if(userInput == null){
                closeSocketConnection();
            }else {
                String serverResponse = splitWiseServer.executeUserCommand(userInput);
                sendMessageToClient(serverResponse);
            }
        } catch (ClientConnectionException e) {
            closeSocketConnection();
            LOGGER.info(e.getMessage());
            LOGGER.error(e.getMessage(), e);
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

    private void closeSocketConnection(){
        try{
             socket.close();
        }catch (IOException e){
            LOGGER.error(ERROR_CLOSING_SOCKET,e);
        }
    }

}
