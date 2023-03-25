package br.net.rankup.mining.utils;

import br.net.rankup.booster.api.BoosterAPI;
import br.net.rankup.booster.manager.BoosterManager;
import br.net.rankup.booster.models.Account;
import br.net.rankup.rubis.managers.RubisManager;
import br.net.rankup.rubis.models.User;
import org.bukkit.entity.*;

public class CurrencyUtils
{

    public static double getRubis(final Player player) {
        final User user = RubisManager.getUsers().get(player.getName());
        if (user != null) {
            return user.getAmount();
        }
        return 0.0;
    }
    
    public static void addRubis(final Player player, double amount) {
        final User user = RubisManager.getUsers().get(player.getName());
        if (user != null) {
            user.setAmount(user.getAmount() + amount);
        }
    }
    
    public static void removeGems(final Player player, final double amount) {
        final User user = RubisManager.getUsers().get(player.getName());
        if (user != null) {
            user.setAmount(user.getAmount() - amount);
        }
    }
}
