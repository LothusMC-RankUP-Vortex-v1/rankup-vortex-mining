package br.net.rankup.mining.cache;

import br.net.rankup.mining.MiningPlugin;
import br.net.rankup.mining.enchantment.EnchantmentType;
import br.net.rankup.mining.enchantment.attributes.EnchantmentAttributes;
import br.net.rankup.mining.utils.Cache;

public class EnchantmentAttributesCache extends Cache<EnchantmentAttributes>
{
    private final MiningPlugin plugin;
    
    public EnchantmentAttributesCache(final MiningPlugin plugin) {
        this.plugin = plugin;
    }
    
    public EnchantmentAttributes getByType(final EnchantmentType type) {
        return this.get(attributes -> attributes.getType() == type);
    }
    
    public MiningPlugin getPlugin() {
        return this.plugin;
    }
}
