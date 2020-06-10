package splitwise.server.model;


public interface UserRepository {
    User getById(String username);
}
