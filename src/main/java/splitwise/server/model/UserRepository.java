package splitwise.server.model;


import java.util.List;
import java.util.Optional;

public interface UserRepository {
    Optional<User> getById(String username);

    void addUser(User user);

}