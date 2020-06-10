package splitwise.server.model.filesystem;


import splitwise.server.model.User;
import splitwise.server.model.UserRepository;

import java.util.HashMap;
import java.util.Map;

public class FileSystemUserRepository implements UserRepository {

    private Map<String, User> users;

    public  FileSystemUserRepository(){
        this.users = new HashMap();
    }

    public User getById(String username) {
        return users.get(username);
    }
}
