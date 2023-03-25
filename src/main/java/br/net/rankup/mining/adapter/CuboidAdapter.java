package br.net.rankup.mining.adapter;

import java.lang.reflect.*;

import br.net.rankup.mining.utils.Cuboid;
import org.bukkit.*;
import com.google.gson.*;

public class CuboidAdapter implements JsonSerializer<Cuboid>, JsonDeserializer<Cuboid>
{
    public Cuboid deserialize(final JsonElement jsonElement, final Type type, final JsonDeserializationContext context) throws JsonParseException {
        final JsonObject jsonObject = jsonElement.getAsJsonObject();
        return new Cuboid((Location)context.deserialize((JsonElement)jsonObject.getAsJsonObject("upperSW"), (Type)Location.class), (Location)context.deserialize((JsonElement)jsonObject.getAsJsonObject("lowerNE"), (Type)Location.class));
    }

    public JsonElement serialize(final Cuboid cuboid, final Type type, final JsonSerializationContext context) {
        final JsonObject object = new JsonObject();
        object.add("upperSW", context.serialize((Object)cuboid.getUpperSW(), (Type)Location.class));
        object.add("lowerNE", context.serialize((Object)cuboid.getLowerNE(), (Type)Location.class));
        return (JsonElement)object;
    }
}
