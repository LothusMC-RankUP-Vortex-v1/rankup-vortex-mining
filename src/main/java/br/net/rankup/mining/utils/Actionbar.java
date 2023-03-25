package br.net.rankup.mining.utils;

import org.bukkit.entity.*;
import org.bukkit.craftbukkit.v1_8_R3.entity.*;
import net.minecraft.server.v1_8_R3.*;

public class Actionbar
{
    public static void sendActionBar(final Player player, final String text) {
        final PacketPlayOutChat packetPlayOutChat = new PacketPlayOutChat(IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + text + "\"}"), (byte)2);
        ((CraftPlayer)player).getHandle().playerConnection.sendPacket((Packet)packetPlayOutChat);
    }
}
