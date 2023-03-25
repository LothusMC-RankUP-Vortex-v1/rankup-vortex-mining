package br.net.rankup.mining.enchantment.attributes;


import br.net.rankup.mining.enchantment.EnchantmentType;

public class EnchantmentAttributes
{
    private final EnchantmentType type;
    private final int maxLevel;
    private final double chance;
    private final double basePrice;
    private final double incrementPrice;
    
    public double getPrice(final int level) {
        return this.basePrice + this.incrementPrice * level;
    }
    
    public EnchantmentAttributes(final EnchantmentType type, final int maxLevel, final double chance, final double basePrice, final double incrementPrice) {
        this.type = type;
        this.maxLevel = maxLevel;
        this.chance = chance;
        this.basePrice = basePrice;
        this.incrementPrice = incrementPrice;
    }
    
    public EnchantmentType getType() {
        return this.type;
    }
    
    public int getMaxLevel() {
        return this.maxLevel;
    }
    
    public double getChance() {
        return this.chance;
    }
    
    public double getBasePrice() {
        return this.basePrice;
    }
    
    public double getIncrementPrice() {
        return this.incrementPrice;
    }
}
