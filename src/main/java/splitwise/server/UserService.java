package splitwise.server;


import splitwise.server.model.User;
import splitwise.server.model.UserRepository;
import splitwise.server.model.filesystem.FileSystemUserRepository;

import java.util.Queue;

public class UserService {

    private UserRepository userRepository;

    public UserService(){
        userRepository = new FileSystemUserRepository();
    }

    public boolean checkCredentialsValidity(String username, char[]password){
        User user = userRepository.getById(username);
        boolean validCredentials = user.checkCredentials(username,password);
        return validCredentials;
    }

    public Queue<String> getUserNotifications(String username){
        User user = userRepository.getById(username);
        Queue<String>notifications = user.getNotifications();
        return notifications;
    }



}
