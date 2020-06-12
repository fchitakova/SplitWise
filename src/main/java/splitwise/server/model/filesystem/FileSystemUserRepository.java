package splitwise.server.model.filesystem;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import splitwise.server.model.Friendship;
import splitwise.server.model.User;
import splitwise.server.model.UserRepository;

import java.io.*;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class FileSystemUserRepository implements UserRepository {
    private static Type USERS_COLLECTION_TYPE = new TypeToken<Map<String,User>>(){}.getType();

    private ConcurrentMap<String,User> users;
    private File databaseFile;

    public FileSystemUserRepository(String dbFilePath) throws IOException {
        initDatabaseFile(dbFilePath);
        this.users = new ConcurrentHashMap<>();
        synchronized (this) {
            loadUserData();
        }
    }

    private void initDatabaseFile(String dbFilePath) throws IOException {
        databaseFile = new File(dbFilePath);
        databaseFile.createNewFile();
    }

    synchronized private void loadUserData() throws IOException {
        try(FileReader reader = new FileReader(databaseFile)) {
            Gson gson = getCustomGson(new FriendshipSerializer());
            Map<String,User> usersFromJson = gson.fromJson(reader, USERS_COLLECTION_TYPE);
            if(usersFromJson!=null){
                this.users.putAll(usersFromJson);
            }
        }
    }

    private Gson getCustomGson(Object typeAdapter){
        GsonBuilder jsonBuilder = new GsonBuilder();
        jsonBuilder.registerTypeAdapter(Friendship.class, typeAdapter);
        Gson gson = jsonBuilder.create();
        return gson;
    }

    synchronized public void addUser(User user){
        String userId = user.getUsername();
        users.putIfAbsent(userId,user);
        saveUserData();

    }

    private void saveUserData() {
        try(FileWriter fileWriter = new FileWriter(databaseFile,false)){
            Gson gson = getCustomGson(new FriendshipDeserializer());
            String json = gson.toJson(users,USERS_COLLECTION_TYPE);
            fileWriter.write(json);
        } catch (IOException e) {
            ////WHAT TO DO HERE
        }
    }


    @Override
    public Optional<User> getById(String username) {
        User user = this.users.get(username);
        return Optional.ofNullable(user);
    }

}