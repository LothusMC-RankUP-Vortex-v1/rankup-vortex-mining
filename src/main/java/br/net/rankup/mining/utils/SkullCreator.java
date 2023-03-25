package br.net.rankup.mining.utils;

import org.bukkit.entity.*;
import org.bukkit.inventory.*;
import org.bukkit.block.*;
import org.bukkit.command.*;
import org.bukkit.*;
import java.net.*;
import java.util.*;

public class SkullCreator
{
    private static final Map<EntityType, String> URLS;
    
    public static ItemStack itemFromEntityType(final EntityType type) {
        if (!SkullCreator.URLS.containsKey(type)) {
            throw new UnsupportedOperationException("This entity don't has a hat");
        }
        return itemFromUrl(SkullCreator.URLS.get(type));
    }
    
    @Deprecated
    public static ItemStack itemFromName(final String name) {
        final ItemStack item = getPlayerSkullItem();
        return itemWithName(item, name);
    }
    
    @Deprecated
    public static ItemStack itemWithName(final ItemStack item, final String name) {
        notNull(item, "item");
        notNull(name, "name");
        return Bukkit.getUnsafe().modifyItemStack(item, "{SkullOwner:\"" + name + "\"}");
    }
    
    public static ItemStack itemFromUrl(final String url) {
        final ItemStack item = getPlayerSkullItem();
        return itemWithUrl(item, url);
    }
    
    public static ItemStack itemWithUrl(final ItemStack item, final String textureUrl) {
        final String url = "https://textures.minecraft.net/texture/" + textureUrl;
        notNull(item, "item");
        notNull(url, "url");
        return itemWithBase64(item, urlToBase64(url));
    }
    
    public static ItemStack itemFromBase64(final String base64) {
        final ItemStack item = getPlayerSkullItem();
        return itemWithBase64(item, base64);
    }
    
    public static ItemStack itemWithBase64(final ItemStack item, final String base64) {
        notNull(item, "item");
        notNull(base64, "base64");
        final UUID hashAsId = new UUID(base64.hashCode(), base64.hashCode());
        return Bukkit.getUnsafe().modifyItemStack(item, "{SkullOwner:{Id:\"" + hashAsId + "\",Properties:{textures:[{Value:\"" + base64 + "\"}]}}}");
    }
    
    public static void blockWithUrl(final Block block, final String url) {
        notNull(block, "block");
        notNull(url, "url");
        blockWithBase64(block, urlToBase64(url));
    }
    
    public static void blockWithBase64(final Block block, final String base64) {
        notNull(block, "block");
        notNull(base64, "base64");
        final UUID hashAsId = new UUID(base64.hashCode(), base64.hashCode());
        final String args = String.format("%d %d %d %s", block.getX(), block.getY(), block.getZ(), "{Owner:{Id:\"" + hashAsId + "\",Properties:{textures:[{Value:\"" + base64 + "\"}]}}}");
        if (newerApi()) {
            Bukkit.dispatchCommand((CommandSender)Bukkit.getConsoleSender(), "data merge block " + args);
        }
        else {
            Bukkit.dispatchCommand((CommandSender)Bukkit.getConsoleSender(), "blockdata " + args);
        }
    }
    
    private static boolean newerApi() {
        try {
            Material.valueOf("PLAYER_HEAD");
            return true;
        }
        catch (IllegalArgumentException e) {
            return false;
        }
    }
    
    private static ItemStack getPlayerSkullItem() {
        if (newerApi()) {
            return new ItemStack(Material.valueOf("PLAYER_HEAD"));
        }
        return new ItemStack(Material.valueOf("SKULL_ITEM"), 1, (short)3);
    }
    
    private static void setBlockType(final Block block) {
        try {
            block.setType(Material.valueOf("PLAYER_HEAD"), false);
        }
        catch (IllegalArgumentException e) {
            block.setType(Material.valueOf("SKULL"), false);
        }
    }
    
    private static void notNull(final Object o, final String name) {
        if (o == null) {
            throw new NullPointerException(name + " should not be null!");
        }
    }
    
    private static String urlToBase64(final String url) {
        URI actualUrl;
        try {
            actualUrl = new URI(url);
        }
        catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        final String toEncode = "{\"textures\":{\"SKIN\":{\"url\":\"" + actualUrl.toString() + "\"}}}";
        return Base64.getEncoder().encodeToString(toEncode.getBytes());
    }
    
