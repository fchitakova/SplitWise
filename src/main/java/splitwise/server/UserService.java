package splitwise.server;


import splitwise.server.model.User;
import splitwise.server.model.UserRepository;
import splitwise.server.model.filesystem.FileSystemUserRepository;

import java.io.IOException;
import java.util.*;

public class UserService {
    private static final String DB_FILE_PATH =  "src/main/resources/users.json";

    private UserRepository userRepository;

    public UserService(){
        try {
            userRepository = new FileSystemUserRepository(DB_FILE_PATH);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    public UserService(UserRepository userRepository){
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

     synchronized public void registerUser(String username,char[]password){
        this.userRepository.addUser(new User(username,password));
    }

}
