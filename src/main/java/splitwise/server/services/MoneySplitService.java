package splitwise.server.services;

import org.apache.log4j.Logger;
import splitwise.server.exceptions.MoneySplitException;
import splitwise.server.exceptions.PersistenceException;
import splitwise.server.model.Friendship;
import splitwise.server.model.GroupFriendship;
import splitwise.server.model.User;
import splitwise.server.repository.UserRepository;
import splitwise.server.server.ActiveUsers;

import java.util.List;


public class MoneySplitService extends SplitWiseService {
    public static final String SPLITTING_FAILED = "Split command failed.";
    public static final String SEE_STATUS = "You can view the status of all splits with get-status command.";
    public static final String NOTIFICATION_FOR_FRIEND = "%s split %s LV with you. Splitting reason: %s. " + SEE_STATUS;
    public static final String NOTIFICATION_FOR_GROUP_MEMBERS = "%s split %s LV with you and other members of %s group. Splitting reason: %s. " + SEE_STATUS;

    private static final Logger LOGGER = Logger.getLogger(MoneySplitService.class);

    public MoneySplitService(UserRepository userRepository, ActiveUsers activeUsers) {
        this.userRepository = userRepository;
        this.activeUsers = activeUsers;
    }

    public void split(String splitterUsername, String friendshipName, Double amount, String splitReason) throws MoneySplitException {
        User splitter = userRepository.getById(splitterUsername).get();
        split(splitter, friendshipName, (-amount));

        boolean isGroupFriendship = isGroupFriendship(splitter, friendshipName);
        if (!isGroupFriendship) {
            friendshipName = splitterUsername;
        }

        List<String> friendshipMembers = getFriendshipParticipants(splitter, friendshipName);
        for (String memberUsername : friendshipMembers) {

            User groupMember = userRepository.getById(memberUsername).get();
            split(groupMember, friendshipName, amount);

            String notification = isGroupFriendship ?
                    String.format(NOTIFICATION_FOR_GROUP_MEMBERS, splitterUsername, amount, friendshipName, splitReason) :
                    String.format(NOTIFICATION_FOR_FRIEND, splitterUsername, amount, splitReason);
            sendNotification(groupMember, notification);

        }
        saveChanges();
    }

    private void split(User user, String friendshipId, Double amount) {
        Friendship friendship = user.getSpecificFriendship(friendshipId);
        friendship.split(amount);
    }

    private List<String> getFriendshipParticipants(User splitter, String friendshipId) {
        Friendship friendship = splitter.getSpecificFriendship(friendshipId);
        return friendship.getMembersUsernames();
    }


    private boolean isGroupFriendship(User splitter, String friendshipId) {
        return splitter.getSpecificFriendship(friendshipId) instanceof GroupFriendship;
    }

//    private void sendNotificationToFriendshipMember(User groupMember, String splitterUsername, String friendshipId, Double amount, String splitReason) {
//        User splitter = userRepository.getById(splitterUsername).get();
//        boolean isGroupFriendship = isGroupFriendship(splitter, friendshipId);
//
//        String notification = isGroupFriendship ?
//                String.format(NOTIFICATION_FOR_GROUP_MEMBERS, splitterUsername, amount, friendshipId, splitReason) :
//                String.format(NOTIFICATION_FOR_FRIEND, splitterUsername, amount, splitReason);
//        sendNotification(groupMember, notification);
//    }


    private void saveChanges() throws MoneySplitException {
        try {
            userRepository.save();
        } catch (PersistenceException e) {
            LOGGER.info(SPLITTING_FAILED + SEE_LOG_FILE);
            LOGGER.error(SPLITTING_FAILED + e.getMessage(), e);
            throw new MoneySplitException(SPLITTING_FAILED, e);
        }
    }


    public boolean isSplittingAllowed(String splitterUsername, String friendshipId) {
        User user = userRepository.getById(splitterUsername).get();
        return user.isPartOfFriendship(friendshipId);
    }

}
