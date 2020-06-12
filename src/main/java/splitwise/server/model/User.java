package splitwise.server.model;

import java.util.*;


public class User{
    private String username;
    private char[]password;
    private String fullName;
    private List<Friendship> friendships;
    private Deque<String> notifications;

    public User(String username,char[]password){
        this.username = username;
        this.password = password;
        this.friendships = new ArrayList<>();
        this.notifications = new ArrayDeque<>();
    }

    public String getUsername(){
        return this.username;
    }

    public boolean checkCredentials(String username,char[]password)
    {
        if(this.username.equals(username) && Arrays.equals(this.password,password)){
            return true;
        }
        return false;
    }

    public void pushNotification(String notification){
        this.notifications.push(notification);
    }

    public Deque<String> getNotifications(){
        return this.notifications;
    }

}
