package splitwise.server.services;

import org.apache.log4j.Logger;
import splitwise.server.exceptions.PersistenceException;
import splitwise.server.exceptions.UserServiceException;
import splitwise.server.model.User;
import splitwise.server.model.UserRepository;
import splitwise.server.model.filesystem.FileSystemUserRepository;
import splitwise.server.server.ActiveClients;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.Optional;

public class UserService {
    public static final String SEE_LOG_FILE = "See logging.log for more information.";
    public static final String FAILED_SERVICE_CREATION = "UserService cannot be created because of persistence error : ";
    public static final String USER_REGISTRATION_FAILED = "User registration failed.";
    public static final String START_SPLITTING = "You can start splitting!";
    public static final String RECEIVED_FRIENDSHIP_NOTIFICATION = "%s added you as a friend. " + START_SPLITTING;
    public static final String FRIENDSHIP_CREATION_FAILED = "Creating friendship failed due to persistence error.";

    private static final String DB_FILE_PATH = "src/main/resources/users.json";

    private static final Logger LOGGER = Logger.getLogger(UserService.class);

    private UserRepository userRepository;
    private ActiveClients activeClients;

    public UserService(ActiveClients activeClients) throws UserServiceException {
        try {
            userRepository = new FileSystemUserRepository(DB_FILE_PATH);
        }catch (PersistenceException e) {
            throw new UserServiceException(FAILED_SERVICE_CREATION + e.getMessage(),e);
        }
        this.activeClients = activeClients;
    }

    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    public boolean checkCredentialsValidity(String username, char[]password){
        Optional<User>user = userRepository.getById(username);
        if(!user.isPresent()){
            return false;
        }
        boolean validCredentials = user.get().checkCredentials(username,password);
        return validCredentials;
    }


    public Deque<String> getUserNotifications(String username) {
        Optional<User> user = userRepository.getById(username);
        Deque<String> notifications = new ArrayDeque<>();
        if (user.isPresent()) {
            notifications = user.get().getNotifications();
        }
        return notifications;
    }

    /**
     * Clears all notifications of user.
     */
    public void resetNotifications(String username) {
        Optional<User> user = userRepository.getById(username);
        user.ifPresent(User::resetNotifications);
        try {
            userRepository.save();
        } catch (PersistenceException e) {
            //TO DO
            e.printStackTrace();
        }
    }

    public void registerUser(String username, char[] password) throws UserServiceException {
        try {
            userRepository.addUser(new User(username, password));
        } catch (PersistenceException e) {
            LOGGER.info(USER_REGISTRATION_FAILED + SEE_LOG_FILE);
            LOGGER.error(USER_REGISTRATION_FAILED + e.getMessage(), e);
            throw new UserServiceException(USER_REGISTRATION_FAILED, e);
        }
    }


    public boolean createFriendship(String addingUsername, String addedUsername) {

        if (!checkIfRegistered(addingUsername, addedUsername)) {
            return false;
        }
        User addingUser = userRepository.getById(addingUsername).get();
        User addedUser = userRepository.getById(addedUsername).get();

        boolean friendshipCanBeEstablished = addingUser.addFriend(addedUsername) &&
                addedUser.addFriend(addingUsername);

        if (friendshipCanBeEstablished) {
            try {
                String notificationMessage = String.format(RECEIVED_FRIENDSHIP_NOTIFICATION, addingUsername);
                sendNotification(addedUser, notificationMessage);
                userRepository.save();
            } catch (PersistenceException e) {
                LOGGER.info(FRIENDSHIP_CREATION_FAILED + SEE_LOG_FILE);
                LOGGER.error(FRIENDSHIP_CREATION_FAILED, e);
                return false;
                //
                // throw new UserServiceException("Friendship cannot be established due to unexpected error.Try again later.");
            }
            return true;
        }
        return false;
    }

    public boolean checkIfRegistered(String... usernames) {
        return Arrays.stream(usernames).allMatch(username -> userRepository.getById(username).isPresent());
    }


    public void sendNotification(User user, String notification) {
        String username = user.getUsername();
        if (activeClients.isActive(username)) {
            activeClients.sendMessageToUser(username, notification);
        } else {
            user.pushNotification(notification);
        }
    }

    public String getCurrentSessionsUsername() {
        String username = activeClients.getUsernameOfCurrentClientConnection();
        return username;
    }

    public void setUserAsActive(String username) {
        activeClients.setUsernameForCurrentClientConnection(username);
    }

    public void logoutUser() {
        this.activeClients.logoutClient();
    }

}
