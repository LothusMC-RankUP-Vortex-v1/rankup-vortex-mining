package br.net.rankup.mining.listeners;

import br.net.rankup.booster.api.BoosterAPI;
import br.net.rankup.booster.models.Account;
import br.net.rankup.booster.type.BoosterType;
import br.net.rankup.mineprivate.models.mine.Mine;
import br.net.rankup.mineprivate.utils.MineUtils;
import br.net.rankup.mining.Constants;
import br.net.rankup.mining.MiningPlugin;
import br.net.rankup.mining.cache.MineCache;
import br.net.rankup.mining.cache.UserCache;
import br.net.rankup.mining.drops.controller.BonusController;
import br.net.rankup.mining.drops.manager.DropManager;
import br.net.rankup.mining.drops.models.bonus.BonusModel;
import br.net.rankup.mining.drops.models.drop.DropModel;
import br.net.rankup.mining.enchantment.EnchantmentType;
import br.net.rankup.mining.enchantment.tool.EnchantmentTool;
import br.net.rankup.mining.manager.ClassesManager;
import br.net.rankup.mining.misc.BukkitUtils;
import br.net.rankup.mining.model.Classes;
import br.net.rankup.mining.model.mine.MineModel;
import br.net.rankup.mining.model.reward.RewardModel;
import br.net.rankup.mining.model.user.UserModel;
import br.net.rankup.mining.utils.Actionbar;
import br.net.rankup.mining.utils.CurrencyUtils;
import br.net.rankup.mining.utils.Toolchain;
import org.bukkit.command.CommandSender;
import org.bukkit.event.block.*;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.block.*;
import org.bukkit.inventory.*;
import org.bukkit.event.*;

public class MineListeners implements Listener
{
    private final MiningPlugin plugin;
    private final UserCache userCache;
    private final MineCache mineCache;
    private final EnchantmentTool enchantmentTool;
    
    public MineListeners() {
        this.plugin = MiningPlugin.getInstance();
        this.userCache = this.plugin.getUserCache();
        this.mineCache = this.plugin.getMineCache();
        this.enchantmentTool = this.plugin.getEnchantmentTool();
        this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
    }
    
