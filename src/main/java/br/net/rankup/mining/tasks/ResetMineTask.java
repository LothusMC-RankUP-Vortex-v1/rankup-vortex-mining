package br.net.rankup.mining.tasks;

import br.net.rankup.mining.MiningPlugin;
import br.net.rankup.mining.cache.MineCache;
import br.net.rankup.mining.model.mine.MineModel;
import org.bukkit.scheduler.*;

public class ResetMineTask extends BukkitRunnable
{
    private final MiningPlugin plugin;
    private final MineCache mineCache;
    
    public ResetMineTask(final MiningPlugin plugin) {
        this.plugin = plugin;
        this.mineCache = plugin.getMineCache();
    }
    
    public void run() {
        for (final MineModel mineModel : this.mineCache.getElements()) {
            if (System.currentTimeMillis() >= mineModel.getLastReset() + mineModel.getResetTime()) {
                mineModel.reset(this.plugin.getBlockHandler());
            }
        }
    }
    
    public MiningPlugin getPlugin() {
        return this.plugin;
    }
    
    public MineCache getMineCache() {
        return this.mineCache;
    }
}
