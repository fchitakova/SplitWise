package splitwise.server;

import org.apache.log4j.Logger;
import splitwise.server.commands.Command;
import splitwise.server.exceptions.ClientConnectionException;
import splitwise.server.exceptions.ServerSocketException;
import splitwise.server.exceptions.UserServiceException;
import splitwise.server.services.CommandFactory;
import splitwise.server.services.UserService;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class SplitWiseServer {
    public static final String ERROR_CLOSING_SOCKET = "Cannot close client socket connection because of I/O exception.";

    private static final int SERVER_PORT = 8080;
    private static final int MAXIMUM_CONNECTIONS_COUNT = 100;
    private static final String CONNECTION_CANNOT_BE_ESTABLISHED="Connection cannot be established.Reason: ";
    private static final String FAILED_SERVER_SOCKET_CREATION =
            "Split Wise Server instantiation failed because of I/O exception during server socket creation!" ;
    private static final String SERVER_STARTED = "SplitWise server started!";
    private static final String SOCKET_ACCEPT_ERROR="I/O error occurred while waiting for client connection.";
    private static final String FAILED_USER_SERVICE_CREATION = """
            Split Wise Server instantiation failed. Reason:
            CommandFactory cannot be instantiated because of UserServiceException
            during User Service instantiation.
            See logging.log for more information.""";
    private static final String CLOSING_SERVER_SOCKET_FAILED="IO error while trying to close server socket.";

    private static Logger LOGGER = Logger.getLogger(ClientConnection.class);

    private ServerSocket serverSocket;
    private ExecutorService executorService;
    private CommandFactory commandFactory;

    public static void main(String[]args){
        try{
            SplitWiseServer splitWiseServer = new SplitWiseServer(SERVER_PORT);
            splitWiseServer.start();
        }catch(ServerSocketException | UserServiceException e){
            LOGGER.info(e.getMessage());
            LOGGER.fatal(e.getMessage(),e);
        }

    }

    public SplitWiseServer(int port) throws ServerSocketException, UserServiceException {
        createServerSocket(port);
        createCommandFactory();
        executorService = Executors.newFixedThreadPool(MAXIMUM_CONNECTIONS_COUNT);
    }

    private void createServerSocket(int port) throws ServerSocketException {
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            throw new ServerSocketException(FAILED_SERVER_SOCKET_CREATION,e);
        }
    }

    private void createCommandFactory() throws UserServiceException {
        UserService userService;
        try {
            userService = new UserService();
        } catch (UserServiceException e) {
            throw new UserServiceException(FAILED_USER_SERVICE_CREATION,e);
        }
        commandFactory = new CommandFactory(userService);
    }


    public void start(){
        System.out.println(SERVER_STARTED);
        while(!serverSocket.isClosed()) {
            submitClientConnections();
        }
    }

    private void submitClientConnections(){
         try {
            Socket clientSocket = getSocketConnection();
            ClientConnection clientConnection = new ClientConnection(clientSocket, this);
            executorService.execute(clientConnection);
        }catch (ServerSocketException | ClientConnectionException e){
            String logMessage =CONNECTION_CANNOT_BE_ESTABLISHED+e.getMessage();
            LOGGER.info(logMessage);
            LOGGER.error(logMessage,e);
        }
    }

    private Socket getSocketConnection() throws ServerSocketException {
        Socket socket;
        try {
            socket = serverSocket.accept();
        }catch (IOException e) {
            throw new ServerSocketException(SOCKET_ACCEPT_ERROR,e);
        }
        return socket;
    }

    public String processUserInput(String input){
        Command command = commandFactory.createCommand(input);
        String commandExecutionResult = command.execute();
        return commandExecutionResult;
    }

    private void stopSplitWiseServer(){
        executorService.shutdown();
        try {
            serverSocket.close();
        } catch (IOException e) {
            LOGGER.error(CLOSING_SERVER_SOCKET_FAILED,e);
        }
    }

}
