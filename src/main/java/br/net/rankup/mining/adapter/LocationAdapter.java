package br.net.rankup.mining.adapter;

import java.lang.reflect.*;
import com.google.gson.*;
import org.bukkit.*;

public class LocationAdapter implements JsonSerializer<Location>, JsonDeserializer<Location>
{
    public Location deserialize(final JsonElement json, final Type typeOfT, final JsonDeserializationContext context) throws JsonParseException {
        final JsonObject jObj = json.getAsJsonObject();
        final JsonElement worldElement = jObj.get("world");
        final JsonElement xElement = jObj.get("x");
        final JsonElement yElement = jObj.get("y");
        final JsonElement zElement = jObj.get("z");
        final JsonElement yawElement = jObj.get("yaw");
        final JsonElement pitchElement = jObj.get("pitch");
        float yaw = 0.0f;
        float pitch = 0.0f;
        if (yawElement != null && yawElement.isJsonPrimitive()) {
            yaw = yawElement.getAsFloat();
        }
        if (pitchElement != null && pitchElement.isJsonPrimitive()) {
            pitch = pitchElement.getAsFloat();
        }
        return new Location(this.getWorld(worldElement.getAsString()), xElement.getAsDouble(), yElement.getAsDouble(), zElement.getAsDouble(), yaw, pitch);
    }
    
    public JsonElement serialize(final Location src, final Type typeOfSrc, final JsonSerializationContext context) {
        final JsonObject locJson = new JsonObject();
        locJson.addProperty("world", src.getWorld().getName());
        locJson.addProperty("x", src.getX() + "");
        locJson.addProperty("y", src.getY() + "");
        locJson.addProperty("z", src.getZ() + "");
        if (src.getYaw() != 0.0) {
            locJson.addProperty("yaw", src.getYaw() + "");
        }
        if (src.getPitch() != 0.0) {
            locJson.addProperty("pitch", src.getPitch() + "");
        }
        return (JsonElement)locJson;
    }
    
    private World getWorld(final String name) {
        return Bukkit.getWorld(name);
    }
}
