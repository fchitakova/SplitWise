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
    private static final String SERVER_STOPPED = "server stopped";
    private static final String SERVER_WENT_DOWN = "SplitWise server went down. Try connecting later.";
    private static final String SERVER_RESPONSE_INDICATOR = "Server >>> ";
    private static final String EXIT_COMMAND = "exit";

    private static final Logger LOGGER = Logger.getLogger(SplitWiseClient.class);

    private boolean stoppedByClient;
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

          stoppedByClient = false;
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
        new Thread(()-> sendUserInputToServer()).start();
        new Thread(() -> printServerResponse()).start();
    }

    private void sendUserInputToServer(){
        String command = getUserInput();

        while(!exitCommandRead(command)) {
            sendUserInputToServer(command);
            command = getUserInput();
        }

        stoppedByClient = true;
        endSession();
    }

    private void printServerResponse() {
        while(!socket.isClosed()) {
            try {
                String serverResponse = serverInputReader.readLine();
                printServerResponse(serverResponse);
            } catch (IOException e) {
                if(!stoppedByClient){
                    LOGGER.info(FAIL_READING_SERVER_INPUT_INFO_MESSAGE);
                    LOGGER.error(FAIL_READING_SERVER_INPUT_ERROR_MESSAGE, e);
                }
            }
        }
    }

    private String getUserInput(){
        String command = null;
        try{
            command = userInputReader.readLine();

        }catch (IOException e){
            LOGGER.info(FAILED_READING_USER_INPUT_INFO_MESSAGE);
            LOGGER.error(FAILED_READING_USER_INPUT_ERROR_MESSAGE,e);
        }
        return command;
    }

    private void sendUserInputToServer(String input){
        if(input!=null){
            serverOutputWriter.println(input);
        }
    }

    private boolean exitCommandRead(String command){
        return command.equals(EXIT_COMMAND);
    }

    private void printServerResponse(String serverResponse) {
        if (serverResponse.equals(SERVER_STOPPED)) {
            System.out.println(SERVER_WENT_DOWN);
            closeSocketConnection();
        } else {
            System.out.println(SERVER_RESPONSE_INDICATOR + serverResponse);
        }
    }



    private void endSession(){
        closeSocketConnection();
        closeUserInputReader();
    }

    private void closeSocketConnection(){
        try{
             socket.close();
        }catch (IOException e){
            LOGGER.error(FAILED_SOCKET_CLOSE_ERROR_MESSAGE,e);
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