    @EventHandler
    public void onBlockBreak(final BlockBreakEvent event) {
        final Player player = event.getPlayer();
        final Block block = event.getBlock();
        if (block.getWorld().getName().equals("mines")) {
            final MineModel mineModel = this.mineCache.getByLocation(block.getLocation());
            if (mineModel == null) {
                if(!player.hasPermission("group.staff")) {
                    event.setCancelled(true);
                }
                return;
            }
            final ItemStack itemInHand = player.getItemInHand();
            if (itemInHand == null) {
                return;
            }
            if (itemInHand.getType() != Material.DIAMOND_PICKAXE) {
                return;
            }
            final UserModel userModel = this.userCache.getById(player.getUniqueId());
            if (userModel == null) {
                return;
            }
            final String dropType = block.getTypeId() + ":" + block.getData();
            double coins = 1.0;
            double rubis = 1.0;
            double count = 1.0;
            double drops = 1.0;

            DropModel dropModel = DropManager.getDrops().get(dropType);
            if(dropModel == null) {
                BukkitUtils.sendMessage(player, "&cO drop dessa mina não está configurado.");
                event.setCancelled(true);
                return;
            } else {
                rubis = dropModel.getPriceRubis();
                coins = dropModel.getPrice();
            }

            for (final EnchantmentType type : EnchantmentType.values()) {
                if (userModel.getEnchantmentLevel(type) >= 1) {
                    final double random = Math.random() * 100.0;
                    final double chance = type.getAttributes().getChance() * userModel.getEnchantmentLevel(type);
                    if (random < chance) {
                        count += this.plugin.getEnchantmentBehavior().process(type, player, userModel, mineModel, block);
                    }
                }
            }
            coins *= count;
            rubis *= count;
            if (userModel.getEnchantmentLevel(EnchantmentType.FORTUNE) > 0) {
                count *= userModel.getEnchantmentLevel(EnchantmentType.FORTUNE);
            }
            if (userModel.getEnchantmentLevel(EnchantmentType.JEWELLER) > 0) {
                rubis *= userModel.getEnchantmentLevel(EnchantmentType.JEWELLER) + 1;
            }

            String actionBar = "§aVocê recebeu §2$§a{coins} coins §f§l| §4✦§c {rubis} rubís";

            Account account = BoosterAPI.getAccount(player);
            if(account != null) {
                if(account.getType().equals(BoosterType.RUBIS)) {
                    actionBar = (actionBar+" §f§l| §cBooster de Rúbis §7("+account.getBonus()+"x)");
                    rubis *= account.getBonus();
                }
                if(account.getType().equals(BoosterType.COINS)) {
                    actionBar = (actionBar+" §f§l| §aBooster de Coins §7("+account.getBonus()+"x)");
                    coins *= account.getBonus();
                }
                if(account.getType().equals(BoosterType.DROPS)) {
                    actionBar = (actionBar+" §f§l| §9Booster de Drops §7("+account.getBonus()+"x)");
                    drops = account.getBonus();
                }
            }

            if(ClassesManager.getClasses().get(userModel.getPlayerClass()) != null) {
                Classes classes = ClassesManager.getClasses().get(userModel.getPlayerClass());
                if(classes.getMultiplier()>1) {
                    rubis *= classes.getMultiplier();
                    coins *= classes.getMultiplier();
                    actionBar = (actionBar + " §f§l| " + classes.getFriendlyName() + " §7(" + classes.getMultiplier() + "x)");
                }
            }

            BonusController bonusController = MiningPlugin.getInstance().getBonusController();

            if(!bonusController.getBonusMessage(player).equalsIgnoreCase("")) {
                coins = MiningPlugin.getInstance().getBonusController().applyGroupBonus(player, coins*dropModel.getPrice());
                actionBar = (actionBar +bonusController.getBonusMessage(player));
            }

            coins = this.applyPrestigeBonus(player, drops*coins);


            for (final RewardModel reward : Constants.minesRewards) {
                final double random2 = Math.random() * 100.0;
                final double chance2 = reward.getChance();
                if (random2 < chance2) {
                    Bukkit.dispatchCommand((CommandSender)Bukkit.getConsoleSender(), reward.getCommand().replace("<player>", player.getName()));
                    //BukkitUtils.sendActionBar(player,"§b§lRECOMPENSA §eVocê recebeu " + reward.getFriendlyName().replace("&", "§"));
                    player.playSound(player.getLocation(), Sound.LEVEL_UP, 5.0f, 5.0f);
                }
            }


            CurrencyUtils.addRubis(player, drops*rubis);
            MiningPlugin.getInstance().getEconomy().depositPlayer(player.getName(), drops*coins);
            Actionbar.sendActionBar(player, actionBar.replace("{rubis}", Toolchain.format(drops*rubis))
                    .replace("{coins}", Toolchain.format(drops*coins)));
            userModel.setCount(userModel.getCount() + count);
            this.enchantmentTool.updateDisplayName(itemInHand, userModel);
            block.setType(Material.AIR);
        } else if (block.getWorld().getName().equals("plotworld") && block.getType() == Material.QUARTZ_BLOCK) {
            final Mine mine = MineUtils.getMineByLocation(block.getLocation());
            if (mine == null) {
                return;
            }
            for (final RewardModel reward : Constants.privateMineRewards) {
                final double random2 = Math.random() * 100.0;
                final double chance2 = reward.getChance();
                if (random2 < chance2) {
                    Bukkit.dispatchCommand((CommandSender)Bukkit.getConsoleSender(), reward.getCommand().replace("<player>", player.getName()));
                    //BukkitUtils.sendActionBar(player,"§b§lMINA PRIVADA §eVocê recebeu " + reward.getFriendlyName().replace("&", "§"));
                    player.playSound(player.getLocation(), Sound.LEVEL_UP, 5.0f, 5.0f);
                }
            }
            mine.reduceBlocksLeft();
            block.setType(Material.AIR);
            player.playEffect(block.getLocation(), Effect.CLOUD, (Object)null);
        }
    }

    public double applyPrestigeBonus(final Player player, final double toApply) {
        final UserModel userModel = this.plugin.getUserCache().getById(player.getUniqueId());
        if (userModel != null) {
            return toApply + toApply / 100.0 * userModel.getPrestigeBonus();
        }
        return toApply;
    }
}
