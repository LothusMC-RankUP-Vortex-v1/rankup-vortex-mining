package br.net.rankup.mining.model.editor;

import br.net.rankup.mining.utils.Cuboid;
import org.bukkit.*;

public class EditorSelectionModel
{
    private Location min;
    private Location max;
    
    private boolean isCompleted() {
        return this.min != null && this.max != null;
    }
    
    public boolean isValid() {
        return this.isCompleted() && this.min.getWorld().equals(this.max.getWorld());
    }
    
    public Cuboid asCuboid() {
        if (!this.isCompleted()) {
            return null;
        }
        return new Cuboid(this.min, this.max);
    }
    
    public void clear() {
        this.max = null;
        this.min = null;
    }
    
    public Location getMin() {
        return this.min;
    }
    
    public Location getMax() {
        return this.max;
    }
    
    public void setMin(final Location min) {
        this.min = min;
    }
    
    public void setMax(final Location max) {
        this.max = max;
    }
}
