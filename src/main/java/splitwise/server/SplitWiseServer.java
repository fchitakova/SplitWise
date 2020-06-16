package splitwise.server;

import org.apache.log4j.Logger;
import splitwise.server.commands.Command;
import splitwise.server.exceptions.ServerConnectionException;
import splitwise.server.exceptions.UserServiceException;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class SplitWiseServer {
    public static final String ERROR_CLOSING_SOCKET = "Cannot close socket connection because of I/O exception.";

    private static final int SERVER_PORT = 8080;
    private static final int MAXIMUM_CONNECTIONS_COUNT = 100;
    private static final String FAILED_SERVER_SOCKET_CREATION =
            "Split Wise Server instantiation failed because of I/O exception during server socket creation!" ;
    private static final String SERVER_STARTED = "SplitWise server started!";
    private static final String SOCKET_ACCEPT_ERROR="I/O error occurred while waiting for client connection.";
    private static final String USER_SERVICE_CREATION_FAILED = """
            CommandFactory cannot be instantiated because of UserServiceException
            during User Service instantiation.
            See logging.log for more information.""";

    private static Logger LOGGER = Logger.getLogger(SplitWiseServer.class);

    private ServerSocket serverSocket;
    private ExecutorService executorService;
    private CommandFactory commandFactory;

    public static void main(String[]args){
        try{
            SplitWiseServer splitWiseServer = new SplitWiseServer(SERVER_PORT);
            splitWiseServer.start();
        }catch(ServerConnectionException e){
            LOGGER.info(e.getMessage());
            LOGGER.fatal(e.getMessage(),e);
        } catch (UserServiceException e) {
            LOGGER.info(e.getMessage());
            LOGGER.fatal(e.getMessage(),e);
        }

    }

    public SplitWiseServer(int port) throws ServerConnectionException, UserServiceException {
        createServerSocket(port);
        createCommandFactory();
        executorService = Executors.newFixedThreadPool(MAXIMUM_CONNECTIONS_COUNT);
    }

    private void createServerSocket(int port) throws ServerConnectionException {
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            throw new ServerConnectionException(FAILED_SERVER_SOCKET_CREATION,e);
        }
    }

    private void createCommandFactory() throws UserServiceException {
        UserService userService;
        try {
            userService = new UserService();
        } catch (UserServiceException e) {
            throw new UserServiceException(USER_SERVICE_CREATION_FAILED,e);
        }
        commandFactory = new CommandFactory(userService);
    }


    public void start(){
        System.out.println(SERVER_STARTED);
            try {
                while(true) {
                    Socket clientSocket = getSocketConnection();
                    ClientConnection clientConnection = new ClientConnection(clientSocket, this);
                    executorService.execute(clientConnection);
                }
            } catch(IOException e){
                LOGGER.error(e.getMessage(),e);
            }finally {
                executorService.shutdown();
            }
        }

    private Socket getSocketConnection() throws IOException {
        Socket socket;
        try {
            socket = this.serverSocket.accept();
        }catch (IOException ioException) {
            throw new IOException(SOCKET_ACCEPT_ERROR,ioException);
        }
        return socket;
    }

    public String processUserInput(String input){
        Command command = commandFactory.createCommand(input);
        String commandExecutionResult = command.execute();
        return commandExecutionResult;
    }

}
