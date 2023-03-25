package br.net.rankup.mining.enchantment;

import br.net.rankup.mining.MiningPlugin;
import br.net.rankup.mining.enchantment.attributes.EnchantmentAttributes;
import br.net.rankup.mining.misc.ItemBuilder;
import br.net.rankup.mining.utils.SkullCreator;
import org.bukkit.*;
import org.bukkit.inventory.*;

public enum EnchantmentType
{
    FORTUNE("Fortuna", new ItemStack(Material.EMERALD)
            , new String[] { "§7Com este encantamento, você", "§7ganhará mais drops por bloco", "§7quebrado." }, 19),
    EXPLOSIVE("Explosão", new ItemStack(Material.TNT)
            , new String[] { "§7Com este encantamento, você", "§7terá chance de fazer um", "§7buraco §f3x3 §7na mina." }, 20),
    PERFORATOR("Perfurador", new ItemStack(Material.PISTON_BASE)
            , new String[] { "§7Com este encantamento, você", "§7terá chance de quebrar uma", "§7camada inteira da mina." }, 21),
    LASER("Laser", new ItemStack(Material.BLAZE_ROD)
            , new String[] { "§7Com este encantamento, você", "§7terá chance de quebrar uma", "§7linha inteira da mina." }, 22),
    TANKER("Petroleiro", new ItemStack(Material.COAL, 1, (short)1)
            , new String[] { "§7Com este encantamento, você", "§7terá chance de ganhar", "§7combustíveis minerando." }, 23),
    LOCKSMITH("Chaveiro", new ItemStack(Material.TRIPWIRE_HOOK)
            , new String[] { "§7Com este encantamento, você", "§7terá chance de ganhar", "§7chaves minerando." }, 24),
    JEWELLER("Joalheiro", new ItemStack(SkullCreator.itemFromUrl("5e48615df6b7ddf3ad495041876d9169bdc983a3fa69a2aca107e8f251f7687"))
            , new String[] { "§7Com este encantamento, você", "§7terá chance de ganhar mais", "§7rubis enquanto minera." }, 25),
    SPEED("Velocidade", new ItemBuilder(Material.POTION, 1, 8258)
            .addItemFlag(ItemFlag.HIDE_POTION_EFFECTS)
            .addItemFlag(ItemFlag.HIDE_ATTRIBUTES).build()
            , new String[] { "§7Com este encantamento, você", "§7terá chance de ganhar efeito", "§7de velocidade enquanto minera." }, 29),
    NUKER("Nuker", new ItemStack(SkullCreator.itemFromUrl("7faf3efbff6d7ef465ecacbc517f4dad5cc1a2261ea7a609f216aae48784"))
            , new String[] { "§7Com este encantamento, você", "§7terá chance de destruir", "§7a mina por completo." }, 30),
    DRAWER("Sorteador", new ItemStack(SkullCreator.itemFromUrl("1762a15b04692a2e4b3fb3663bd4b78434dce1732b8eb1c7a9f7c0fbf6f"))
            , new String[] { "§7Com este encantamento, você", "§7terá chance de receber", "§7um nível de um encantamento", "§7aleat\u00f3rio." }, 32),
    LIMITER("Limitador", new ItemStack(Material.NAME_TAG)
            , new String[] { "§7Com este encantamento, você", "§7terá chance de receber", "§7limites de compra enquanto minera." }, 33);
    
    private final String friendlyName;
    private final ItemStack itemStack;
    private final String[] description;
    private final int slot;
    
    public EnchantmentAttributes getAttributes() {
        return MiningPlugin.getInstance().getAttributesCache().getByType(this);
    }
    
    public String getFriendlyName() {
        return this.friendlyName;
    }
    
    public ItemStack getItemStack() {
        return this.itemStack;
    }
    
    public String[] getDescription() {
        return this.description;
    }
    
    public int getSlot() {
        return this.slot;
    }
    
    private EnchantmentType(final String friendlyName, final ItemStack itemStack, final String[] description, final int slot) {
        this.friendlyName = friendlyName;
        this.itemStack = itemStack;
        this.description = description;
        this.slot = slot;
    }
}
