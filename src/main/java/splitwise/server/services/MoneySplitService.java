package splitwise.server.services;

import org.apache.log4j.Logger;
import splitwise.server.exceptions.MoneySplitException;
import splitwise.server.exceptions.PersistenceException;
import splitwise.server.model.GroupFriendship;
import splitwise.server.model.User;
import splitwise.server.repository.UserRepository;
import splitwise.server.server.ActiveUsers;


public class MoneySplitService extends SplitWiseService {
    public static final String SPLITTING_FAILED = "Split command failed.";
    public static final String SEE_STATUS = "You can view the status of all splits with get-status command.";
    public static final String SPLIT_NOTIFICATION_FOR_FRIEND = "%s split %s LV with you. Splitting reason: %s. " + SEE_STATUS;
    public static final String SPLIT_NOTIFICATION_FOR_GROUP_MEMBERS = "%s split %s LV with you and other members of %s group. Splitting reason: %s. " + SEE_STATUS;
    public static final String PAYED_NOTIFICATION_FOR_FRIEND = "%s approved your payment %s LV [%s]." + SEE_STATUS;
    public static final String PAYED_NOTIFICATION_FOR_GROUP_MEMBERS = "%s approved %s  payment %s LV [%s] in group: %s." + SEE_STATUS;

    private static final Logger LOGGER = Logger.getLogger(MoneySplitService.class);

    public MoneySplitService(UserRepository userRepository, ActiveUsers activeUsers) {
        this.userRepository = userRepository;
        this.activeUsers = activeUsers;
    }


    public void split(String splitterUsername, String friendsName, Double amount, String splitReason) throws MoneySplitException {
        User splitter = userRepository.getById(splitterUsername).get();
        User friendToSplitWith = userRepository.getById(friendsName).get();

        splitter.splitWithFriend(friendsName, amount);
        friendToSplitWith.splitWithFriend(splitterUsername, (-amount));

        String notification = String.format(SPLIT_NOTIFICATION_FOR_FRIEND, splitterUsername, amount, splitReason);
        sendNotification(friendToSplitWith, notification);

        saveChanges();
    }


    public void splitInGroup(String splitterUsername, String groupName, Double amount, String splitReason) throws MoneySplitException {
        User splitter = userRepository.getById(splitterUsername).get();
        GroupFriendship groupFriendship = splitter.getGroup(groupName);

        for (String username : groupFriendship.getMembersUsernames()) {
            User groupMember = userRepository.getById(username).get();
            groupMember.splitInGroup(groupName, amount);

            String notification = String.format(SPLIT_NOTIFICATION_FOR_GROUP_MEMBERS, splitterUsername, amount, groupName, splitReason);
            sendNotification(groupMember, notification);

        }
        saveChanges();
    }

    public void payOff(String usernameToWhomIsPayed, Double amount, String debtorUsername, String splitReason) throws MoneySplitException {
        User payed = userRepository.getById(usernameToWhomIsPayed).get();
        payed.payOffWith(debtorUsername, amount);

        User debtor = userRepository.getById(debtorUsername).get();
        debtor.payOffWith(usernameToWhomIsPayed, (-amount));

        String notification = String.format(PAYED_NOTIFICATION_FOR_FRIEND, usernameToWhomIsPayed, amount, splitReason);
        sendNotification(debtor, notification);

        saveChanges();
    }

    public void groupPayOff(
            String usernameToWhomIsPayed,
            Double amount,
            String debtorUsername,
            String groupName,
            String splitReason)
            throws MoneySplitException {
        User payedUser = userRepository.getById(usernameToWhomIsPayed).get();

        GroupFriendship groupFriendship = payedUser.getGroup(groupName);
        for (String username : groupFriendship.getMembersUsernames()) {
            User groupMember = userRepository.getById(username).get();
            groupMember.payOffInGroup(groupName, debtorUsername, amount);

            if (!groupMember.getUsername().equals(usernameToWhomIsPayed)) {
                String notification =
                        String.format(
                                PAYED_NOTIFICATION_FOR_GROUP_MEMBERS,
                                usernameToWhomIsPayed,
                                debtorUsername,
                                amount,
                                splitReason,
                                groupName);
                sendNotification(groupMember, notification);
            }
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


    public String getStatus(String username) {
        User user = userRepository.getById(username).get();
        return user.getSplittingStatus();
    }

}
