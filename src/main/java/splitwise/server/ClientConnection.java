package splitwise.server;


import org.apache.log4j.Logger;

import java.io.*;
import java.net.Socket;



public class ClientConnection implements Runnable{
    private static final String ERROR_READING_SOCKET_INPUT = "Error reading socket input.";
    private static final String ERROR_DURING_GETTING_SOCKET_IO_STREAMS = "Error occurred during getting client socket I/O streams.";

    private static Logger LOGGER = Logger.getLogger(ClientConnection.class);

    private Socket socket;
    private BufferedReader socketInputReader;
    private PrintWriter socketOutputWriter;
    private SplitWiseServer splitWiseServer;

    public ClientConnection(Socket socket,SplitWiseServer splitWiseServer) throws IOException {
        this.socket = socket;
        this.splitWiseServer = splitWiseServer;
        initializeSocketIOStreams();
    }

    private void initializeSocketIOStreams() throws IOException {
        try {
            socketInputReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            socketOutputWriter = new PrintWriter(socket.getOutputStream(), true);
        }catch (IOException e){
            throw new IOException(ERROR_DURING_GETTING_SOCKET_IO_STREAMS,e);
        }finally {
            cleanUpConnectionResources();
        }
    }


    public void run() {
        try {
            while (!socket.isClosed()) {
                String userInput = readClientInput();
                String serverResponse = splitWiseServer.processUserInput(userInput);
                sendServerResponse(serverResponse);
            }
        } catch (IOException e) {
            LOGGER.info(e.getMessage());
            LOGGER.error(e.getMessage(),e);
        }finally {
            cleanUpConnectionResources();
        }
    }

    private String readClientInput() throws IOException {
        String input;
        try{
            input = socketInputReader.readLine();
        }catch(IOException e){
            throw  new IOException(ERROR_READING_SOCKET_INPUT,e);
        }
        return input;
    }

    private void sendServerResponse(String response) {
        socketOutputWriter.println(response);
    }

    private void cleanUpConnectionResources(){
        UserContextHolder.usernameHolder.remove();
        closeSocketConnection();
    }

    private void closeSocketConnection(){
        try{
             socket.close();
        }catch (IOException ioException){
            LOGGER.error(SplitWiseServer.ERROR_CLOSING_SOCKET,ioException);
        }
    }

}
