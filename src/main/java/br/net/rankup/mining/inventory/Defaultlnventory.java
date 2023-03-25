package br.net.rankup.mining.inventory;

import br.net.rankup.mining.MiningPlugin;
import br.net.rankup.mining.misc.BukkitUtils;
import br.net.rankup.mining.misc.InventoryUtils;
import br.net.rankup.mining.misc.ItemBuilder;
import br.net.rankup.mining.model.user.UserModel;
import br.net.rankup.mining.utils.SkullCreator;
import io.github.rysefoxx.inventory.plugin.content.IntelligentItem;
import io.github.rysefoxx.inventory.plugin.content.InventoryContents;
import io.github.rysefoxx.inventory.plugin.content.InventoryProvider;
import io.github.rysefoxx.inventory.plugin.pagination.RyseInventory;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Defaultlnventory implements InventoryProvider {

    private final FileConfiguration config;

    public Defaultlnventory(FileConfiguration config) {
        this.config = config;
    }

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

        ItemStack itemEnchant =
                new ItemBuilder(SkullCreator.itemFromUrl("f98bc63f05f6378bf29ef10e3d82acb3ceb73a720bf80f30bc576d0ad8c40cfb"))
                .setName("§aEncantamentos")
                .lore("§7Evolua os encantamentos de", "§7sua picareta.", "", "§aClique para ver.").build();

        IntelligentItem intelligentItem = IntelligentItem.of(itemEnchant, event -> {

            if(!player.getWorld().getName().equalsIgnoreCase("mines")) {
                player.playSound(player.getLocation(), Sound.VILLAGER_NO, 1.0f, 1.0f);
                BukkitUtils.sendMessage(player, "&cEssa função está liberada apenas no mundo de minas.");
                return;
            }

            if(!InventoryUtils.getList().contains(player.getName())) {
                RyseInventory inventory = new EnchantsInventory(MiningPlugin.getInstance().getConfig()).build();
                inventory.open(player);
                InventoryUtils.addDelay(player);
            }
            player.playSound(player.getLocation(), Sound.LEVEL_UP, 1.0f, 1.0f);
        });
        contents.set(11, intelligentItem);


        ItemStack itemClasse =
                new ItemBuilder(SkullCreator.itemFromUrl("7dc985a7a68c574f683c0b859521feb3fc3d2ffa05fa09db0bae44b8ac29b385"))
                        .setName("§aClasses")
                        .lore("§7Gerencie sua classe e ganhe", "§7diversas recompensas e bonús.", "", "§aClique para ver.").build();

        IntelligentItem intelligentClasse = IntelligentItem.of(itemClasse, event -> {
            if(!InventoryUtils.getList().contains(player.getName())) {
                RyseInventory inventory = new ClassesInventory(MiningPlugin.getInstance().getConfig()).build();
                inventory.open(player);
                InventoryUtils.addDelay(player);
            }
            player.playSound(player.getLocation(), Sound.LEVEL_UP, 1.0f, 1.0f);
        });
        contents.set(13, intelligentClasse);


        ItemStack itemMine =
                new ItemBuilder(SkullCreator.itemFromUrl("1321ab1673c95da208e75990242e398f13ac7b6467bb437fb3b7aa9f7cf3ce6d"))
                        .setName("§aMinas")
                        .lore("§7Ver todas as minas diponíveis", "§7do servidor.", "", "§aClique para ver.").build();

        IntelligentItem intelligentMine = IntelligentItem.of(itemMine, event -> {
            if(!InventoryUtils.getList().contains(player.getName())) {
                RyseInventory inventory = new MinesInventory(MiningPlugin.getInstance().getConfig()).build();
                inventory.open(player);
                InventoryUtils.addDelay(player);
            }
            player.playSound(player.getLocation(), Sound.LEVEL_UP, 1.0f, 1.0f);
        });
        contents.set(15, intelligentMine);
    }



    private boolean isInventoryEmpty(final Player player) {
        for (final ItemStack item : player.getInventory().getContents()) {
            if (item != null && item.getType() != Material.AIR) {
                return false;
            }
        }
        for (final ItemStack item : player.getInventory().getArmorContents()) {
            if (item != null && item.getType() != Material.AIR) {
                return false;
            }
        }
        return true;
    }


}

