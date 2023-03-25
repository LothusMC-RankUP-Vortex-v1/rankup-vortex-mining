package br.net.rankup.mining.handler;

import br.net.rankup.mining.MiningPlugin;

public class BlockHandler
{
    private final MiningPlugin plugin;
    
    public BlockHandler(final MiningPlugin plugin) {
        this.plugin = plugin;
    }
    
    public MiningPlugin getPlugin() {
        return this.plugin;
    }
}
