package splitwise.server.services;

import org.apache.log4j.Logger;
import splitwise.server.exceptions.PersistenceException;
import splitwise.server.exceptions.UserServiceException;
import splitwise.server.model.Friend;
import splitwise.server.model.GroupFriendship;
import splitwise.server.model.User;
import splitwise.server.model.UserRepository;
import splitwise.server.server.ActiveUsers;

import java.util.List;
import java.util.stream.Collectors;

public class FriendshipCreator extends SplitWiseService {
    public static final String START_SPLITTING = "You can start splitting!";
    public static final String RECEIVED_FRIENDSHIP_NOTIFICATION = "%s added you as a friend. " + START_SPLITTING;
    public static final String ADDED_TO_GROUP_NOTIFICATION = "You have been added to group %s with participants: %s ."+START_SPLITTING;
    public static final String FRIENDSHIP_CREATION_FAILED = "Creating friendship failed due to persistence error.";
    public static final String GROUP_CREATION_FAILED = "Creating group failed due to persistence error.";
    public static final String ERROR_ESTABLISHING_FRIENDSHIP = "Error during establishing friendship.";
    public static final String ERROR_DURING_GROUP_CREATION = "Error during establishing friendship.";


    private static final Logger LOGGER = Logger.getLogger(FriendshipCreator.class);

    public FriendshipCreator(UserRepository userRepository, ActiveUsers activeUsers){
        this.userRepository = userRepository;
        this.activeUsers = activeUsers;
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
}
