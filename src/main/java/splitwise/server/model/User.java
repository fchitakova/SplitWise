package splitwise.server.model;

import java.io.Serializable;
import java.util.*;

import static splitwise.server.services.StatusMessageBuilder.buildFriendsStatusMessage;
import static splitwise.server.services.StatusMessageBuilder.buildGroupFriendshipsStatusMessage;


public class User implements Serializable {
    public static final String NOT_ANY_OUTSTANDING_FINANCES = "You do not have any outstanding finances with friends.";
    public static final int SPLIT_PARTS_FOR_FRIENDSHIP = 2;

    private String username;
    private char[] password;
    private String fullName;
    private final Set<Friend> friendships;
    private final Set<GroupFriendship> groups;
    private Deque<String> notifications;

    public User(String username, char[] password) {
        this.username = username;
        this.password = password;
        this.friendships = new HashSet<>();
        this.groups = new HashSet<>();
        this.notifications = new ArrayDeque<>();
    }

    public String getUsername() {
        return username;
    }

    public boolean checkCredentials(String username, char[] password) {
        return this.username.equals(username) && Arrays.equals(this.password, password);
    }

    public boolean addFriendship(String friendsUsername) {
        return friendships.add(new Friend(friendsUsername));
    }

    public boolean addToGroup(String groupName, List<String> members) {
        return groups.add(new GroupFriendship(groupName, members));
    }

    public GroupFriendship getGroup(String groupName) {
        for (GroupFriendship group : groups) {
            if (group.hasName(groupName)) {
                return group;
            }
        }
        return null;
    }

    public boolean splitWithFriend(String friendsUsername, Double amount) {
        for (Friendship friend : friendships) {
            if (friend.hasName(friendsUsername)) {
                Double splitAmount = amount / SPLIT_PARTS_FOR_FRIENDSHIP;
                friend.split(splitAmount);
                return true;
            }
        }
        return false;
    }


    public boolean splitInGroup(String groupName, Double amount) {
        for (GroupFriendship group : groups) {
            if (group.hasName(groupName)) {
                Double splitAmount = amount / group.size();
                group.split(splitAmount);
                return true;
            }
        }
        return false;
    }

    public boolean hasFriend(String username) {
        return friendships.stream().anyMatch(group -> group.hasName(username));
    }

    public boolean isGroupMember(String groupName) {
        return groups.stream().anyMatch(group -> group.hasName(groupName));
    }

    public void pushNotification(String notification) {
        notifications.add(notification);
    }

    public Deque<String> getNotifications() {
        return notifications;
    }

    public void resetNotifications() {
        notifications.clear();
    }

    public String getSplittingStatus() {
        String status = buildFriendsStatusMessage(friendships) + buildGroupFriendshipsStatusMessage(groups);
        if (status.isBlank()) {
            return NOT_ANY_OUTSTANDING_FINANCES;
        }

        return status;
    }

    public void payOffWith(String friendsUsername, Double amount) {
        for (Friend friend : friendships) {
            if (friend.hasName(friendsUsername)) {
                friend.payOff(username, amount);
            }
        }
    }

    public void payOffInGroup(String groupName, String username, Double amount) {
        for (GroupFriendship group : groups) {
            if (group.hasName(groupName)) {
                group.payOff(username, amount);
            }
        }
    }

}
