package br.net.rankup.mining.misc;

import org.bukkit.*;

public class LocationSerializer
{
    public static String serialize(final Location location) {
        return location.getWorld().getName() + ";" + location.getX() + ";" + location.getY() + ";" + location.getZ() + ";" + location.getYaw() + ";" + location.getPitch();
    }
    
    public static Location deserialize(final String serializedLocation) {
        final String[] split = serializedLocation.split(";");
        return new Location(Bukkit.getWorld(split[0]), Double.parseDouble(split[1]), Double.parseDouble(split[2]), Double.parseDouble(split[3]), Float.parseFloat(split[4]), Float.parseFloat(split[5]));
    }
}
