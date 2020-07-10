package splitwise.client;

import java.io.*;
import java.net.Socket;
import org.apache.log4j.Logger;

public class SplitWiseClientApplication {
    public static final String SERVER_HOST = "127.0.0.1";
    public static final int SERVER_PORT = 8080;

    public static final String SEE_LOG_FILES = "For more information see logs in logging.log";
    private static final String CONNECTING_TO_SERVER_FAILED_INFO_MESSAGE = "Unable to connect to the server.It may be shut down. " + SEE_LOG_FILES;

    private static final String FAILED_SOCKET_CREATION_ERROR_MESSAGE = "Socket creation failed because of IO exception.";
    private static final String FAILED_SOCKET_CLOSE_ERROR_MESSAGE = "Cannot close socket connection because of I/O exception.";
    private static final String FAILED_GET_SOCKET_IO_STREAMS_ERROR_MESSAGE = "Error occurred during getting socket IO streams.";


    private static final Logger LOGGER = Logger.getLogger(SplitWiseClientApplication.class);

    private Socket socket;
    private BufferedReader userInputReader;
    private BufferedReader serverInputReader;
    private PrintWriter serverOutputWriter;


    public static void main(String[]args)  {
        try {
            SplitWiseClientApplication splitWiseClient = new SplitWiseClientApplication(SERVER_HOST,SERVER_PORT);
            splitWiseClient.start();
        }catch(IOException e){
            LOGGER.info(CONNECTING_TO_SERVER_FAILED_INFO_MESSAGE);
            LOGGER.error(e.getMessage(),e);
        }
    }

    public SplitWiseClientApplication(String host, int port) throws IOException {
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
       ConsoleInputReader consoleInputReader = new  ConsoleInputReader(userInputReader,serverOutputWriter,this);
       new Thread(consoleInputReader).start();

       ServerInputReader serverInputReader = new ServerInputReader(this.serverInputReader,this);
       new Thread(serverInputReader).start();
    }


    public void stop(){
        try{
             socket.close();
        }catch (IOException e){
            LOGGER.error(FAILED_SOCKET_CLOSE_ERROR_MESSAGE,e);
        }
    }

    public boolean isStopped(){
        return socket.isClosed();
    }

}
