package splitwise.server.services;

import splitwise.server.model.User;
import splitwise.server.repository.UserRepository;
import splitwise.server.server.ActiveUsers;

import java.util.Arrays;

public abstract class SplitWiseService{
    public static final String SEE_LOG_FILE = "See logging.log for more information.";
    
    protected UserRepository userRepository;
    protected ActiveUsers activeUsers;


    public boolean checkIfRegistered(String... usernames) {
        return Arrays.stream(usernames).allMatch(username -> userRepository.getById(username).isPresent());
    }

    public void sendNotification(User user, String notification) {
        String username = user.getUsername();
        if (activeUsers.isActive(username)) {
            activeUsers.sendMessageToUser(username, notification);
        } else {
            user.pushNotification(notification);
        }
    }

    public String getCurrentSessionsUsername() {
        String username = activeUsers.getUsernameOfCurrentClientConnection();
        return username;
    }

}
