package br.net.rankup.mining.cache;

import br.net.rankup.mining.MiningPlugin;
import br.net.rankup.mining.model.mine.MineModel;
import br.net.rankup.mining.utils.Cache;
import org.bukkit.*;

public class MineCache extends Cache<MineModel>
{
    private final MiningPlugin plugin;
    
    public MineCache(final MiningPlugin plugin) {
        this.plugin = plugin;
    }
    
    public MineModel getByLocation(final Location location) {
        return this.get(mineModel -> mineModel.getCuboid().contains(location));
    }
    
    public MineModel getByName(final String name) {
        return this.get(mineModel -> mineModel.getName().equals(name));
    }
    
    public MiningPlugin getPlugin() {
        return this.plugin;
    }
}
