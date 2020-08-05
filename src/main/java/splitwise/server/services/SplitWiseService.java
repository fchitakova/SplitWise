package splitwise.server.services;

import splitwise.server.model.User;
import splitwise.server.repository.UserRepository;
import splitwise.server.server.ActiveUsers;

import java.util.Arrays;
import java.util.Optional;

public abstract class SplitWiseService {
    public static final String SEE_LOG_FILE = "See error.log for more information.";
    
    protected UserRepository userRepository;
    protected ActiveUsers activeUsers;
    
    public boolean checkIfRegistered(String... usernames) {
	return Arrays.stream(usernames).allMatch(username -> userRepository.getById(username).isPresent());
    }
    
    public void sendNotification(User user, String notification) {
	String username = user.getUsername();
	if(activeUsers.isActive(username)) {
	    activeUsers.sendMessageToUser(username, notification);
	} else {
	    user.pushNotification(notification);
	}
    }
    
    public String getCurrentSessionsUsername() {
	String username = activeUsers.getUsernameOfCurrentConnection();
	return username;
    }
    
    public boolean areFriends(String username1, String username2) {
	User user1 = userRepository.getById(username1).get();
	return user1.hasFriend(username2);
    }
    
    public boolean isGroupMember(String username, String groupName) {
	Optional<User> user = userRepository.getById(username);
	if(user.isPresent()) {
	    return user.get().isGroupMember(groupName);
	} else {
	    return false;
	}
    }
}
