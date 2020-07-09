package splitwise.server.model.filesystem;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import splitwise.server.exceptions.PersistenceException;
import splitwise.server.model.Friendship;
import splitwise.server.model.User;
import splitwise.server.model.UserRepository;

import java.io.*;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class FileSystemUserRepository implements UserRepository {
    private static final String FAILED_DB_FILE_CREATION="IO error during DB file creation";
    private static final String CANNOT_FIND_DB_FILE = "DB file cannot be found or inaccessible.";
    private static final String CANNOT_LOAD_USER_DATA = "Users can not be loaded because of data corruption.";


    private static final String CANNOT_CREATE_FILE_WRITER="""
            IO exception occurred while constructing FileWriter.
            The cause may be that file is a directory,file does not exist or cannot be opened""";

    private static final String WRITING_TO_DB_FILE_FAILED = "IO error occurred while adding changes to DB file.";
    private static final String FAILED_DATA_SAVING = "Saving data failed!Possible data loss!";

    private static Type USERS_COLLECTION_TYPE = new TypeToken<Map<String,User>>(){}.getType();

    private ConcurrentMap<String,User> users;
    private File databaseFile;

    /**
     * Constructs FileSystemRepository using given database file path.
     *
     * @throws PersistenceException if database file cannot be created or accessed .
     *
     */
    public FileSystemUserRepository(String dbFilePath) throws PersistenceException {
        users = new ConcurrentHashMap<>();
        synchronized (this) {
            accessDBFile(dbFilePath);
            loadUserData();
        }
    }

     synchronized private void accessDBFile(String dbFilePath) throws PersistenceException{
        try {
            databaseFile = new File(dbFilePath);
            databaseFile.createNewFile();
        }catch(IOException e){
           throw new PersistenceException(FAILED_DB_FILE_CREATION,e);
        }
    }

    synchronized private void loadUserData() throws PersistenceException {
        try(FileReader reader = new FileReader(databaseFile)) {
            Gson gson = getCustomGson(new FriendshipJsonSerializer());
            Map<String, User> usersFromJson = gson.fromJson(reader, USERS_COLLECTION_TYPE);
            if (usersFromJson != null) {
                users.putAll(usersFromJson);
            }
        } catch (IOException e) {
            throw new PersistenceException(CANNOT_FIND_DB_FILE, e);
        }catch(JsonSyntaxException e){
            throw new PersistenceException(CANNOT_LOAD_USER_DATA,e);
        }

    }


    private Gson getCustomGson(Object typeAdapter){
        GsonBuilder jsonBuilder = new GsonBuilder();
        jsonBuilder.registerTypeAdapter(Friendship.class, typeAdapter);
        Gson gson = jsonBuilder.create();
        return gson;
    }

    synchronized public void addUser(User user) throws PersistenceException {
        String userId = user.getUsername();
        users.putIfAbsent(userId,user);
        saveUserData();
    }

    private void saveUserData()throws PersistenceException{
        try(FileWriter writer = new FileWriter(databaseFile,false)) {
            Gson gson = getCustomGson(new FriendshipJsonDeserializer());
            String data = gson.toJson(users, USERS_COLLECTION_TYPE);
            writeToDBFile(writer,data);
        }catch(IOException e){
            throw new PersistenceException(FAILED_DATA_SAVING+CANNOT_CREATE_FILE_WRITER,e);
        }
    }

    private void writeToDBFile(FileWriter writer,String data) throws PersistenceException {
        try{
            writer.write(data);
        }catch(IOException e){
            throw new PersistenceException(FAILED_DATA_SAVING+WRITING_TO_DB_FILE_FAILED,e);
        }
    }



    @Override
    public Optional<User> getById(String username) {
        User user = this.users.get(username);
        return Optional.ofNullable(user);
    }

}