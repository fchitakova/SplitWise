package splitwise.server.services;

import org.apache.log4j.Logger;
import splitwise.server.exceptions.PersistenceException;
import splitwise.server.exceptions.AuthenticationException;
import splitwise.server.model.User;
import splitwise.server.repository.UserRepository;
import splitwise.server.server.ActiveUsers;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Optional;

public class AuthenticationService extends SplitWiseService {
    public static final String RESETTING_NOTIFICATIONS_FAILED = "Resetting user notifications failed.";
    public static final String USER_REGISTRATION_FAILED = "User registration failed.";


    private static final Logger LOGGER = Logger.getLogger(AuthenticationService.class);

    public AuthenticationService(UserRepository userRepository, ActiveUsers activeUsers){
        this.userRepository = userRepository;
        this.activeUsers = activeUsers;
    }

    public boolean checkCredentialsValidity(String username, char[]password){
        Optional<User> user = userRepository.getById(username);
        if(!user.isPresent()){
            return false;
        }
        boolean validCredentials = user.get().checkCredentials(username,password);
        return validCredentials;
    }


    public void setUserAsActive(String username) {
        activeUsers.setUsernameForCurrentClientConnection(username);
    }

    public Deque<String> getUserNotifications(String username) {
        Optional<User> user = userRepository.getById(username);
        Deque<String> notifications = new ArrayDeque<>();
        if (user.isPresent()) {
            notifications.addAll(user.get().getNotifications());
        }
        resetNotifications(username);
        return notifications;
    }


    private void resetNotifications(String username) {
        Optional<User> user = userRepository.getById(username);
        user.ifPresent(User::resetNotifications);
        try {
            userRepository.save();
        } catch (PersistenceException e) {
            LOGGER.info(RESETTING_NOTIFICATIONS_FAILED);
            LOGGER.error(RESETTING_NOTIFICATIONS_FAILED + "Reason: " + e.getMessage(), e);
        }
    }

    public void registerUser(String username, char[] password) throws AuthenticationException {
        try {
            userRepository.addUser(new User(username, password));
        } catch (PersistenceException e) {
            LOGGER.info(USER_REGISTRATION_FAILED + SEE_LOG_FILE);
            LOGGER.error(USER_REGISTRATION_FAILED + e.getMessage(), e);
            throw new AuthenticationException(USER_REGISTRATION_FAILED, e);
        }
    }

    public void logoutUser() {
        this.activeUsers.logoutClient();
    }
}
