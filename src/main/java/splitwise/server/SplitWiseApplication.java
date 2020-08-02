package splitwise.server;

import logger.Logger;
import splitwise.server.services.CommandFactory;
import splitwise.server.exceptions.PersistenceException;
import splitwise.server.repository.UserRepository;
import splitwise.server.repository.filesystem.FileSystemUserRepository;
import splitwise.server.server.ActiveUsers;
import splitwise.server.server.SplitWiseServer;
import splitwise.server.services.AuthenticationService;
import splitwise.server.services.FriendshipService;
import splitwise.server.services.MoneySplitService;

import java.io.IOException;
import java.net.ServerSocket;
import java.nio.file.Path;


public class SplitWiseApplication {
    public static final int SERVER_PORT = 8081;

    public static Logger LOGGER;

    private static ServerSocket serverSocket;
    private static ActiveUsers activeUsers;
    private static UserRepository userRepository;
    private static CommandFactory commandFactory;


    public static void main(String[] args) {
        Path logsDirectory = Path.of(args[1]);
        Path logFile = Path.of(args[2]);
        LOGGER = new Logger(logsDirectory, logFile);

        String dbFilePath = args[0];
        if (resolveSplitWiseDependencies(dbFilePath)) {
            SplitWiseServer splitWiseServer = new SplitWiseServer(serverSocket, activeUsers, commandFactory);
            splitWiseServer.start();
        } else {
            LOGGER.info("SplitWise application cannot be started because of missing dependencies.See error.log file for more information.");
        }
    }


    private static boolean resolveSplitWiseDependencies(String dbFilePath) {
        return createServerSocket() && createRepository(dbFilePath) && createCommandFactory();
    }

    private static boolean createServerSocket() {
        try {
            serverSocket = new ServerSocket(SERVER_PORT);
            return true;
        } catch (IOException e) {
            LOGGER.fatal("I/O exception occurred during server socket creation!", e);
            return false;
        }
    }

    private static boolean createRepository(String dbFilePath) {
        try {
            userRepository = new FileSystemUserRepository(dbFilePath);
        } catch (PersistenceException e) {
            LOGGER.info("User repository creation failed. Reason: " + e.getMessage());
            LOGGER.error("User repository creation failed. Reason: " + e.getMessage(), e);

            return false;
        }
        return true;
    }

    private static boolean createCommandFactory() {
        activeUsers = new ActiveUsers();
        commandFactory = new CommandFactory(new AuthenticationService(userRepository, activeUsers),
                new FriendshipService(userRepository, activeUsers), new MoneySplitService(userRepository, activeUsers));

        return true;
    }

}
