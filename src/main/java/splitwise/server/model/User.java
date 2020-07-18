package splitwise.server.model;

import java.io.Serializable;
import java.util.*;


public class User implements Serializable {
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String RED_STAR_SYMBOL = ANSI_RED + '*' + ANSI_RESET;
    public static final String NOT_ANY_OUTSTANDING_FINANCES = "You do not have any outstanding finances with friends.";

    private String username;
    private char[] password;
    private String fullName;
    private final Set<Friendship> friendships;
    private Deque<String> notifications;

    public User(String username, char[] password) {
        this.username = username;
        this.password = password;
        this.friendships = new HashSet<>();
        this.notifications = new ArrayDeque<>();
    }

    public String getUsername() {
        return this.username;
    }

    public boolean checkCredentials(String username, char[] password) {
        return this.username.equals(username) && Arrays.equals(this.password, password);
    }

    public boolean addFriendship(Friendship friendship) {
        return friendships.add(friendship);
    }

    public boolean isPartOfFriendship(String groupName) {
        return friendships.stream().anyMatch(friendship -> friendship.getName().equals(groupName));
    }


    public Optional<Friendship> getSpecificFriendship(String friendshipName) {
        for (Friendship friendship : friendships) {
            if (friendship.getName().equals(friendshipName)) {
                return Optional.of(friendship);
            }
        }
        return Optional.ofNullable(null);
    }


    public void pushNotification(String notification) {
        this.notifications.add(notification);
    }

    public Deque<String> getNotifications() {
        return this.notifications;
    }

    public void resetNotifications() {
        this.notifications.clear();
    }

    public String getSplittingStatus() {
        StringBuilder friendsStatus = new StringBuilder();
        StringBuilder groupsStatus = new StringBuilder();

        for (Friendship friendship : friendships) {

            StringBuilder friendshipStatus = new StringBuilder(friendship.getStatus());

            if (anyOutstandingAccountsArePresent(friendshipStatus)) {
                if (friendship instanceof Friend) {
                    friendsStatus.append(RED_STAR_SYMBOL + friendship.getStatus() + '\n');
                } else {
                    groupsStatus.append(RED_STAR_SYMBOL + friendship.getName() + ":\n" + friendshipStatus);
                }
            }
        }

        StringBuilder status = new StringBuilder();
        if (anyOutstandingAccountsArePresent(friendsStatus)) {
            status.append("Friends:\n" + friendsStatus);
        }
        if (anyOutstandingAccountsArePresent(groupsStatus)) {
            status.append("\nGroups:\n" + groupsStatus);
        }
        if (!anyOutstandingAccountsArePresent(status)) {
            status.append(NOT_ANY_OUTSTANDING_FINANCES);
        }

        return status.toString();
    }

    private boolean anyOutstandingAccountsArePresent(StringBuilder friendshipStatus) {
        return !friendshipStatus.toString().isBlank();
    }

}
