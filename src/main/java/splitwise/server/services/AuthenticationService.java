package splitwise.server.services;

import logger.Logger;
import splitwise.server.exceptions.PersistenceException;
import splitwise.server.exceptions.AuthenticationException;
import splitwise.server.model.User;
import splitwise.server.repository.UserRepository;
import splitwise.server.server.ActiveUsers;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Optional;

import static splitwise.server.SplitWiseApplication.LOGGER;

public class AuthenticationService extends SplitWiseService {

    public AuthenticationService(UserRepository userRepository, ActiveUsers activeUsers){
        this.userRepository = userRepository;
        this.activeUsers = activeUsers;
    }

    public boolean checkCredentialsValidity(String username, char[]password){
        Optional<User> user = userRepository.getById(username);
        if (user.isPresent()) {
            return user.get().checkCredentials(username, password);
        }
        return false;
    }


    public void setUserAsActive(String username) {
        activeUsers.setUsernameOfCurrentConnection(username);
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
            LOGGER.info("Resetting user notifications failed. Reason: " + e.getMessage() + SEE_LOG_FILE);
            LOGGER.error("Resetting user notifications failed.", e);
        }
    }

    public void registerUser(String username, char[] password) throws AuthenticationException {
        try {
            userRepository.addUser(new User(username, password));
        } catch (PersistenceException e) {
            LOGGER.info("User registration failed.Reason:" + e.getMessage() + SEE_LOG_FILE);
            LOGGER.error("User registration failed.", e);

            throw new AuthenticationException("User registration failed.", e);
        }
    }

    public void logoutUser() {
        this.activeUsers.logoutUser();
    }
}
