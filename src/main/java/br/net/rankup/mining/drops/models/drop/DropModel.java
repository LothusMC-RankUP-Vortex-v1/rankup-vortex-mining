package br.net.rankup.mining.drops.models.drop;

import br.net.rankup.mining.MiningPlugin;
import br.net.rankup.mining.model.user.UserModel;
import org.bukkit.entity.*;

public class DropModel
{
    private final MiningPlugin plugin;
    private double price;
    private double priceRubis;

    
    public DropModel(Double price, Double priceRubis) {
        this.plugin = MiningPlugin.getInstance();
        this.price = price;
        this.priceRubis = priceRubis;
    }
    
    public MiningPlugin getPlugin() {
        return this.plugin;
    }
    
    public double getPrice() {
        return this.price;
    }

    public Double getPriceRubis() {
        return this.priceRubis;
    }
    public void setPrice(final double amount) {
        this.price = amount;
    }
}
