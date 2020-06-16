package splitwise.client;

import java.io.*;
import java.net.Socket;
import org.apache.log4j.Logger;

public class SplitWiseClient {
    private static final String SERVER_HOST = "127.0.0.1";
    private static final int SERVER_PORT = 8080;

    private static final String SEE_LOG_FILES = "Contact administrator by providing the logs in logging.log";
    private static final String CONNECTING_TO_SERVER_FAILED_INFO_MESSAGE = "Unable to connect to the server. " + SEE_LOG_FILES;
    private static final String FAIL_READING_SERVER_INPUT_INFO_MESSAGE = "Error occurred when getting server response. "+SEE_LOG_FILES;
    private static final String FAILED_READING_USER_INPUT_INFO_MESSAGE = "Cannot get input because of error.Connection closed. "+SEE_LOG_FILES;

    private static final String FAILED_SOCKET_CREATION_ERROR_MESSAGE = "Socket creation failed because of IO exception.";
    private static final String FAILED_SOCKET_CLOSE_ERROR_MESSAGE = "Cannot close socket connection because of I/O exception.";
    private static final String FAILED_GET_SOCKET_IO_STREAMS_ERROR_MESSAGE = "Error occurred during getting socket IO streams.";
    private static final String FAIL_READING_SERVER_INPUT_ERROR_MESSAGE = "IO error occurred while trying to read server input stream.";
    private static final String FAILED_CLOSING_CONSOLE_READER_ERROR_MESSAGE = "IO error occurred while closing user input reader.";
    private static final String FAILED_READING_USER_INPUT_ERROR_MESSAGE ="IO error occurred while reading user input";

    private static final String LOGOUT_COMMAND = "logout";

    private static final Logger LOGGER = Logger.getLogger(SplitWiseClient.class);

    private Socket socket;
    private BufferedReader userInputReader;
    private BufferedReader serverInputReader;
    private PrintWriter serverOutputWriter;


    public static void main(String[]args)  {
        SplitWiseClient splitWiseClient;
        try {
             splitWiseClient = new SplitWiseClient(SERVER_HOST,SERVER_PORT);
        }catch(IOException e){
            LOGGER.info(CONNECTING_TO_SERVER_FAILED_INFO_MESSAGE);
            LOGGER.error(e.getMessage(),e);
            return;
        }
        splitWiseClient.start();
    }

    public SplitWiseClient(String host,int port) throws IOException {
          createSocketConnection(host, port);
          initializeSocketIOStreams();
          userInputReader = new BufferedReader(new InputStreamReader(System.in));
    }


    private void createSocketConnection(String host,int port) throws IOException {
        try{
             socket = new Socket(host, port);
        }catch (IOException e){
            throw new IOException(FAILED_SOCKET_CREATION_ERROR_MESSAGE,e);
        }
    }


    private void initializeSocketIOStreams() throws IOException {
        try {
            serverInputReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            serverOutputWriter = new PrintWriter(socket.getOutputStream(), true);
        }catch(IOException e){
            throw new IOException(FAILED_GET_SOCKET_IO_STREAMS_ERROR_MESSAGE,e);
        }
    }

    public void start(){
        Thread userInputReader = new Thread(()->readUserInput());
        Thread serverInputReader = new Thread(() -> readServerInput());
        userInputReader.start();
        serverInputReader.start();
    }


    private void readServerInput() {
        while(!socket.isClosed()) {
            try {
                String serverResponse = serverInputReader.readLine();
                System.out.println("Server >>> " + serverResponse);
            } catch (IOException e) {
                endSession();
                LOGGER.info(FAIL_READING_SERVER_INPUT_INFO_MESSAGE);
                LOGGER.error(FAIL_READING_SERVER_INPUT_ERROR_MESSAGE, e);
            }
        }
    }

    private void readUserInput(){
        try {
            String command;
            do {
                command = userInputReader.readLine();
                serverOutputWriter.println(command);
            } while (!command.equalsIgnoreCase(LOGOUT_COMMAND));
        }catch (IOException e){
            endSession();
            LOGGER.info(FAILED_READING_USER_INPUT_INFO_MESSAGE);
            LOGGER.error(FAILED_READING_USER_INPUT_ERROR_MESSAGE,e);
        }
    }

    private void endSession(){
        closeSocketConnection();
        closeUserInputReader();
    }

    private void closeSocketConnection(){
        try{
             socket.close();
        }catch (IOException ioException){
            LOGGER.error(FAILED_SOCKET_CLOSE_ERROR_MESSAGE,ioException);
        }
    }

    private void closeUserInputReader(){
        try{
            userInputReader.close();
        }catch(IOException e){
            LOGGER.error(FAILED_CLOSING_CONSOLE_READER_ERROR_MESSAGE,e);
        }
    }

}
