package br.net.rankup.mining.inventory;

import br.net.rankup.mining.MiningPlugin;
import br.net.rankup.mining.manager.ClassesManager;
import br.net.rankup.mining.misc.BukkitUtils;
import br.net.rankup.mining.misc.InventoryUtils;
import br.net.rankup.mining.misc.ItemBuilder;
import br.net.rankup.mining.model.Classes;
import br.net.rankup.mining.model.user.UserModel;
import br.net.rankup.mining.utils.CurrencyUtils;
import br.net.rankup.rubis.managers.RubisManager;
import com.google.common.collect.ImmutableList;
import io.github.rysefoxx.inventory.plugin.content.IntelligentItem;
import io.github.rysefoxx.inventory.plugin.content.InventoryContents;
import io.github.rysefoxx.inventory.plugin.content.InventoryProvider;
import io.github.rysefoxx.inventory.plugin.pagination.Pagination;
import io.github.rysefoxx.inventory.plugin.pagination.RyseInventory;
import io.github.rysefoxx.inventory.plugin.pagination.SlotIterator;
import io.github.rysefoxx.inventory.plugin.pattern.SlotIteratorPattern;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Iterator;
import java.util.TreeMap;

public class ClassesInventory implements InventoryProvider {

    private final FileConfiguration config;

    public ClassesInventory(FileConfiguration config) {
        this.config = config;
    }

    public RyseInventory build() {
        return RyseInventory.builder()
                .title("Classes".replace("&", "§"))
                .rows(4)
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

        if (ClassesManager.getClasses().isEmpty()) {
            ItemStack empty = new ItemBuilder(Material.WEB, 1, 0)
                    .owner(player.getName()).setName("§cVazio").setLore(ImmutableList.of(
                            "§7Não existe nenhuma classe."
                    )).build();
            contents.set(22, empty);
        } else {
            Pagination pagination = contents.pagination();
            pagination.iterator(SlotIterator.builder().withPattern(SlotIteratorPattern.builder().define(
                                    "XXXXXXXXX",
                                    "XOOOOOOOX",
                                    "XOOOOOOOX",
                                    "XOOOOOOOX",
                                    "XOOOOOOOX",
                                    "XXXXXXXXX")
                            .attach('O')
                            .buildPattern())
                    .build());
            pagination.setItemsPerPage(7);


            if (pagination.isFirst()) {
                ItemStack itemStack = new ItemBuilder(Material.ARROW).setName("§aPágina anterior").toItemStack();
                IntelligentItem intelligentItem2 = IntelligentItem.of(itemStack, event -> {
                    if(!InventoryUtils.getList().contains(player.getName())) {
                        RyseInventory inventory = new Defaultlnventory(MiningPlugin.getConfiguration()).build();
                        inventory.open(player);
                        InventoryUtils.addDelay(player);
                        player.playSound(player.getLocation(), Sound.CHEST_OPEN, 5.0f, 5.0f);
                    }
                });
                contents.set(27, intelligentItem2);
            }

            if (!pagination.isFirst()) {
                ItemStack itemStack = new ItemBuilder(Material.ARROW).setName("§aPágina anterior").toItemStack();
                IntelligentItem intelligentItem = IntelligentItem.of(itemStack, event -> {
                    if (event.isLeftClick()) {
                        player.playSound(player.getLocation(), Sound.LEVEL_UP, 1.0f, 1.0f);
                        pagination.inventory().open(player, pagination.previous().page());
                    }
                });
                contents.set(27, intelligentItem);
            }

            if (!pagination.isLast()) {
                ItemStack itemStack = new ItemBuilder(Material.ARROW).setName("§aPróxima página").toItemStack();
                IntelligentItem intelligentItem = IntelligentItem.of(itemStack, event -> {
                    if (event.isLeftClick()) {
                        player.playSound(player.getLocation(), Sound.LEVEL_UP, 1.0f, 1.0f);
                        pagination.inventory().open(player, pagination.next().page());
                    }
                });
                contents.set(35, intelligentItem);
            }

            ClassesManager.getClasses().keySet().iterator();
            TreeMap<Double, Classes> tm = new TreeMap<Double, Classes>(ClassesManager.getClasses());
            Iterator itr = tm.keySet().iterator();
            while (itr.hasNext()) {
                double key = (Double) itr.next();
                Classes classes = ClassesManager.getClasses().get(key);
                IntelligentItem intelligentItem = IntelligentItem.of(classes.getItemStack(userModel, classes), event -> {
                    if(userModel.getPlayerClass().equals(key)) {
                        BukkitUtils.sendMessage(player, "&cVocê já tem essa classe.");
                        player.closeInventory();
                        return;
                    }
                    if(userModel.getPlayerClass().equals(classes.getIndentifier())) {
                        BukkitUtils.sendMessage(player, "&cVocê já está nessa classe.");
                        player.closeInventory();;
                        return;
                    }
                    if(classes.getIndentifier()<userModel.getPlayerClass()) {
                        BukkitUtils.sendMessage(player, "&cVocê já tem essa classe.");
                        player.closeInventory();;
                        return;
                    }
                    if(!ClassesManager.getClasses().containsKey(key)) {
                        BukkitUtils.sendMessage(player, "&cVocê já está na classe máxima.");
                        player.closeInventory();
                        return;
                    }
                    if(userModel.getPlayerClass()+1 != classes.getIndentifier()) {
                        BukkitUtils.sendMessage(player, "&cVocê precisar evoluir em sequência.");
                        player.closeInventory();
                        return;
                    }
                    Classes newClasse = ClassesManager.getClasses().get(key);
                    if(!has(player, newClasse))  {
                        BukkitUtils.sendMessage(player, "&cVocê não tem os requisitos para evoluir de classe.");
                        player.closeInventory();
                        return;
                    }
                    CurrencyUtils.removeGems(player, newClasse.getPriceRubis());
                    MiningPlugin.getInstance().getEconomy().withdrawPlayer(player, newClasse.getPriceCoins());
                    Double indetifier = (double) (key);
                    userModel.setPlayerClass(indetifier);
                    MiningPlugin.getInstance().getUsersRepository().update(userModel);
                    BukkitUtils.sendMessage(player, "&aYAY! Você evoluiu sua classe.");
                    player.closeInventory();
                    player.playSound(player.getLocation(), Sound.LEVEL_UP, 1.0f, 1.0f);
                });
                pagination.addItem(intelligentItem);
            }
        }
    }


    public boolean has(Player player, Classes classes) {
        double rubis = 0;
        if(RubisManager.getUsers().containsKey(player.getName())) {
            rubis = RubisManager.getUsers().get(player.getName()).getAmount();
        } else {
            return false;
        }
        boolean has = MiningPlugin.getInstance().getEconomy().getBalance(player) >= classes.getPriceCoins() && rubis >= classes.getPriceRubis();
        return has;
    }

}

