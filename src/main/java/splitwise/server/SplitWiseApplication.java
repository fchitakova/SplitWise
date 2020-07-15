package splitwise.server;

import org.apache.log4j.Logger;
import splitwise.server.exceptions.PersistenceException;
import splitwise.server.model.UserRepository;
import splitwise.server.model.filesystem.FileSystemUserRepository;
import splitwise.server.server.ActiveUsers;
import splitwise.server.server.SplitWiseServer;
import splitwise.server.services.*;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;

public class SplitWiseApplication
{
    public static final int SERVER_PORT = 8080;
    public static final String USER_REPOSITORY_CREATION_FAILED = "User repository creation failed. Reason: ";
    public static final String SEE_LOGS_FILE_FOR_MORE_INFO = "See logging.log file for more information.";
    public static final String FAILED_SERVER_SOCKET_CREATION = "I/O exception occurred during server socket creation!" ;
    public static final String MISSING_DEPENDENCIES_MESSAGE = "SplitWise application cannot be started because of missing dependencies.";
    private static final String DB_FILE_PATH = "src/main/resources/users.json";

    private static Logger LOGGER = Logger.getLogger(SplitWiseApplication.class);

    private static ServerSocket serverSocket;
    private static ActiveUsers activeUsers;
    private static UserRepository userRepository;
    private static CommandFactory commandFactory;


    public static void main(String[] args) {
        if (initializeSplitWiseDependencies()) {
            SplitWiseServer splitWiseServer = new SplitWiseServer(serverSocket, activeUsers, commandFactory);
            splitWiseServer.start();
        } else {
            LOGGER.info(MISSING_DEPENDENCIES_MESSAGE);
        }
    }

    private static boolean initializeSplitWiseDependencies() {
        activeUsers = new ActiveUsers();
        return instantiateServerSocket() && instantiateCommandFactory();
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

    private static boolean instantiateCommandFactory(){
        try {
            userRepository = new FileSystemUserRepository(DB_FILE_PATH);
            commandFactory = new CommandFactory(getSplitWiseServices(userRepository,activeUsers));
        } catch (PersistenceException e) {
            LOGGER.info(USER_REPOSITORY_CREATION_FAILED+e.getMessage());
            LOGGER.error(USER_REPOSITORY_CREATION_FAILED+e.getMessage(),e);
            return false;
        }
        return true;
    }

    private static List<SplitWiseService> getSplitWiseServices(UserRepository userRepository,ActiveUsers activeUsers){
        List<SplitWiseService> services = new ArrayList<>();

        services.add(new AuthenticationService(userRepository,activeUsers));
        services.add(new FriendshipCreator(userRepository,activeUsers));

        return services;
    }


}
