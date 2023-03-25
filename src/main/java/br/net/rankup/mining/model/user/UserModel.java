package br.net.rankup.mining.model.user;

import br.net.rankup.mining.Constants;
import br.net.rankup.mining.MiningPlugin;
import br.net.rankup.mining.enchantment.EnchantmentType;
import br.net.rankup.mining.enchantment.tool.EnchantmentTool;
import br.net.rankup.ranks.RankPlugin;
import br.net.rankup.ranks.manager.UserManager;
import br.net.rankup.ranks.model.ParentRankModel;
import org.bukkit.entity.*;
import org.bukkit.*;
import org.bukkit.inventory.*;
import java.util.*;

public class UserModel
{
    private final UUID id;
    private final String name;
    private double count;
    private Double playerClass;
    private final Map<EnchantmentType, Integer> enchantments;

    public ParentRankModel getRank() {
        final br.net.rankup.ranks.model.UserModel userModel = UserManager.getById(this.id);
        if (userModel != null) {
            return userModel.getChildRank().getParentRank();
        }
        return null;
    }

    public double getPrestigeBonus() {
        final br.net.rankup.ranks.model.UserModel userModel = UserManager.getById(this.id);
        if (userModel == null) {
            return 0.0;
        }
        return userModel.getSellBonus();
    }

    public boolean hasPrestige() {
        final br.net.rankup.ranks.model.UserModel userModel = UserManager.getById(this.id);
        return userModel != null && userModel.getPrestige() > 0;
    }
    public int getEnchantmentLevel(final EnchantmentType type) {
        return this.enchantments.getOrDefault(type, 0);
    }
    
    public void addEnchantment(final EnchantmentType type, int level) {
        level += this.getEnchantmentLevel(type);
        this.enchantments.put(type, level);
        final Player player = Bukkit.getPlayer(this.id);
        if (player != null) {
            final EnchantmentTool enchantmentTool = MiningPlugin.getInstance().getEnchantmentTool();
            enchantmentTool.updateMeta(player.getItemInHand(), this);
        }
    }
    
    public void removeIfContains(final Inventory inventory) {
        for (final ItemStack content : inventory.getContents()) {
            if (content != null && content.getType() == Material.DIAMOND_PICKAXE) {
                if (content.hasItemMeta()) {
                    if (content.getItemMeta().hasDisplayName()) {
                        if (content.getItemMeta().getDisplayName().contains("§bPicareta de Diamante")) {
                            inventory.removeItem(new ItemStack[] { content });
                        }
                    }
                }
            }
        }
    }

    public UserModel(final UUID id, final String name) {
        this.enchantments = new HashMap<EnchantmentType, Integer>();
        this.id = id;
        this.name = name;
    }
    
    public UUID getId() {
        return this.id;
    }
    
    public String getName() {
        return this.name;
    }
    
    public double getCount() {
        return this.count;
    }
    
    public Map<EnchantmentType, Integer> getEnchantments() {
        return this.enchantments;
    }
    
    public void setCount(final double count) {
        this.count = count;
    }

    public Double getPlayerClass() {
        return playerClass;
    }
    public void setPlayerClass(final Double playerClass) {
        this.playerClass = playerClass;
    }
}
