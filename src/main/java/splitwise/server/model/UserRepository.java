package splitwise.server.model;


import splitwise.server.exceptions.PersistenceException;

import java.util.Optional;

public interface UserRepository {
    Optional<User> getById(String username);

    void addUser(User user) throws PersistenceException;

    void save() throws PersistenceException;
}