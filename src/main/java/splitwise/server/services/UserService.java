package splitwise.server.services;

import org.apache.log4j.Logger;
import splitwise.server.exceptions.PersistenceException;
import splitwise.server.exceptions.UserServiceException;
import splitwise.server.model.Friend;
import splitwise.server.model.GroupFriendship;
import splitwise.server.model.User;
import splitwise.server.model.UserRepository;
import splitwise.server.model.filesystem.FileSystemUserRepository;
import splitwise.server.server.ActiveClients;

import java.util.*;
import java.util.stream.Collectors;

public class UserService {
    public static final String SEE_LOG_FILE = "See logging.log for more information.";
    public static final String FAILED_SERVICE_CREATION = "UserService cannot be created because of persistence error : ";
    public static final String USER_REGISTRATION_FAILED = "User registration failed.";
    public static final String START_SPLITTING = "You can start splitting!";
    public static final String RECEIVED_FRIENDSHIP_NOTIFICATION = "%s added you as a friend. " + START_SPLITTING;
    public static final String ADDED_TO_GROUP_NOTIFICATION = "You have been added to group %s with participants: %s ."+START_SPLITTING;
    public static final String FRIENDSHIP_CREATION_FAILED = "Creating friendship failed due to persistence error.";
    public static final String GROUP_CREATION_FAILED = "Creating group failed due to persistence error.";
    public static final String ERROR_ESTABLISHING_FRIENDSHIP = "Error during establishing friendship.";
    public static final String ERROR_DURING_GROUP_CREATION = "Error during establishing friendship.";
    public static final String RESETTING_NOTIFICATIONS_FAILED = "Resetting user notifications failed.";


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
            LOGGER.info(RESETTING_NOTIFICATIONS_FAILED);
            LOGGER.error(RESETTING_NOTIFICATIONS_FAILED + "Reason: " + e.getMessage(), e);
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


    /**
     * This method assumes that both users represented by their usernames are already registered.
     * Returns true if friendship is successfully created.
     * @throws UserServiceException
     */
    public boolean createFriendship(String addingUsername, String addedUsername) throws UserServiceException {
        User addingUser = userRepository.getById(addingUsername).get();
        User addedUser = userRepository.getById(addedUsername).get();

        boolean isFriendshipEstablished = addingUser.addFriendship(new Friend(addedUsername)) &&
                addedUser.addFriendship(new Friend(addingUsername));

        if (isFriendshipEstablished) {
            try {
                String notificationMessage = String.format(RECEIVED_FRIENDSHIP_NOTIFICATION, addingUsername);
                sendNotification(addedUser, notificationMessage);
                userRepository.save();
            } catch (PersistenceException e) {
                LOGGER.info(FRIENDSHIP_CREATION_FAILED + SEE_LOG_FILE);
                LOGGER.error(FRIENDSHIP_CREATION_FAILED, e);
                throw new UserServiceException(ERROR_ESTABLISHING_FRIENDSHIP, e);
            }
            return true;
        }
        return false;
    }


    public boolean checkIfRegistered(String... usernames) {
        return Arrays.stream(usernames).allMatch(username -> userRepository.getById(username).isPresent());
    }

    /**
     * This method assumes that all participating users are already registered.
     * Returns true if the group friendship is successfully created.
     * @throws UserServiceException
     */
    public boolean createGroupFriendship(String groupName, List<String> participants) throws UserServiceException {
        boolean isGroupCreated = participants.stream().allMatch(participant -> addParticipantToGroup(groupName, participant, participants));
        if (isGroupCreated) {
            try {
                userRepository.save();
            } catch (PersistenceException e) {
                LOGGER.info(GROUP_CREATION_FAILED + SEE_LOG_FILE);
                LOGGER.error(GROUP_CREATION_FAILED, e);
                throw new UserServiceException(ERROR_DURING_GROUP_CREATION, e);
            }
        }
        return isGroupCreated;
    }

    private boolean addParticipantToGroup(String groupName, String currentParticipantUsername, List<String> participants) {
        List<String> participantsWithoutCurrent = participants.stream().
                filter(username -> (!username.equals(currentParticipantUsername))).collect(Collectors.toList());

        User participant = userRepository.getById(currentParticipantUsername).get();
        boolean addedToGroup = participant.addFriendship(new GroupFriendship(groupName, participantsWithoutCurrent));
        if(addedToGroup){
            String addedToGroupNotification = String.format(ADDED_TO_GROUP_NOTIFICATION,groupName,
                    participantsWithoutCurrent.stream().collect(Collectors.joining(", ")));

            sendNotification(participant,addedToGroupNotification);
        }

        return addedToGroup;
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
