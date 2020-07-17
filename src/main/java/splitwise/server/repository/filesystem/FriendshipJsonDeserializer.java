package splitwise.server.repository.filesystem;


import com.google.gson.*;
import splitwise.server.model.Friendship;

import java.lang.reflect.Type;


public class FriendshipJsonDeserializer implements JsonDeserializer<Friendship> {
    private static final String friendshipType = "friendshipType";
    private static final String friendshipData = "friendshipData";

    @Override
    public Friendship deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        String classname = jsonObject.get(friendshipType).getAsString();
        Class objectClass = getObjectClass(classname);

        return jsonDeserializationContext.deserialize(jsonObject.get(friendshipData), objectClass);
    }

    private Class getObjectClass(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new JsonParseException(e.getMessage());
        }
    }
}