    static {
        URLS = new HashMap<EntityType, String>() {
            {
                this.put(EntityType.COW, "5d6c6eda942f7f5f71c3161c7306f4aed307d82895f9d2b07ab4525718edc5");
                this.put(EntityType.PIG, "621668ef7cb79dd9c22ce3d1f3f4cb6e2559893b6df4a469514e667c16aa4");
                this.put(EntityType.BAT, "9e99deef919db66ac2bd28d6302756ccd57c7f8b12b9dca8f41c3e0a04ac1cc");
                this.put(EntityType.OCELOT, "5657cd5c2989ff97570fec4ddcdc6926a68a3393250c1be1f0b114a1db1");
                this.put(EntityType.CHICKEN, "1638469a599ceef7207537603248a9ab11ff591fd378bea4735b346a7fae893");
                this.put(EntityType.HORSE, "7bb4b288991efb8ca0743beccef31258b31d39f24951efb1c9c18a417ba48f9");
                this.put(EntityType.MUSHROOM_COW, "d0bc61b9757a7b83e03cd2507a2157913c2cf016e7c096a4d6cf1fe1b8db");
                this.put(EntityType.RABBIT, "cec242e667aee44492413ef461b810cac356b74d8718e5cec1f892a6b43e5e1");
                this.put(EntityType.SHEEP, "f31f9ccc6b3e32ecf13b8a11ac29cd33d18c95fc73db8a66c5d657ccb8be70");
                this.put(EntityType.SQUID, "d8705624daa2956aa45956c81bab5f4fdb2c74a596051e24192039aea3a8b8");
                this.put(EntityType.VILLAGER, "41b830eb4082acec836bc835e40a11282bb51193315f91184337e8d3555583");
                this.put(EntityType.CAVE_SPIDER, "41645dfd77d09923107b3496e94eeb5c30329f97efc96ed76e226e98224");
                this.put(EntityType.ENDERMAN, "96c0b36d53fff69a49c7d6f3932f2b0fe948e032226d5e8045ec58408a36e951");
                this.put(EntityType.IRON_GOLEM, "89091d79ea0f59ef7ef94d7bba6e5f17f2f7d4572c44f90f76c4819a714");
                this.put(EntityType.SPIDER, "cd541541daaff50896cd258bdbdd4cf80c3ba816735726078bfe393927e57f1");
                this.put(EntityType.WOLF, "69d1d3113ec43ac2961dd59f28175fb4718873c6c448dfca8722317d67");
                this.put(EntityType.PIG_ZOMBIE, "74e9c6e98582ffd8ff8feb3322cd1849c43fb16b158abb11ca7b42eda7743eb");
                this.put(EntityType.BLAZE, "b78ef2e4cf2c41a2d14bfde9caff10219f5b1bf5b35a49eb51c6467882cb5f0");
                this.put(EntityType.CREEPER, "f4254838c33ea227ffca223dddaabfe0b0215f70da649e944477f44370ca6952");
                this.put(EntityType.GHAST, "7a8b714d32d7f6cf8b37e221b758b9c599ff76667c7cd45bbc49c5ef19858646");
                this.put(EntityType.GUARDIAN, "932c24524c82ab3b3e57c2052c533f13dd8c0beb8bdd06369bb2554da86c123");
                this.put(EntityType.MAGMA_CUBE, "38957d5023c937c4c41aa2412d43410bda23cf79a9f6ab36b76fef2d7c429");
                this.put(EntityType.SILVERFISH, "92ec2c3cb95ab77f7a60fb4d160bced4b879329b62663d7a9860642e588ab210");
                this.put(EntityType.SKELETON, "301268e9c492da1f0d88271cb492a4b302395f515a7bbf77f4a20b95fc02eb2");
                this.put(EntityType.SLIME, "a20e84d32d1e9c919d3fdbb53f2b37ba274c121c57b2810e5a472f40dacf004f");
                this.put(EntityType.ZOMBIE, "56fc854bb84cf4b7697297973e02b79bc10698460b51a639c60e5e417734e11");
                this.put(EntityType.WITHER, "ee280cefe946911ea90e87ded1b3e18330c63a23af5129dfcfe9a8e166588041");
                this.put(EntityType.WITCH, "20e13d18474fc94ed55aeb7069566e4687d773dac16f4c3f8722fc95bf9f2dfa");
            }
        };
    }
}
