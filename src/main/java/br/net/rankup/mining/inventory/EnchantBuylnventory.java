package br.net.rankup.mining.inventory;

import br.net.rankup.mining.MiningPlugin;
import br.net.rankup.mining.enchantment.EnchantmentType;
import br.net.rankup.mining.enchantment.attributes.EnchantmentAttributes;
import br.net.rankup.mining.misc.InventoryUtils;
import br.net.rankup.mining.misc.ItemBuilder;
import br.net.rankup.mining.model.user.UserBuy;
import br.net.rankup.mining.model.user.UserModel;
import br.net.rankup.mining.utils.CurrencyUtils;
import br.net.rankup.mining.utils.SkullCreator;
import br.net.rankup.mining.utils.Toolchain;
import io.github.rysefoxx.inventory.plugin.content.IntelligentItem;
import io.github.rysefoxx.inventory.plugin.content.InventoryContents;
import io.github.rysefoxx.inventory.plugin.content.InventoryProvider;
import io.github.rysefoxx.inventory.plugin.pagination.RyseInventory;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EnchantBuylnventory implements InventoryProvider {

    private final FileConfiguration config;

    private EnchantmentType type;

    public EnchantBuylnventory(FileConfiguration config, final EnchantmentType type) {
        this.type = type;
        this.config = config;
    }

    public static final Map<UUID, UserBuy> MAP = new HashMap<>();

    public RyseInventory build() {
        return RyseInventory.builder()
                .title("Mineração".replace("&", "§"))
                .rows(3)
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

        ItemStack itemStack = new ItemBuilder(Material.ARROW).setName("§aPágina anterior").toItemStack();
        IntelligentItem intelligentItemBack = IntelligentItem.of(itemStack, event -> {
            if(!InventoryUtils.getList().contains(player.getName())) {
                RyseInventory inventory = new EnchantsInventory(MiningPlugin.getConfiguration()).build();
                inventory.open(player);
                InventoryUtils.addDelay(player);
                player.playSound(player.getLocation(), Sound.CHEST_OPEN, 5.0f, 5.0f);
            }
        });
        contents.set(18, intelligentItemBack);

        ItemStack itemAmount1 =
                new ItemBuilder(
                        SkullCreator.itemFromUrl("18e790411d8e90e946ddbbb3b7369f79dd47b8bb9a351e8cf0f8f59a6d298865"))
        .setName("§aAdquirir 1 nível")
                        .lore(new String[]{"§7Ganhe §f1 §7nível de"
                                , "§f" + type.getFriendlyName().toLowerCase() + " §7na sua picareta."
                                , "", "§f Custo: §4✦§c" + Toolchain.format(this.getPrice(userModel, type, 1))
                                , "", "§aClique para adquirir!"}).build();
        IntelligentItem intelligentItem = IntelligentItem.of(itemAmount1, event -> {
            if (!this.verifyItem(player)) {
                player.sendMessage("§cVocê não tem uma picareta na mão.");
            } else {
                this.addEnchantment(userModel, type, 1);
            }
            if(!InventoryUtils.getList().contains(player.getName())) {
                RyseInventory inventory = new EnchantBuylnventory(MiningPlugin.getInstance().getConfig(), type).build();
                inventory.open(player);
                InventoryUtils.addDelay(player);
            }
            player.playSound(player.getLocation(), Sound.LEVEL_UP, 1.0f, 1.0f);
        });
        contents.set(10, intelligentItem);

        ItemStack itemAmount10 =
                new ItemBuilder(
                        SkullCreator.itemFromUrl("18e790411d8e90e946ddbbb3b7369f79dd47b8bb9a351e8cf0f8f59a6d298865"))
                        .setName("§aAdquirir 10 nível")
                        .lore(new String[]{"§7Ganhe §f1 §7nível de"
                                , "§f" + type.getFriendlyName().toLowerCase() + " §7na sua picareta."
                                , "", "§f Custo: §4✦§c" + Toolchain.format(this.getPrice(userModel, type, 10))
                                , "", "§aClique para adquirir!"}).build();
        IntelligentItem intelligentItem2 = IntelligentItem.of(itemAmount10, event -> {
            if (!this.verifyItem(player)) {
                player.sendMessage("§cVocê não tem uma picareta na mão.");
            } else {
                this.addEnchantment(userModel, type, 10);
            }
            if(!InventoryUtils.getList().contains(player.getName())) {
                RyseInventory inventory = new EnchantBuylnventory(MiningPlugin.getInstance().getConfig(), type).build();
                inventory.open(player);
                InventoryUtils.addDelay(player);
            }
            player.playSound(player.getLocation(), Sound.LEVEL_UP, 1.0f, 1.0f);
        });
        contents.set(13, intelligentItem2);


        ItemStack itemAmountPer =
                new ItemBuilder(
                        SkullCreator.itemFromUrl("18e790411d8e90e946ddbbb3b7369f79dd47b8bb9a351e8cf0f8f59a6d298865"))
                        .setName("§aPersonalizar Compra")
                        .lore(new String[]{"§7Clique para adquirir os", "§7níveis de §f" + type.getFriendlyName().toLowerCase() + " §7desejar."}).build();
        IntelligentItem intelligentPer = IntelligentItem.of(itemAmountPer, event -> {
            player.closeInventory();
            player.sendMessage(new String[]{"", "§e Digite a quantia de níveis que deseja adicionar.", "§7 Caso queira cancelar, digite 'cancelar'.", ""});
            UserBuy userBuy = new UserBuy(userModel, type);
            EnchantBuylnventory.MAP.put(player.getUniqueId(), userBuy);
            player.playSound(player.getLocation(), Sound.LEVEL_UP, 1.0f, 1.0f);
        });
        contents.set(16, intelligentPer);

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

    public static boolean addEnchantment(UserModel userModel, EnchantmentType type, int level) {
        Player player = Bukkit.getPlayer(userModel.getId());
        double price = 0.0D;
        int toAdd = 0;
        EnchantmentAttributes attributes = MiningPlugin.getInstance().getAttributesCache().getByType(type);
        int currentLevel = userModel.getEnchantmentLevel(type);

        for(int i = currentLevel; i < currentLevel + level; ++i) {
            if (i < attributes.getMaxLevel()) {
                price += attributes.getBasePrice() + (double)i * attributes.getIncrementPrice();
                ++toAdd;
            }
        }

        if (CurrencyUtils.getRubis(player) >= price) {
            CurrencyUtils.removeGems(player, price);
            userModel.addEnchantment(type, toAdd);
            player.sendMessage("§aAdicionado " + toAdd + " níveís de " + type.getFriendlyName() + " na sua picareta.");
            return true;
        } else {
            player.sendMessage("§cVocê precisa de §4✦§c" + Toolchain.format(price) + " rubis para comprar esta evolução.");
            return false;
        }
    }

    public static boolean verifyItem(Player player) {
        ItemStack itemInHand = player.getItemInHand();
        if (itemInHand == null) {
            return false;
        } else {
            return itemInHand.getType() == Material.DIAMOND_PICKAXE;
        }
    }


}

