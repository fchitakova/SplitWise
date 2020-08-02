package splitwise.server.services;


import splitwise.server.exceptions.FriendshipException;
import splitwise.server.exceptions.PersistenceException;
import splitwise.server.model.User;
import splitwise.server.repository.UserRepository;
import splitwise.server.server.ActiveUsers;

import java.util.List;
import java.util.stream.Collectors;

import static splitwise.server.SplitWiseApplication.LOGGER;

public class FriendshipService extends SplitWiseService {
    public static final String START_SPLITTING = "You can start splitting!";
    public static final String RECEIVED_FRIENDSHIP_NOTIFICATION = "%s added you as a friend. " + START_SPLITTING;
    public static final String ADDED_TO_GROUP_NOTIFICATION = "You have been added to group %s with participants: %s ." + START_SPLITTING;

    public FriendshipService(UserRepository userRepository, ActiveUsers activeUsers) {
        this.userRepository = userRepository;
        this.activeUsers = activeUsers;
    }


    public void createFriendship(String addingUsername, String addedUsername) throws FriendshipException {
        User addingUser = userRepository.getById(addingUsername).get();

        addingUser.addFriendship(addedUsername);

        User addedUser = userRepository.getById(addedUsername).get();
        addedUser.addFriendship(addingUsername);

        String notificationMessage = String.format(RECEIVED_FRIENDSHIP_NOTIFICATION, addingUsername);
        sendNotification(addedUser, notificationMessage);
        saveChanges();

    }

    private void saveChanges() throws FriendshipException {
        try {
            userRepository.save();
        } catch (PersistenceException e) {
            LOGGER.info("Establishing friendship failed. Reason:" + e.getMessage() + SEE_LOG_FILE);
            LOGGER.error("Establishing friendship failed.", e);

            throw new FriendshipException("Error during establishing friendship.", e);
        }
    }


    public void createGroupFriendship(String groupCreator, String groupName, List<String> membersUsernames) throws FriendshipException {
        List<User> members = membersUsernames.stream().map(username -> userRepository.getById(username).get()).collect(Collectors.toList());

        members.stream().forEach(member -> addGroupMember(groupCreator, member, groupName, membersUsernames));
        saveChanges();
    }

    private void addGroupMember(String groupCreator, User memberToAdd, String groupName, List<String> members) {
        memberToAdd.addToGroup(groupName, members);
        String addedToGroupNotification = String.format(ADDED_TO_GROUP_NOTIFICATION, groupName,
                members.stream().collect(Collectors.joining(", ")));

        if (!groupCreator.equals(memberToAdd.getUsername())) {
            sendNotification(memberToAdd, addedToGroupNotification);
        }
    }

    public boolean canGroupBeCreated(String groupName, List<String> membersUsernames) {
        return !membersUsernames.stream().anyMatch(username -> isGroupMember(username, groupName));
    }

}
