package splitwise.server.services;

import org.apache.log4j.Logger;
import splitwise.server.exceptions.MoneySplitException;
import splitwise.server.exceptions.PersistenceException;
import splitwise.server.model.Friendship;
import splitwise.server.model.User;
import splitwise.server.repository.UserRepository;
import splitwise.server.server.ActiveUsers;

import java.util.List;


public class MoneySplitService extends SplitWiseService {
    public static final String SPLITTING_FAILED = "Split command failed.";
    public static final String SEE_STATUS = "You can view the status of all splits with get-status command.";
    public static final String SPLIT_NOTIFICATION_FOR_FRIEND = "%s split %s LV with you. Splitting reason: %s. " + SEE_STATUS;
    public static final String SPLIT_NOTIFICATION_FOR_GROUP_MEMBERS = "%s split %s LV with you and other members of %s group. Splitting reason: %s. " + SEE_STATUS;
    public static final String PAYED_NOTIFICATION_FOR_FRIEND = "%s approved your payment %s LV [%s]." + SEE_STATUS;
    public static final String PAYED_NOTIFICATION_FOR_GROUP_MEMBERS = "%s approved your payment %s LV [%s] in group: %s." + SEE_STATUS;

    private static final Logger LOGGER = Logger.getLogger(MoneySplitService.class);

    public MoneySplitService(UserRepository userRepository, ActiveUsers activeUsers) {
        this.userRepository = userRepository;
        this.activeUsers = activeUsers;
    }

    public void split(String splitterUsername, String friendshipName, Double amount, String splitReason) throws MoneySplitException {
        User splitter = userRepository.getById(splitterUsername).get();

        List<String> friendshipMembers = getFriendshipParticipants(splitter, friendshipName);

        Double splitAmount = calculateSplitAmount(amount, friendshipMembers);
        split(splitter, friendshipName, splitAmount);

        boolean isGroupFriendship = isGroupFriendship(friendshipMembers);
        if (!isGroupFriendship) {
            friendshipName = splitterUsername;
        }

        for (String memberUsername : friendshipMembers) {
            User groupMember = userRepository.getById(memberUsername).get();
            split(groupMember, friendshipName, (-splitAmount));

            String notification = isGroupFriendship ?
                    String.format(SPLIT_NOTIFICATION_FOR_GROUP_MEMBERS, splitterUsername, amount, friendshipName, splitReason) :
                    String.format(SPLIT_NOTIFICATION_FOR_FRIEND, splitterUsername, amount, splitReason);
            sendNotification(groupMember, notification);

        }
        saveChanges();
    }

    private Double calculateSplitAmount(Double amount, List<String> friendshipMembers) {
        boolean isGroupFriendship = isGroupFriendship(friendshipMembers);
        if (isGroupFriendship) {
            return amount / friendshipMembers.size();
        }
        return amount / 2;
    }

    private void split(User user, String friendshipName, Double amount) {
        Friendship friendship = user.getSpecificFriendship(friendshipName).get();
        friendship.split(amount);
    }

    private List<String> getFriendshipParticipants(User splitter, String friendshipName) {
        Friendship friendship = splitter.getSpecificFriendship(friendshipName).get();
        return friendship.getMembersUsernames();
    }


    private boolean isGroupFriendship(List<String> members) {
        return members.size() > 1;
    }

    public void payOff(String usernameToWhomIsPaid, Double amount, String debtorUsername, String splitReason) throws MoneySplitException {
        User paidUser = userRepository.getById(usernameToWhomIsPaid).get();
        Friendship paidUserSideFriendship = paidUser.getSpecificFriendship(debtorUsername).get();
        paidUserSideFriendship.payOff(debtorUsername, amount);

        User debtor = userRepository.getById(debtorUsername).get();
        Friendship debtorSideFriendship = debtor.getSpecificFriendship(usernameToWhomIsPaid).get();
        debtorSideFriendship.payOff(usernameToWhomIsPaid, (-amount));

        String notification = String.format(PAYED_NOTIFICATION_FOR_FRIEND, usernameToWhomIsPaid, Double.toString(amount), splitReason);
        sendNotification(debtor, notification);

        saveChanges();
    }

    public void groupPayOff(String usernameToWhomIsPaid, Double amount, String debtorUsername, String groupName, String splitReason) throws MoneySplitException {
        User paidUser = userRepository.getById(usernameToWhomIsPaid).get();
        Friendship paidUserSideFriendship = paidUser.getSpecificFriendship(groupName).get();
        paidUserSideFriendship.payOff(debtorUsername, amount);

        List<String> groupMembersUsernames = paidUserSideFriendship.getMembersUsernames();
        for (String username : groupMembersUsernames) {
            User groupMember = userRepository.getById(username).get();
            Friendship friendship = groupMember.getSpecificFriendship(groupName).get();
            friendship.payOff(debtorUsername, (-amount));

            String notification = String.format(PAYED_NOTIFICATION_FOR_GROUP_MEMBERS, usernameToWhomIsPaid, Double.toString(amount), groupMember, splitReason);
            sendNotification(groupMember, notification);
        }
        saveChanges();
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


    public boolean isMoneySharingAllowedBetween(String splitterUsername, String friendshipName) {
        User user = userRepository.getById(splitterUsername).get();
        return user.isPartOfFriendship(friendshipName);
    }

    public String getSplittingStatusOfUser(String username) {
        User user = userRepository.getById(username).get();
        return user.getSplittingStatus();
    }

}
