package splitwise.server.model;

import java.io.Serializable;
import java.util.*;

import static splitwise.server.services.StatusMessageBuilder.buildFriendsStatusMessage;
import static splitwise.server.services.StatusMessageBuilder.buildGroupFriendshipsStatusMessage;

public class User implements Serializable {
    public static final String NOT_ANY_OUTSTANDING_FINANCES = "You do not have any outstanding finances with friends.";
    public static final int SPLIT_PARTS_FOR_FRIENDSHIP = 2;
    
    private final String username;
    private final char[] password;
    private final List<Friend> friendships;
    private final List<GroupFriendship> groups;
    private final Deque<String> notifications;
    private String fullName;
    
    public User(String username, char[] password) {
        this.username = username;
        this.password = password;
        this.friendships = new ArrayList<>();
        this.groups = new ArrayList<>();
        this.notifications = new ArrayDeque<>();
    }
    
    public boolean checkCredentials(String username, char[] password) {
        return this.username.equals(username) && Arrays.equals(this.password, password);
    }
    
    public void addFriendship(String friendsUsername) {
        friendships.add(new Friend(friendsUsername));
    }
    
    public void addToGroup(String groupName, List<String> members) {
        groups.add(new GroupFriendship(groupName, members));
    }
    
    public GroupFriendship getGroup(String groupName) {
        for(GroupFriendship group : groups) {
            if(group.hasName(groupName)) {
                return group;
            }
        }
        return null;
    }
    
    public void splitWithFriend(String friendsUsername, Double amount) {
        for(Friendship friend : friendships) {
            if(friend.hasName(friendsUsername)) {
                Double splitAmount = amount / SPLIT_PARTS_FOR_FRIENDSHIP;
                friend.split(splitAmount);
            }
        }
    }
    
    public void splitInGroup(String groupName, Double amount) {
        for(GroupFriendship group : groups) {
            if(group.hasName(groupName)) {
                Double splitAmount = amount / group.size();
                group.split(splitAmount);
            }
        }
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
    
    public void resetNotifications() {
        notifications.clear();
    }
    
    public String getSplittingStatus() {
        String status = buildFriendsStatusMessage(friendships) + buildGroupFriendshipsStatusMessage(groups);
        if(status.isBlank()) {
            return NOT_ANY_OUTSTANDING_FINANCES;
        }
        
        return status;
    }
    
    public void payOffWith(String friendsUsername, Double amount) {
        for(Friend friend : friendships) {
            if(friend.hasName(friendsUsername)) {
                friend.payOff(username, amount);
            }
        }
    }
    
    public void payOffInGroup(String groupName, String username, Double amount) {
        for(GroupFriendship group : groups) {
            if(group.hasName(groupName)) {
                group.payOff(username, amount);
            }
        }
    }
    
    @Override
    public boolean equals(Object o) {
        if(this==o) {
            return true;
        }
        if(!(o instanceof User)) {
            return false;
        }
        User user = (User) o;
        return getUsername().equals(user.getUsername()) && Arrays.equals(password, user.password) && fullName.equals(user.fullName) && friendships.equals(user.friendships) && groups.equals(user.groups) && getNotifications().equals(user.getNotifications());
    }
    
    public String getUsername() {
        return username;
    }
    
    public Deque<String> getNotifications() {
        return notifications;
    }
    
    @Override
    public int hashCode() {
        int result = Objects.hash(getUsername(), fullName, friendships, groups, getNotifications());
        result = 31 * result + Arrays.hashCode(password);
        return result;
    }
}
