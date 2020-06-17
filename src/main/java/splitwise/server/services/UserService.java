package splitwise.server.services;


import org.apache.log4j.Logger;
import splitwise.server.exceptions.PersistenceException;
import splitwise.server.exceptions.UserServiceException;
import splitwise.server.model.User;
import splitwise.server.model.UserRepository;
import splitwise.server.model.filesystem.FileSystemUserRepository;

import java.util.*;

public class UserService {
    private static final String FAILED_SERVICE_CREATION="""
                           User Service cannot be created because of unavailable persistence.
                           See logging.log for more information.""";
    private static final String USER_REGISTRATION_FAILED = "User registration failed.";
    private static final String SEE_LOG_FILE= "See logging.log for more information.";

    private static final String DB_FILE_PATH =  "src/main/resources/users.json";

    private static final Logger LOGGER =Logger.getLogger(UserService.class);

    private UserRepository userRepository;

    public UserService() throws UserServiceException {
        try {
            userRepository = new FileSystemUserRepository(DB_FILE_PATH);
        }catch (PersistenceException e) {
            LOGGER.info(FAILED_SERVICE_CREATION+e.getMessage());
            LOGGER.error(FAILED_SERVICE_CREATION+e.getMessage(),e);
            throw new UserServiceException(FAILED_SERVICE_CREATION,e);
        }
    }

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    public boolean checkCredentialsValidity(String username, char[]password){
        Optional<User>user = userRepository.getById(username);
        if(!user.isPresent()){
            return false;
        }
        boolean validCredentials = user.get().checkCredentials(username,password);
        return validCredentials;
    }

    public boolean checkIfRegistered(String username){
        Optional<User>user = userRepository.getById(username);
        return user.isPresent();
    }

    public Deque<String> getUserNotifications(String username){
        Optional<User> user = userRepository.getById(username);
        Deque<String>notifications = new ArrayDeque<>();
        if(user.isPresent()){
            notifications = user.get().getNotifications();
        }
        return notifications;
    }

     synchronized public void registerUser(String username,char[]password) throws UserServiceException {
         try {
             userRepository.addUser(new User(username,password));
         } catch (PersistenceException e) {
             LOGGER.info(USER_REGISTRATION_FAILED+SEE_LOG_FILE);
             LOGGER.error(USER_REGISTRATION_FAILED+e.getMessage(),e);
             throw new UserServiceException(USER_REGISTRATION_FAILED,e);
         }
     }

     synchronized public void createFriendship(String initiatorUsername,String wantedFriendUsername) {
         Optional<User> initiatorUser = userRepository.getById(initiatorUsername);
         Optional<User> wantedFriend = userRepository.getById(wantedFriendUsername);
         initiatorUser.ifPresent(user -> user.addFriend(initiatorUsername));
         wantedFriend.ifPresent(user -> user.addFriend(wantedFriendUsername));
     }

     synchronized public void sendNotification(String username,String notification){
        userRepository.getById(username).ifPresent(user->user.pushNotification(notification));
     }

}
