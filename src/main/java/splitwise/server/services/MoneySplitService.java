package splitwise.server.services;

import org.apache.log4j.Logger;
import splitwise.server.exceptions.MoneySplitException;
import splitwise.server.exceptions.PersistenceException;
import splitwise.server.model.Friendship;
import splitwise.server.model.User;
import splitwise.server.repository.UserRepository;
import splitwise.server.server.ActiveUsers;

import java.util.List;
import java.util.Set;


public class MoneySplitService extends SplitWiseService {
    public static final String SPLITTING_FAILED = "Split command failed.";
    public static final String SEE_STATUS = "You can view all splits status with get-status command.";
    public static final String NOTIFICATION_FOR_SPLITTER = "Split %s LV between you and %s for %s. " + SEE_STATUS;
    public static final String NOTIFICATION_FOR_GROUP_SPLITTER = "Split %s LV between you and % group members %s. " + SEE_STATUS;
    public static final String NOTIFICATION_FOR_FRIEND = "% split %s LV with you. Splitting reason %s. " + SEE_STATUS;
    public static final String NOTIFICATION_FOR_GROUP_MEMBERS = "%s split %s LV with you and other members of %s group. Splitting reason %s. " + SEE_STATUS;


    private static final Logger LOGGER = Logger.getLogger(MoneySplitService.class);

    public MoneySplitService(UserRepository userRepository, ActiveUsers activeUsers) {
        this.userRepository = userRepository;
        this.activeUsers = activeUsers;
    }

    public void split(String splitterUsername, String friendshipId, Double amount, String splitReason) throws MoneySplitException {
        User splitter = userRepository.getById(splitterUsername).get();
        Friendship splittersGroup = getFriendshipOf(splitter, friendshipId);
        splittersGroup.split((-amount));
        List<String> groupMembers = splittersGroup.getFriendshipMembersUsernames();
        boolean isGroupSplitting = groupMembers.size() > 1;
        if (isGroupSplitting) {
            sendNotification(splitter, String.format(NOTIFICATION_FOR_GROUP_SPLITTER, amount, friendshipId, splitReason));
        } else {
            sendNotification(splitter, String.format(NOTIFICATION_FOR_SPLITTER, amount, friendshipId, splitReason));
        }


        for (String memberUsername : groupMembers) {
            User groupMember = userRepository.getById(memberUsername).get();
            Friendship group = getFriendshipOf(groupMember, friendshipId);
            group.split(amount);
            if (isGroupSplitting) {
                sendNotification(groupMember, String.format(NOTIFICATION_FOR_GROUP_MEMBERS, splitterUsername, amount, friendshipId, splitReason));
            } else {
                sendNotification(groupMember, String.format(NOTIFICATION_FOR_FRIEND, splitterUsername, amount, splitReason));
            }

        }
        saveChanges();
    }


    private Friendship getFriendshipOf(User user, String friendshipId) {
        Set<Friendship> usersFriendships = user.getFriendships();
        for (Friendship friendship : usersFriendships) {
            if (friendship.getName().equals(friendshipId)) {
                return friendship;
            }
        }
        return null;
    }

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

    /*
    public String getStatusForUser(String username) {
        User user = userRepository.getById(username).get();
        Set<Friendship> friendships = user.getFriendships();

        StringBuilder status = new StringBuilder();
        for (Friendship friendship : friendships) {
            // if (friendship.)
            status.append(friendship.getStatus());
            status.append('\n');
        }
        return status.toString();
    }

    public String getStatusForUser(String username, Friendship friendship) {
        return null;
    }*/


}
