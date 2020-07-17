package splitwise.server.model;

import java.io.Serializable;
import java.util.*;


public class User implements Serializable {
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String RED_STAR_SYMBOL = ANSI_RED + '*' + ANSI_RESET;

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
        StringBuilder splittingStatus = new StringBuilder();
        splittingStatus.append("Friends:\n");
        for (Friendship friendship : friendships) {
            if (friendship instanceof Friend && !friendship.getStatus().isBlank()) {
                splittingStatus.append(RED_STAR_SYMBOL + friendship.getStatus() + '\n');
            }
        }

        splittingStatus.append("\nGroups:\n");
        for (Friendship friendship : friendships) {
            if (friendship instanceof GroupFriendship) {
                splittingStatus.append(RED_STAR_SYMBOL + friendship.getName() + ":\n");
                splittingStatus.append(friendship.getStatus());
            }
        }
        return splittingStatus.toString();
    }

}
