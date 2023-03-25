package br.net.rankup.mining.model.mine;

import br.net.rankup.mining.handler.BlockHandler;
import br.net.rankup.mining.utils.Cuboid;
import br.net.rankup.mining.utils.blocks.BlockUtil;
import org.bukkit.material.*;
import org.bukkit.entity.*;
import org.bukkit.*;

public class MineModel
{
    private String id;
    private String name;
    private Location spawnLocation;
    private Cuboid cuboid;
    private long resetTime;
    private long lastReset;
    private MaterialData compound;
    
    public boolean isEmpty() {
        return this.cuboid.containsOnly(0);
    }
    
    public void reset(final BlockHandler handler) {
        BlockUtil.resetMine(this);
        for (final Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (this.cuboid.contains(onlinePlayer.getLocation())) {
                onlinePlayer.teleport(this.spawnLocation);
                onlinePlayer.playSound(onlinePlayer.getLocation(), Sound.WITHER_SPAWN, 5.0f, 5.0f);
                onlinePlayer.sendMessage("§aMina resetada!");
            }
        }
        this.lastReset = System.currentTimeMillis();
    }
    
    MineModel(final String id, final String name, final Location spawnLocation, final Cuboid cuboid, final long resetTime, final long lastReset, final MaterialData compound) {
        this.id = id;
        this.name = name;
        this.spawnLocation = spawnLocation;
        this.cuboid = cuboid;
        this.resetTime = resetTime;
        this.lastReset = lastReset;
        this.compound = compound;
    }
    
    public static MineModelBuilder builder() {
        return new MineModelBuilder();
    }
    
    public String getId() {
        return this.id;
    }
    
    public String getName() {
        return this.name;
    }
    
    public Location getSpawnLocation() {
        return this.spawnLocation;
    }
    
    public Cuboid getCuboid() {
        return this.cuboid;
    }
    
    public long getResetTime() {
        return this.resetTime;
    }
    
    public long getLastReset() {
        return this.lastReset;
    }
    
    public MaterialData getCompound() {
        return this.compound;
    }
    
    public void setId(final String id) {
        this.id = id;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public void setSpawnLocation(final Location spawnLocation) {
        this.spawnLocation = spawnLocation;
    }
    
    public void setCuboid(final Cuboid cuboid) {
        this.cuboid = cuboid;
    }
    
    public void setResetTime(final long resetTime) {
        this.resetTime = resetTime;
    }
    
    public void setLastReset(final long lastReset) {
        this.lastReset = lastReset;
    }
    
    public void setCompound(final MaterialData compound) {
        this.compound = compound;
    }
    
    public static class MineModelBuilder
    {
        private String id;
        private String name;
        private Location spawnLocation;
        private Cuboid cuboid;
        private long resetTime;
        private long lastReset;
        private MaterialData compound;
        
        MineModelBuilder() {
        }
        
        public MineModelBuilder id(final String id) {
            this.id = id;
            return this;
        }
        
        public MineModelBuilder name(final String name) {
            this.name = name;
            return this;
        }
        
        public MineModelBuilder spawnLocation(final Location spawnLocation) {
            this.spawnLocation = spawnLocation;
            return this;
        }
        
        public MineModelBuilder cuboid(final Cuboid cuboid) {
            this.cuboid = cuboid;
            return this;
        }
        
        public MineModelBuilder resetTime(final long resetTime) {
            this.resetTime = resetTime;
            return this;
        }
        
        public MineModelBuilder lastReset(final long lastReset) {
            this.lastReset = lastReset;
            return this;
        }
        
        public MineModelBuilder compound(final MaterialData compound) {
            this.compound = compound;
            return this;
        }
        
        public MineModel build() {
            return new MineModel(this.id, this.name, this.spawnLocation, this.cuboid, this.resetTime, this.lastReset, this.compound);
        }

    }
}
