package splitwise.server;

import org.apache.log4j.Logger;
import splitwise.server.services.CommandFactory;
import splitwise.server.exceptions.PersistenceException;
import splitwise.server.repository.UserRepository;
import splitwise.server.repository.filesystem.FileSystemUserRepository;
import splitwise.server.server.ActiveUsers;
import splitwise.server.server.SplitWiseServer;
import splitwise.server.services.AuthenticationService;
import splitwise.server.services.FriendshipService;
import splitwise.server.services.MoneySplitService;
import splitwise.server.services.SplitWiseService;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;

public class SplitWiseApplication
{
    public static final int SERVER_PORT = 8081;
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
            LOGGER.info("SplitWise application cannot be started because of missing dependencies.See logging.log file for more information.");
        }
    }

    private static boolean initializeSplitWiseDependencies() {
        return instantiateServerSocket() && instantiateCommandFactory();
    }

    private static boolean instantiateServerSocket() {
        try {
            serverSocket = new ServerSocket(SERVER_PORT);
            return true;
        } catch (IOException e) {
            LOGGER.fatal("I/O exception occurred during server socket creation!", e);
            return false;
        }
    }

    private static boolean instantiateCommandFactory(){
        try {
            activeUsers = new ActiveUsers();
            userRepository = new FileSystemUserRepository(DB_FILE_PATH);
            commandFactory = new CommandFactory(new AuthenticationService(userRepository, activeUsers),
                    new FriendshipService(userRepository, activeUsers),new MoneySplitService(userRepository,activeUsers));
        } catch (PersistenceException e) {
            LOGGER.info("User repository creation failed. Reason: " + e.getMessage());
            LOGGER.error("User repository creation failed. Reason: " + e.getMessage(), e);
            return false;
        }
        return true;
    }

}
