package br.net.rankup.mining.cache;

import br.net.rankup.mining.MiningPlugin;
import br.net.rankup.mining.model.user.UserModel;
import br.net.rankup.mining.utils.Cache;

import java.util.*;

public class UserCache extends Cache<UserModel>
{
    private final MiningPlugin plugin;
    
    public UserCache(final MiningPlugin plugin) {
        this.plugin = plugin;
    }
    
    public UserModel getById(final UUID id) {
        return this.get(userModel -> userModel.getId().equals(id));
    }
    
    public UserModel getByName(final String name) {
        return this.get(userModel -> userModel.getName().equals(name));
    }
    
    public MiningPlugin getPlugin() {
        return this.plugin;
    }
}
