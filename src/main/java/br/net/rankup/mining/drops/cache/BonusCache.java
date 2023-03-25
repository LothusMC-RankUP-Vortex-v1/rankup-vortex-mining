package br.net.rankup.mining.drops.cache;

import br.net.rankup.mining.MiningPlugin;
import br.net.rankup.mining.drops.Cache;
import br.net.rankup.mining.drops.adapter.BonusAdapter;
import br.net.rankup.mining.drops.models.bonus.BonusModel;

public class BonusCache extends Cache<BonusModel>
{
    private final MiningPlugin plugin;
    
    public BonusCache(final MiningPlugin plugin) {
        this.plugin = plugin;
        final BonusAdapter adapter = new BonusAdapter();
        for (final String key : plugin.getConfig().getConfigurationSection("bonus").getKeys(false)) {
            this.addElement(adapter.read(plugin.getConfig().getConfigurationSection("bonus." + key)));
        }
    }
    
    public BonusModel getByPermission(final String permission) {
        return this.get(bonusModel -> bonusModel.getPermission().equals(permission));
    }
    
    public MiningPlugin getPlugin() {
        return this.plugin;
    }
}
