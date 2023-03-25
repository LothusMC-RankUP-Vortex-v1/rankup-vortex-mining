package br.net.rankup.mining.manager;

import br.net.rankup.mining.MiningPlugin;
import br.net.rankup.mining.misc.BukkitUtils;
import br.net.rankup.mining.model.Classes;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;

public class ClassesManager {

    private static HashMap<Double, Classes> classes;

    public void loadAll() {
        int amount = 0;
        classes = new HashMap<>();
        for (final String name : MiningPlugin.getConfiguration().getConfigurationSection("classes").getKeys(false)) {
            final ConfigurationSection section = MiningPlugin.getConfiguration().getConfigurationSection("classes." + name);
            Bukkit.broadcastMessage(""+name);
            String friendlyName = section.getString("friendly-name").replace("&", "§");
            Color armorColor = getColor(section.getString("armor-color"));
            Double multiplier = section.getDouble("bonus.multiplier");
            Double priceCoins = section.getDouble("price.coins");
            Double priceRubis = section.getDouble("price.rubis");
            boolean isDeafult = section.getBoolean("default");
            Double indentifier = section.getDouble("indentifier");

            Classes classe = new Classes(friendlyName, armorColor, multiplier, priceCoins, priceRubis, isDeafult, indentifier);
            classes.put(indentifier, classe);
            amount++;
        }
        BukkitUtils.sendMessage(Bukkit.getConsoleSender(), "&fsuccessfully loaded {int} classes ({time} ms)"
                .replace("{time}",""+(System.currentTimeMillis() - MiningPlugin.getStart()))
                .replace("{int}", amount+""));
    }


    public Color getColor(String line) {
        String[] split = line.split(",");
        return Color.fromBGR(Integer.valueOf(split[0]), Integer.valueOf(split[1]), Integer.valueOf(split[2]));
    }

    public static HashMap<Double, Classes> getClasses() {
        return classes;
    }
}
