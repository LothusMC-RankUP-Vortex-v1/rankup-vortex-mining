package br.net.rankup.mining.inventory;

import br.net.rankup.mining.MiningPlugin;
import br.net.rankup.mining.enchantment.EnchantmentType;
import br.net.rankup.mining.enchantment.attributes.EnchantmentAttributes;
import br.net.rankup.mining.misc.BukkitUtils;
import br.net.rankup.mining.misc.InventoryUtils;
import br.net.rankup.mining.misc.ItemBuilder;
import br.net.rankup.mining.model.user.UserModel;
import br.net.rankup.mining.utils.CurrencyUtils;
import br.net.rankup.mining.utils.SkullCreator;
import br.net.rankup.mining.utils.Toolchain;
import io.github.rysefoxx.inventory.plugin.content.IntelligentItem;
import io.github.rysefoxx.inventory.plugin.content.InventoryContents;
import io.github.rysefoxx.inventory.plugin.content.InventoryProvider;
import io.github.rysefoxx.inventory.plugin.pagination.RyseInventory;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class EnchantsInventory implements InventoryProvider {


    private final FileConfiguration config;


    public EnchantsInventory(FileConfiguration config) {
        this.config = config;
    }

    public RyseInventory build() {
        return RyseInventory.builder()
                .title("Encantamentos".replace("&", "§"))
                .rows(5)
                .provider(this)
                .disableUpdateTask()
                .build(MiningPlugin.getInstance());
    }

    @Override
    public void init(Player player, InventoryContents contents) {

        final UserModel userModel = MiningPlugin.getInstance().getUserCache().getById(player.getUniqueId());
        if (userModel == null) {
            return;
        }

        ItemStack itemStackInfo = new ItemBuilder(
                SkullCreator.itemFromName(player.getName()))
                .setName("§eInformações")
                .lore(new String[]{"§fBlocos quebrados: §7" + Toolchain.formatWithoutReduce(userModel.getCount())
                        , "§fRúbis atuais: §4✦§c" + Toolchain.format(CurrencyUtils.getRubis(player))}).build();


        IntelligentItem intelligentInfo = IntelligentItem.of(itemStackInfo, event -> {
            event.setCancelled(true);
        });
        contents.set(3, intelligentInfo);

        IntelligentItem intelligentHand = IntelligentItem.of(player.getItemInHand(), event -> {
            event.setCancelled(true);
        });
        contents.set(5, intelligentHand);

        ItemStack itemStack = new ItemBuilder(Material.ARROW).setName("§aPágina anterior").toItemStack();
        IntelligentItem intelligentItemBack = IntelligentItem.of(itemStack, event -> {
            if(!InventoryUtils.getList().contains(player.getName())) {
                RyseInventory inventory = new Defaultlnventory(MiningPlugin.getConfiguration()).build();
                inventory.open(player);
                InventoryUtils.addDelay(player);
                player.playSound(player.getLocation(), Sound.CHEST_OPEN, 5.0f, 5.0f);
            }
        });
        contents.set(36, intelligentItemBack);

        EnchantmentType[] var4 = EnchantmentType.values();
        int var5 = var4.length;
        for(int var6 = 0; var6 < var5; ++var6) {
            final EnchantmentType type = var4[var6];
            EnchantmentAttributes attributes = MiningPlugin.getInstance().getAttributesCache().getByType(type);
            if(attributes == null) break;
            int currentLevel = userModel.getEnchantmentLevel(type);
            if (currentLevel < attributes.getMaxLevel()) {
                    IntelligentItem intelligentEnchant = IntelligentItem.of(this.enchantItem(userModel, type, currentLevel), event -> {
                        if(!InventoryUtils.getList().contains(player.getName())) {
                            RyseInventory inventory = new EnchantBuylnventory(MiningPlugin.getInstance().getConfig(), type).build();
                            inventory.open(player);
                            InventoryUtils.addDelay(player);
                        }
                        event.setCancelled(true);
                        player.playSound(player.getLocation(), Sound.LEVEL_UP, 1.0f, 1.0f);
                    });
                    contents.set(type.getSlot(), intelligentEnchant);
            } else {
                IntelligentItem intelligentEnchant = IntelligentItem.of(this.maxEnchantmentLevelItem(type, currentLevel), event -> {
                    event.setCancelled(true);
                    player.closeInventory();
                    BukkitUtils.sendMessage(player, "&cVocê atingiu o nível máximo desse encantanto.");
                    player.playSound(player.getLocation(), Sound.VILLAGER_NO, 1.0f, 1.0f);
                });
                contents.set(type.getSlot(), intelligentEnchant);
            }
        }

    }


    private ItemStack enchantItem(UserModel userModel, EnchantmentType type, int currentLevel) {
        EnchantmentAttributes attributes = MiningPlugin.getInstance().getAttributesCache().getByType(type);
        double chance = attributes.getChance() * (double)(currentLevel + 1);
        if (chance > 100.0D) {
            chance = 100.0D;
        }

        return (new ItemBuilder(type.getItemStack().clone()))
                .setName("§a" + type.getFriendlyName() + " §e(" + Toolchain.formatPercentage(chance) + "%)")
                .lore(type.getDescription())
                .addLoreLine("")
                .addLoreLine("§f Nível máximo: §7" + Toolchain.formatWithoutReduce((double)attributes.getMaxLevel()))
                .addLoreLine("§f Custo por nível: §4✦§c" + Toolchain.format(this.getPrice(userModel, type, 1)))
                .addLoreLine("")
                .addLoreLine("§aClique para abrir.").build();
    }

    private ItemStack maxEnchantmentLevelItem(EnchantmentType type, int currentLevel) {
        EnchantmentAttributes attributes = MiningPlugin.getInstance().getAttributesCache().getByType(type);
        double chance = attributes.getChance() * (double)(currentLevel + 1);
        if (chance > 100.0D) {
            chance = 100.0D;
        }

        return (new ItemBuilder(type.getItemStack().clone()))
                .setName("§a" + type.getFriendlyName() + " §e(" + Toolchain.formatPercentage(chance) + "%)")
                .lore("§7Nível máximo atingido!").build();
    }

    private double getPrice(UserModel userModel, EnchantmentType type, int level) {
        double price = 0.0D;
        EnchantmentAttributes attributes = MiningPlugin.getInstance().getAttributesCache().getByType(type);
        int currentLevel = userModel.getEnchantmentLevel(type);

        for(int i = currentLevel; i < currentLevel + level; ++i) {
            if (i < attributes.getMaxLevel()) {
                price += attributes.getBasePrice() + (double)i * attributes.getIncrementPrice();
            }
        }

        return price;
    }

}

