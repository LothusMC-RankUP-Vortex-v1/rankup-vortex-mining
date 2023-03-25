package br.net.rankup.mining.inventory;

import br.net.rankup.core.systems.scoreboard.api.ScoreAPI;
import br.net.rankup.mining.MiningPlugin;
import br.net.rankup.mining.misc.ItemBuilder;
import br.net.rankup.mining.misc.SkullCreatorUtils;
import br.net.rankup.mining.model.mine.MineModel;
import br.net.rankup.mining.model.user.UserModel;
import br.net.rankup.mining.Constants;
import br.net.rankup.ranks.manager.RankManager;
import br.net.rankup.ranks.model.ParentRankModel;
import io.github.rysefoxx.inventory.plugin.content.IntelligentItem;
import io.github.rysefoxx.inventory.plugin.content.InventoryContents;
import io.github.rysefoxx.inventory.plugin.content.InventoryProvider;
import io.github.rysefoxx.inventory.plugin.pagination.RyseInventory;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class MinesInventory implements InventoryProvider {


    private final FileConfiguration config;


    private final int[] slots;

    public MinesInventory(FileConfiguration config) {
        this.config = config;
        slots = new int[] { 11, 12, 13, 14, 15, 21, 22, 23 };
    }

    public RyseInventory build() {
        return RyseInventory.builder()
                .title("Minas".replace("&", "§"))
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

        ItemStack itemMineVIP = new ItemBuilder(Material.PACKED_ICE)
                .setName(this.isInventoryEmpty(player) ? "§bMina VIP" : "§cMina VIP")
                .lore("§7Clique para acessar a"
                        , "§7mina restrita a jogadores"
                        , "§7com rank §aVIP §7ou superior."
                        , ""
                        , player.hasPermission("group.vip") ?
                        (this.isInventoryEmpty(player) ? "§bClique para entrar!"
                        : "§cEsvazie o invent\u00e1rio.")
                        : "§cSem acesso.").build();

        IntelligentItem intelligentItem = IntelligentItem.of(itemMineVIP, event -> {
            if (!player.hasPermission("group.vip")) {
                player.sendMessage("§cVoc\u00ea n\u00e3o possui permiss\u00e3o para acessar esta mina.");
                return;
            }
            if (!this.isInventoryEmpty(player)) {
                player.sendMessage("§cEsvazie o invent\u00e1rio!");
                return;
            }
            final MineModel mineModel = MiningPlugin.getInstance().getMineCache().getByName("minavip");
            if (mineModel == null) {
                player.sendMessage("§cA localiza\u00e7\u00e3o de spawn desta mina n\u00e3o est\u00e1 definida.");
                return;
            }
            player.closeInventory();
            player.teleport(mineModel.getSpawnLocation());
            player.getInventory().addItem(new ItemStack[] { MiningPlugin.getInstance().getEnchantmentTool().provideItem(userModel) });
            Constants.vipMine.add(player);
            ScoreAPI.setScoreBoard(player, "MINA_VIP");
            player.playSound(player.getLocation(), Sound.LEVEL_UP, 10.0f, 10.0f);
        });
        contents.set(31, intelligentItem);


        int index = 0;
        for (int i = 0; i <= 9; ++i) {
            final ParentRankModel rankModel = RankManager.getByPosition(i);
            if(rankModel == null) break;
            final ItemBuilder itemBuilder = new ItemBuilder(this.getByRank(i));
            if (userModel.getRank().getRankPosition() >= i || player.isOp()) {
                if (!this.isInventoryEmpty(player)) {
                    itemBuilder.setName("§cMina " + ChatColor.stripColor(rankModel.getFancyName().replace("Rank ", "")));
                    itemBuilder.lore("§7Clique para ir at\u00e9 \u00e0", "§7\u00e1rea de minera\u00e7\u00e3o do", "§7rank " + ChatColor.stripColor(rankModel.getFancyName().replace("Rank ", "")), "", "§cEsvazie o invent\u00e1rio.");
                }
                else {
                    itemBuilder.setName("§aMina " + ChatColor.stripColor(rankModel.getFancyName().replace("Rank ", "")));
                    itemBuilder.lore("§7Clique para ir at\u00e9 \u00e0", "§7\u00e1rea de minera\u00e7\u00e3o do", "§7rank " + ChatColor.stripColor(rankModel.getFancyName().replace("Rank ", "")), "", "§aClique para entrar!");
                }
            }
            else {
                itemBuilder.setName("§cMina " + ChatColor.stripColor(rankModel.getFancyName()
                        .replace("Rank ", "")));
                itemBuilder.
                        lore("§7Clique para ir at\u00e9 \u00e0", "§7\u00e1rea de minera\u00e7\u00e3o do", "§7rank " + ChatColor.stripColor(rankModel.getFancyName().replace("Rank ", "")), "", "§cSem acesso.");
            }
            int finalIndex = index;
            IntelligentItem intelligentMina = IntelligentItem.of(itemBuilder.build(), event -> {
                if (userModel.getRank().getRankPosition() >= finalIndex || player.isOp()) {
                    if (!this.isInventoryEmpty(player)) {
                        player.sendMessage("§cEsvazie o invent\u00e1rio!");
                        return;
                    }
                    final MineModel mineModel = MiningPlugin.getInstance().getMineCache().getByName("mina" + (finalIndex + 1));
                    if (mineModel == null) {
                        player.sendMessage("§cA localiza\u00e7\u00e3o de spawn desta mina n\u00e3o est\u00e1 definida.");
                        return;
                    }
                    player.closeInventory();
                    player.teleport(mineModel.getSpawnLocation());
                    player.getInventory().addItem(new ItemStack[] { MiningPlugin.getInstance().getEnchantmentTool().provideItem(userModel) });
                    Constants.publicMine.add(player);
                    ScoreAPI.setScoreBoard(player, "MINA");
                    player.playSound(player.getLocation(), Sound.LEVEL_UP, 10.0f, 10.0f);
                }
                else {
                    player.sendMessage("§cVoc\u00ea n\u00e3o tem acesso para entrar nesta mina.");
                }
            });
            contents.set(slots[index], intelligentMina);
            index++;
        }

    }


    private ItemStack getByRank(final int position) {
        switch (position) {
            case 0: {
                ItemStack itemStack = new ItemStack(Material.STONE);
                return itemStack;
            }
            case 1: {
                ItemStack itemStack = new ItemStack(Material.COAL);
                return itemStack;
            }
            case 2: {
                ItemStack itemStack = new ItemStack(Material.REDSTONE);
                return itemStack;
            }
            case 3: {
                ItemStack itemStack = new ItemStack(Material.IRON_INGOT);
                return itemStack;
            }
            case 4: {
                ItemStack itemStack = new ItemStack(Material.GOLD_INGOT);
                return itemStack;
            }
            case 5: {
                ItemStack itemStack = new ItemStack(Material.DIAMOND);
                return itemStack;
            }
            case 6: {
                ItemStack itemStack = new ItemStack(Material.EMERALD);
                return itemStack;
            }
            case 7: {
                ItemStack itemStack = new ItemStack(Material.NETHER_STAR);
                return itemStack;
            }
            default: {
                return new ItemStack(Material.STONE);
            }
        }
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

