package splitwise.server.repository.filesystem;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import splitwise.server.exceptions.PersistenceException;
import splitwise.server.model.User;
import splitwise.server.repository.UserRepository;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class FileSystemUserRepository implements UserRepository {
  private static Type USERS_COLLECTION_TYPE = new TypeToken<Map<String, User>>() {}.getType();

  private Map<String, User> users;
  private File databaseFile;

  public FileSystemUserRepository(String dbFilePath) throws PersistenceException {
    accessDBFile(dbFilePath);
    loadUserData();
  }

  private synchronized void accessDBFile(String dbFilePath) throws PersistenceException {
    try {
      databaseFile = new File(dbFilePath);
      databaseFile.createNewFile();
    } catch (IOException e) {
      throw new PersistenceException("IO error during DB file creation", e);
    }
  }

  private synchronized void loadUserData() throws PersistenceException {
    try (FileReader reader = new FileReader(databaseFile)) {
      {
        Gson gson = new Gson();
        Map<String, User> usersFromJson = gson.fromJson(reader, USERS_COLLECTION_TYPE);
        this.users = new HashMap<>();
        if (usersFromJson != null) {
          users.putAll(usersFromJson);
        }
      }
    } catch (IOException e) {
      throw new PersistenceException("DB file cannot be found or inaccessible.", e);
    } catch (JsonSyntaxException e) {
      throw new PersistenceException("Loading users data failed.", e);
    }
  }

  @Override
  public synchronized void addUser(User user) throws PersistenceException {
    String userId = user.getUsername();
    users.putIfAbsent(userId, user);
    save();
  }

  public synchronized void save() throws PersistenceException {
    try (FileWriter writer = new FileWriter(databaseFile, false)) {
      GsonBuilder gsonBuilder = new GsonBuilder();
      String data = gsonBuilder.create().toJson(users, USERS_COLLECTION_TYPE);
      writer.write(data);
    } catch (IOException e) {
      throw new PersistenceException("IO error occurred while adding changes to DB file.", e);
    }
  }

  @Override
  public Optional<User> getById(String username) {
    User user = this.users.get(username);
    return Optional.ofNullable(user);
  }
}
