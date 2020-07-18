package splitwise.server.services;

import org.apache.log4j.Logger;
import splitwise.server.exceptions.FriendshipException;
import splitwise.server.exceptions.PersistenceException;
import splitwise.server.model.User;
import splitwise.server.repository.UserRepository;
import splitwise.server.server.ActiveUsers;

import java.util.List;
import java.util.stream.Collectors;

public class FriendshipService extends SplitWiseService {
    public static final String START_SPLITTING = "You can start splitting!";
    public static final String RECEIVED_FRIENDSHIP_NOTIFICATION = "%s added you as a friend. " + START_SPLITTING;
    public static final String ADDED_TO_GROUP_NOTIFICATION = "You have been added to group %s with participants: %s ." + START_SPLITTING;
    public static final String FRIENDSHIP_CREATION_FAILED = "Creating friendship failed due to persistence error.";
    public static final String GROUP_CREATION_FAILED = "Creating group failed due to persistence error.";
    public static final String ERROR_ESTABLISHING_FRIENDSHIP = "Error during establishing friendship.";
    public static final String ERROR_DURING_GROUP_CREATION = "Error during establishing friendship.";


    private static final Logger LOGGER = Logger.getLogger(FriendshipService.class);

    public FriendshipService(UserRepository userRepository, ActiveUsers activeUsers) {
        this.userRepository = userRepository;
        this.activeUsers = activeUsers;
    }


    public boolean createFriendship(String addingUsername, String addedUsername) throws FriendshipException {
        User addingUser = userRepository.getById(addingUsername).get();
        User addedUser = userRepository.getById(addedUsername).get();

        boolean isFriendshipEstablished = addingUser.addFriendship(addedUsername) &&
                addedUser.addFriendship(addingUsername);

        if (isFriendshipEstablished) {
            String notificationMessage = String.format(RECEIVED_FRIENDSHIP_NOTIFICATION, addingUsername);
            sendNotification(addedUser, notificationMessage);
            saveChanges(false);
            return true;
        }
        return false;
    }

    private void saveChanges(boolean isGroupFriendship) throws FriendshipException {
        try {
            userRepository.save();
        } catch (PersistenceException e) {
            String logMessage = isGroupFriendship ? GROUP_CREATION_FAILED : FRIENDSHIP_CREATION_FAILED;
            String exceptionMessage = isGroupFriendship ? ERROR_DURING_GROUP_CREATION : ERROR_ESTABLISHING_FRIENDSHIP;
            LOGGER.info(logMessage + SEE_LOG_FILE);
            LOGGER.error(logMessage, e);
            throw new FriendshipException(exceptionMessage, e);
        }
    }


    public boolean createGroupFriendship(String groupName, List<String> membersUsernames) throws FriendshipException {
        List<User> members = membersUsernames.stream().map(username -> userRepository.getById(username).get()).collect(Collectors.toList());

        if (canGroupBeCreated(groupName, membersUsernames)) {
            members.stream().forEach(member -> addGroupMember(member, groupName, membersUsernames));
            saveChanges(true);
            return true;
        }
        return false;
    }

    private boolean canGroupBeCreated(String groupName, List<String> membersUsernames) {
        return !membersUsernames.stream().anyMatch(username -> isGroupMember(username, groupName));
    }

    private void addGroupMember(User memberToAdd, String groupName, List<String> members) {
        memberToAdd.addToGroup(groupName, members);
        String addedToGroupNotification = String.format(ADDED_TO_GROUP_NOTIFICATION, groupName,
                members.stream().collect(Collectors.joining(", ")));

        sendNotification(memberToAdd, addedToGroupNotification);
    }
}
