package br.net.rankup.mining.enchantment.attributes;

import br.net.rankup.mining.enchantment.EnchantmentType;
import br.net.rankup.mining.utils.Cache;
import org.bukkit.configuration.file.*;
import org.bukkit.configuration.*;

public class EnchantmentAttributesLoader
{
    public void load(final Cache<EnchantmentAttributes> cache, final FileConfiguration config) {
        for (final String key : config.getConfigurationSection("enchantments").getKeys(false)) {
            final ConfigurationSection section = config.getConfigurationSection("enchantments." + key);
            final EnchantmentType type = EnchantmentType.valueOf(key);
            final int maxLevel = section.getInt("max-level");
            final double chance = section.getDouble("chance");
            final double basePrice = section.getDouble("base-price");
            final double incrementPrice = section.getDouble("increment-price");
            final EnchantmentAttributes attributes = new EnchantmentAttributes(type, maxLevel, chance, basePrice, incrementPrice);
            cache.addElement(attributes);
        }
    }
}
