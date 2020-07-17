package splitwise.server.repository.filesystem;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import splitwise.server.model.Friendship;

import java.lang.reflect.Type;


public class FriendshipJsonSerializer implements JsonSerializer<Friendship> {
    private static final String friendshipType = "friendshipType";
    private static final String friendshipData = "friendshipData";

    @Override
    public JsonElement serialize(Friendship friendship, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(friendshipType, friendship.getClass().getName());
        jsonObject.add(friendshipData, jsonSerializationContext.serialize(friendship));

        return jsonObject;
    }
}
