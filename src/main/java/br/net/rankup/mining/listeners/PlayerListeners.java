package br.net.rankup.mining.listeners;

import br.net.rankup.booster.manager.BoosterManager;
import br.net.rankup.core.CorePlugin;
import br.net.rankup.core.systems.scoreboard.api.ScoreAPI;
import br.net.rankup.core.systems.scoreboard.enums.ScoreBoardType;
import br.net.rankup.core.systems.scoreboard.manager.ScoreManager;
import br.net.rankup.mining.MiningPlugin;
import br.net.rankup.mining.cache.UserCache;
import br.net.rankup.mining.inventory.EnchantBuylnventory;
import br.net.rankup.mining.inventory.EnchantsInventory;
import br.net.rankup.mining.misc.InventoryUtils;
import br.net.rankup.mining.model.user.UserBuy;
import br.net.rankup.mining.model.user.UserModel;
import br.net.rankup.mining.Constants;
import io.github.rysefoxx.inventory.plugin.pagination.RyseInventory;
import net.royawesome.jlibnoise.module.combiner.Min;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;

public class PlayerListeners implements Listener {
   private final MiningPlugin plugin = MiningPlugin.getInstance();
   private final UserCache userCache;

   public PlayerListeners(MiningPlugin miningPlugin) {
      miningPlugin.getServer().getPluginManager().registerEvents(this, miningPlugin);
      this.userCache = this.plugin.getUserCache();
   }


   @EventHandler
   public void onJoin(PlayerJoinEvent event) {
      Player player = event.getPlayer();
         Constants.vipMine.remove(player);
         Constants.publicMine.remove(player);
   }

   @EventHandler
   public void onPlayerChat(AsyncPlayerChatEvent event) {
      Player player = event.getPlayer();
      int amount = 0;
      if (EnchantBuylnventory.MAP.containsKey(player.getUniqueId())) {
         event.setCancelled(true);
         String message = event.getMessage();

         if (message.equalsIgnoreCase("cancelar")) {
            player.sendMessage("§aOperação cancelada com êxito.");
            EnchantBuylnventory.MAP.remove(player.getUniqueId());
            return;
         }

         try {
            amount = Integer.parseInt(message);
         } catch (Exception var8) {
            player.sendMessage("§cInsira um valor válido.");
            EnchantBuylnventory.MAP.remove(player.getUniqueId());
            return;
         }

      if (!EnchantBuylnventory.verifyItem(player)) {
         player.sendMessage("§cVocê não tem uma picareta na mão.");
         EnchantBuylnventory.MAP.remove(player.getUniqueId());
         return;
      } else {
         UserBuy userBuy = EnchantBuylnventory.MAP.get(player.getUniqueId());
         if (EnchantBuylnventory.addEnchantment(userBuy.getUserModel(), userBuy.getType(), amount)) {
            if (!InventoryUtils.getList().contains(player.getName())) {
               RyseInventory inventory = new EnchantsInventory(MiningPlugin.getInstance().getConfig()).build();
               inventory.open(player);
               InventoryUtils.addDelay(player);
            }
         }
         EnchantBuylnventory.MAP.remove(player.getUniqueId());
      }
   }
}

   @EventHandler
   public void onPlayerChangedWorld(PlayerChangedWorldEvent event) {
      Player player = event.getPlayer();
      World from = event.getFrom();
      if (from.getName().equals("mines")) {
         Constants.vipMine.remove(player);
         Constants.publicMine.remove(player);
         ScoreAPI.setScoreBoard(player, "GLOBAL");
         UserModel userModel = this.userCache.getById(event.getPlayer().getUniqueId());
         if (userModel != null) {
            userModel.removeIfContains(player.getInventory());
         }
      }

   }


   @EventHandler
   public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
      Player player = event.getPlayer();
      if (player.getWorld().getName().contains("mines") && !player.isOp()) {
         String message = event.getMessage();
         if (message.startsWith("/g ") || message.startsWith("/tell ") || message.startsWith("/r ") || message.equalsIgnoreCase("/fly") || message.equalsIgnoreCase("/voar") || message.equalsIgnoreCase("/spawn") || message.equalsIgnoreCase("/drops") || message.equalsIgnoreCase("/vender") || message.equalsIgnoreCase("/rankup") || message.equalsIgnoreCase("/luz") || message.equalsIgnoreCase("/on") || message.equalsIgnoreCase("/off")) {
            return;
         }

         player.sendMessage("§cVocê não pode executar comandos na mina. Digite /spawn para sair.");
         event.setCancelled(true);
      }

   }

   @EventHandler
   public void onPlayerInteract(PlayerInteractEvent event) {
      Player player = event.getPlayer();
      UserModel userModel = this.userCache.getById(player.getUniqueId());
      if (userModel != null) {
         if (player.getWorld().getName().equals("mines") && (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR)) {
            ItemStack itemInHand = player.getItemInHand();
            if (itemInHand == null || itemInHand.getType() != Material.DIAMOND_PICKAXE) {
               return;
            }

            if (!itemInHand.hasItemMeta()) {
               return;
            }

            if (!itemInHand.getItemMeta().hasDisplayName()) {
               return;
            }

            if (itemInHand.getItemMeta().getDisplayName().contains("§bPicareta de Diamante")) {
               if(!InventoryUtils.getList().contains(player.getName())) {
                  RyseInventory inventory = new EnchantsInventory(MiningPlugin.getInstance().getConfig()).build();
                  inventory.open(player);
                  InventoryUtils.addDelay(player);
               }
               player.playSound(player.getLocation(), Sound.LEVEL_UP, 1.0f, 1.0f);
               player.playSound(player.getLocation(), Sound.NOTE_BASS, 10.0F, 10.0F);
               event.setCancelled(true);
            }
         }

      }
   }

   @EventHandler
   public void onPlayerDropItem(PlayerDropItemEvent event) {
      Player player = event.getPlayer();
      if (player.getWorld().getName().equals("mines") && !player.isOp()) {
         player.sendMessage("§cVocê não pode dropar itens na mina.");
         event.setCancelled(true);
      }

   }

   @EventHandler
   public void onPlayerPickupItem(PlayerPickupItemEvent event) {
      Player player = event.getPlayer();
      if (player.getWorld().getName().equals("mines") && !player.isOp()) {
         event.setCancelled(true);
      }

   }
}