package br.net.rankup.mining.registry;

import br.net.rankup.mining.MiningPlugin;
import br.net.rankup.mining.hook.PlaceHolderHook;
import br.net.rankup.mining.misc.BukkitUtils;
import org.bukkit.Bukkit;

public final class PlaceHolderRegistry {

    public static void init() {
        if (!MiningPlugin.getInstance().getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")){
        	BukkitUtils.sendMessage(Bukkit.getConsoleSender(), "&cPlaceholderAPI não foi encontrado no servidor.");
            return;
        }
        BukkitUtils.sendMessage(Bukkit.getConsoleSender(), "&aPalaceholder registrado com sucesso.");
        new PlaceHolderHook(MiningPlugin.getInstance()).register();
    }

}
