package splitwise.server;

import org.apache.log4j.Logger;
import splitwise.server.exceptions.UserServiceException;
import splitwise.server.server.ActiveClients;
import splitwise.server.server.SplitWiseServer;
import splitwise.server.services.CommandFactory;
import splitwise.server.services.UserService;

import java.io.IOException;
import java.net.ServerSocket;

public class SplitWiseApplication
{
    public static final int SERVER_PORT = 8080;

    private static final String SEE_LOGS_FILE_FOR_MORE_INFO = "See logging.log file for more information.";
    private static final String FAILED_SERVER_SOCKET_CREATION =
            "I/O exception occurred during server socket creation!" ;
    private static final String MISSING_DEPENDENCIES_MESSAGE = "SplitWise application cannot be started because of missing dependencies.";

    private static Logger LOGGER = Logger.getLogger(SplitWiseApplication.class);

    private static ServerSocket serverSocket;
    private static ActiveClients activeClients;
    private static UserService userService;
    private static CommandFactory commandFactory;


    public static void main(String[] args) {
        if (initializeSplitWiseDependencies()) {
            SplitWiseServer splitWiseServer = new SplitWiseServer(serverSocket, activeClients, commandFactory);
            splitWiseServer.start();
        } else {
            LOGGER.info(MISSING_DEPENDENCIES_MESSAGE);
        }
    }

    private static boolean initializeSplitWiseDependencies() {
        activeClients = new ActiveClients();
        if (instantiateServerSocket() && instantiateUserService()) {
            commandFactory = new CommandFactory(userService);
            return true;
        }
        return false;
    }

    private static boolean instantiateServerSocket() {
        try {
            serverSocket = new ServerSocket(SERVER_PORT);
            return true;
        } catch (IOException e) {
            LOGGER.info(FAILED_SERVER_SOCKET_CREATION + SEE_LOGS_FILE_FOR_MORE_INFO);
            LOGGER.fatal(FAILED_SERVER_SOCKET_CREATION, e);
            return false;
        }

    }

    private static boolean instantiateUserService() {
        try {
            userService = new UserService(activeClients);
            return true;
        } catch (UserServiceException e) {
            LOGGER.info(e.getMessage() + SEE_LOGS_FILE_FOR_MORE_INFO);
            LOGGER.fatal(e.getMessage(), e);
            return false;
        }
    }


}
