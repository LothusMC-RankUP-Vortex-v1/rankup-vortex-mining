package br.net.rankup.mining.drops.manager;

import br.net.rankup.mining.MiningPlugin;
import br.net.rankup.mining.drops.models.drop.DropModel;
import br.net.rankup.mining.misc.BukkitUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;

public class DropManager {

    private static HashMap<String, DropModel> drops;

    public void loadAll() {
        int amount = 0;
        drops = new HashMap<>();
        for (final String name : MiningPlugin.getConfiguration().getConfigurationSection("drops").getKeys(false)) {
            final ConfigurationSection section = MiningPlugin.getConfiguration().getConfigurationSection("drops." + name);
            Bukkit.broadcastMessage(""+name);
            Double priceCoins = section.getDouble("price.coins");
            Double priceRubis = section.getDouble("price.rubis");
            String indentifier = section.getString("indentifier");

            DropModel dropModel = new DropModel(priceCoins, priceRubis);
            drops.put(indentifier, dropModel);
            amount++;
        }
        BukkitUtils.sendMessage(Bukkit.getConsoleSender(), "&fsuccessfully loaded {int} drops ({time} ms)"
                .replace("{time}",""+(System.currentTimeMillis() - MiningPlugin.getStart()))
                .replace("{int}", amount+""));
    }

    public static HashMap<String, DropModel> getDrops() {
        return drops;
    }
}
