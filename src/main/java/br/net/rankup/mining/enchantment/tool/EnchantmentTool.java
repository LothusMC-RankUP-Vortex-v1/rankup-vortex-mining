package br.net.rankup.mining.enchantment.tool;

import br.net.rankup.mining.MiningPlugin;
import br.net.rankup.mining.enchantment.EnchantmentType;
import br.net.rankup.mining.model.user.UserModel;
import br.net.rankup.mining.utils.Toolchain;
import org.bukkit.*;
import org.bukkit.inventory.*;
import org.bukkit.enchantments.*;
import java.util.*;
import org.bukkit.inventory.meta.*;

public class EnchantmentTool
{
    private final MiningPlugin plugin;
    
    public EnchantmentTool(final MiningPlugin plugin) {
        this.plugin = plugin;
    }
    
    public ItemStack provideItem(final UserModel userModel) {
        final ItemStack itemStack = new ItemStack(Material.DIAMOND_PICKAXE);
        this.updateMeta(itemStack, userModel);
        return itemStack;
    }
    
    public void updateMeta(final ItemStack itemStack, final UserModel userModel) {
        final ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.addItemFlags(ItemFlag.values());
        itemMeta.setDisplayName("§bPicareta de Diamante §7[" + Toolchain.formatWithoutReduce(userModel.getCount()) + "]");
        final List<String> lore = new ArrayList<String>();
        lore.add("§7Efici\u00eancia \u221e");
        lore.add("§7Inquebr\u00e1vel \u221e");
        for (final EnchantmentType value : EnchantmentType.values()) {
            final int level = userModel.getEnchantmentLevel(value);
            if (level > 0) {
                lore.add("§7" + value.getFriendlyName() + " " + Toolchain.formatWithoutReduce(level));
            }
        }
        itemMeta.addEnchant(Enchantment.DIG_SPEED, 30000, true);
        itemMeta.addEnchant(Enchantment.DURABILITY, 30000, true);
        itemMeta.setLore((List)lore);
        itemStack.setItemMeta(itemMeta);
    }
    
    private List<String> parseLore(final UserModel userModel) {
        final List<String> lore = new ArrayList<String>();
        lore.add("§7Efici\u00eancia \u221e");
        lore.add("§7Inquebr\u00e1vel \u221e");
        for (final EnchantmentType value : EnchantmentType.values()) {
            final int level = userModel.getEnchantmentLevel(value);
            if (level > 0) {
                lore.add("§7" + value.getFriendlyName() + " " + Toolchain.formatWithoutReduce(level));
            }
        }
        return lore;
    }
    
    public void updateDisplayName(final ItemStack itemStack, final UserModel userModel) {
        final ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName("§bPicareta de Diamante §7[" + Toolchain.formatWithoutReduce(userModel.getCount()) + "]");
        itemStack.setItemMeta(itemMeta);
    }
    
    public MiningPlugin getPlugin() {
        return this.plugin;
    }
}
