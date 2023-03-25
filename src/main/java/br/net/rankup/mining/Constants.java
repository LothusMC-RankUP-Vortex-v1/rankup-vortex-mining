package br.net.rankup.mining;

import br.net.rankup.mining.model.reward.RewardModel;
import org.bukkit.entity.*;
import java.util.*;

public class Constants
{
    public static List<Player> publicMine;
    public static List<Player> vipMine;
    public static List<RewardModel> privateMineRewards;
    public static List<RewardModel> minesRewards;

    public static void loadPrivateMineRewards(final MiningPlugin plugin) {
        privateMineRewards = new ArrayList<>();
        for (final String key : plugin.getConfig().getConfigurationSection("private-mine-rewards").getKeys(false)) {
            final String prefix = "private-mine-rewards." + key + ".";
            final String friendlyName = plugin.getConfig().getString(prefix + "friendly-name");
            final String command = plugin.getConfig().getString(prefix + "command");
            final double chance = plugin.getConfig().getDouble(prefix + "chance");
            Constants.privateMineRewards.add(new RewardModel(friendlyName, command, chance));
        }
    }

    public static void loadMineRewards(final MiningPlugin plugin) {
        minesRewards = new ArrayList<>();
        for (final String key : plugin.getConfig().getConfigurationSection("mine-rewards").getKeys(false)) {
            final String prefix = "mine-rewards." + key + ".";
            final String friendlyName = plugin.getConfig().getString(prefix + "friendly-name");
            final String command = plugin.getConfig().getString(prefix + "command");
            final double chance = plugin.getConfig().getDouble(prefix + "chance");
            Constants.minesRewards.add(new RewardModel(friendlyName, command, chance));
        }
    }

    static {
        Constants.publicMine = new ArrayList<Player>();
        Constants.vipMine = new ArrayList<Player>();
    }
}
