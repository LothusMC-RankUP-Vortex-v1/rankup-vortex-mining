package br.net.rankup.mining.listeners;

import br.net.rankup.mining.Constants;
import br.net.rankup.mining.MiningPlugin;
import br.net.rankup.mining.enchantment.EnchantmentType;
import br.net.rankup.mining.model.user.UserModel;
import net.royawesome.jlibnoise.module.combiner.Min;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RepositoryListener implements Listener {

    public RepositoryListener(MiningPlugin miningPlugin) {
        miningPlugin.getServer().getPluginManager().registerEvents(this, miningPlugin);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if(!MiningPlugin.getInstance().getUsersRepository().exists(player.getUniqueId().toString())) {
            UserModel model = new UserModel(player.getUniqueId(), player.getName());
            model.setCount(0.0D);
            model.setPlayerClass(0.0D);
            for(EnchantmentType enchantmentType : EnchantmentType.values()) {
                model.getEnchantments().put(enchantmentType, 0);
            }
            MiningPlugin.getInstance().getUserCache().addElements(model);
            model.removeIfContains(event.getPlayer().getInventory());
            Constants.vipMine.remove(player);
            Constants.publicMine.remove(player);
        } else {
            MiningPlugin.getInstance().getUsersRepository().load(player);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if(MiningPlugin.getInstance().getUserCache().getById(player.getUniqueId()) != null) {
            UserModel userModel = MiningPlugin.getInstance().getUserCache().getById(player.getUniqueId());
            MiningPlugin.getInstance().getUsersRepository().update(userModel);
            userModel.removeIfContains(event.getPlayer().getInventory());
        }
    }

}
