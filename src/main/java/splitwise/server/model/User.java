package splitwise.server.model;

import java.io.Serializable;
import java.util.*;


public class User implements Serializable {
    private String username;
    private char[]password;
    private String fullName;
    private final Set<Friendship> friendships;
    private Deque<String> notifications;

    public User(String username,char[]password){
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

    public boolean isPartOfFriendship(String groupName){
        return friendships.stream().anyMatch(friendship -> friendship.getName().equals(groupName));
    }

    public Set<Friendship> getFriendships(){
        return friendships;
    }

    public void pushNotification(String notification) {
        this.notifications.push(notification);
    }

    public Deque<String> getNotifications() {
        return this.notifications;
    }

    public void resetNotifications() {
        this.notifications.clear();
    }

}
