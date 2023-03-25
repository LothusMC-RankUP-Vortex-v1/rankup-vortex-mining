package br.net.rankup.mining.hook;

import br.net.rankup.booster.api.BoosterAPI;
import br.net.rankup.booster.models.Account;
import br.net.rankup.booster.type.BoosterType;
import br.net.rankup.mining.Constants;
import br.net.rankup.mining.MiningPlugin;
import br.net.rankup.mining.manager.ClassesManager;
import br.net.rankup.mining.model.Classes;
import br.net.rankup.ranks.manager.UserManager;
import br.net.rankup.ranks.model.UserModel;
import br.net.rankup.rubis.managers.RubisManager;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.*;

import java.text.DecimalFormat;

public class PlaceHolderHook extends PlaceholderExpansion {

    private static MiningPlugin plugin;

    public PlaceHolderHook(MiningPlugin plugin) {
        this.plugin = plugin;
    }

    public String getName() {
        return this.plugin.getName();
    }

    public String getIdentifier() {
        return "mine";
    }

    public String getAuthor() {
        return "zRomaGod_";
    }

    public String getVersion() {
        return this.plugin.getDescription().getVersion();
    }


    DecimalFormat decimalFormat = new DecimalFormat("#.#");
    @Override
    public String onPlaceholderRequest(Player player, String identifier) {
        if (identifier.equalsIgnoreCase("publicmine_players")) {
            if (player == null) {
                return "";
            }
            return String.valueOf(Constants.publicMine.size());
        }
        if (identifier.equalsIgnoreCase("vipmine_players")) {
            if (player == null) {
                return "";
            }
            return String.valueOf(Constants.vipMine.size());
        }
        if (identifier.equalsIgnoreCase("rank")) {
            return MiningPlugin.getInstance().getBonusController().getRank(player);
        }
        if (identifier.equalsIgnoreCase("booster")) {
            Account account = BoosterAPI.getAccount(player);
            if(account == null) {
                return "§7Sem booster.";
            }
            String boosterName = "§7Sem booster.";
            if(account.getType().equals(BoosterType.COINS)) {
                boosterName = "§aCoins §8(" + account.getBonus() + "x)";
            }
            if(account.getType().equals(BoosterType.DROPS)) {
                boosterName = "§9Drops §8(" + account.getBonus() + "x)";
            }
            if(account.getType().equals(BoosterType.RUBIS)) {
                boosterName = "§cRúbis §8(" + account.getBonus() + "x)";
            }
            return boosterName;
        }
        if (identifier.equalsIgnoreCase("classe_percent")) {
            br.net.rankup.mining.model.user.UserModel userModel = MiningPlugin.getInstance().getUserCache().getByName(player.getName());
            if (player == null) {
                return "§7Carregando...";
            }
            Classes classe = ClassesManager.getClasses().get(userModel.getPlayerClass());
            if(classe == null) {
                return "§7Carregando...";
            }
            Classes nextClasse = ClassesManager.getClasses().get(classe.getIndentifier()+1);
            if(nextClasse == null) {
                return "§cUltima classe.";
            }
            return this.progressBar(player, nextClasse.getPriceCoins(), nextClasse.getPriceRubis()) + " §7"
                    + this.decimalFormat.format(this.percentage(player, nextClasse.getPriceCoins()
                    , nextClasse.getPriceRubis()))+"%";
        }
        if (identifier.equalsIgnoreCase("classe_fancyname")) {
            br.net.rankup.mining.model.user.UserModel userModel = MiningPlugin.getInstance().getUserCache().getByName(player.getName());
            if (userModel == null) {
                return "§7Carregando...";
            }
            Classes classe = ClassesManager.getClasses().get(userModel.getPlayerClass());
            if(classe == null) {
                return "§7Carregando...";
            }
            return classe.getFriendlyName();
        }
        return "Placeholder inválida";
    }


    private double percentage(final Player player, double coins, double rubisNeed) {
        double rubis = 0;
        if(RubisManager.getUsers().containsKey(player.getName())) {
            rubis = RubisManager.getUsers().get(player.getName()).getAmount();
        }
        final double coinsPrice = coins + coins;
        final double rubisPrice = rubisNeed + rubisNeed;
        return Math.min(this.plugin.getEconomy().getBalance((OfflinePlayer)player) * 50.0 / coinsPrice, 50.0) + Math.min(rubis * 50.0 / rubisPrice, 50.0);
    }

    private String progressBar(final Player player, double coins, double rubisNeed) {
        final StringBuilder bar = new StringBuilder("§a");
        final int divide = (int)(this.percentage(player, coins, rubisNeed) / 10.0);
        for (int i = 0; i < 10; ++i) {
            bar.append((i == divide) ? "§8" : "").append("▎");
        }
        return bar.toString();
    }
}
