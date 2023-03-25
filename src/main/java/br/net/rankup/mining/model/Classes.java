package br.net.rankup.mining.model;

import br.net.rankup.mining.MiningPlugin;
import br.net.rankup.mining.misc.BukkitUtils;
import br.net.rankup.mining.misc.ItemBuilder;
import br.net.rankup.mining.model.user.UserModel;
import br.net.rankup.mining.utils.Toolchain;
import br.net.rankup.rubis.managers.RubisManager;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

@Getter
@Setter
@AllArgsConstructor
public class Classes {

    private String friendlyName;
    private Color armorColor;
    private Double multiplier;
    private Double priceCoins;
    private Double priceRubis;
    private boolean isDefault;
    private Double indentifier;

    public ItemStack getItemStack(UserModel userModel, Classes classes) {
        String loreBuy = "";
        int has = 0;

        if(userModel.getPlayerClass()>classes.getIndentifier()) {
            loreBuy = "§cVocê já tem essa classe.";
            has = 1;
        }
        if(userModel.getPlayerClass().equals(classes.getIndentifier())) {
            has = 2;
            loreBuy = "§eVocê está nessa classe.";
        }
        if(userModel.getPlayerClass()<classes.getIndentifier()) {
            loreBuy = has(Bukkit.getPlayer(userModel.getName())) ? "§aClique para comprar." : "§cVocê não pode comprar essa classe.";
        }

        ItemBuilder itemBuilder = new ItemBuilder(Material.LEATHER_CHESTPLATE, 1, 0)
                .setName(friendlyName)
                .setLeatherArmorColor(armorColor)
                .addLoreLine("")
                .addLoreLine(" §fVantagem: §7Multiplicar os coins e rubis ganhos.")
                .addLoreLine(" §fMultiplicador: §7"+multiplier+"x.")
                .addLoreLine("")
                .addLoreLine(" §fValor para evoluir:")
                .addLoreLine("  §fCoins: §2$§a"+Toolchain.format(priceCoins))
                .addLoreLine("  §fRúbis: §4✦§c"+Toolchain.format(priceRubis))
                .addLoreLine("")
                .addLoreLine(loreBuy);


        if(has == 1) {
            itemBuilder = new ItemBuilder(Material.LEATHER_CHESTPLATE, 1, 0)
                    .setName(friendlyName)
                    .addLoreLine("")
                    .addLoreLine(" §fVantagem: §7Multiplicar os coins e rubis ganhos.")
                    .addLoreLine(" §fMultiplicador: §7"+multiplier+"x.")
                    .addLoreLine("")
                    .addLoreLine(" §fValor para evoluir:")
                    .addLoreLine("  §fCoins: §2$§a"+Toolchain.format(priceCoins))
                    .addLoreLine("  §fRúbis: §4✦§c"+Toolchain.format(priceRubis))
                    .addLoreLine("")
                    .addLoreLine(loreBuy)
                    .setLeatherArmorColor(armorColor);
        }

        if(has == 2) {
            itemBuilder = new ItemBuilder(Material.LEATHER_CHESTPLATE, 1, 0)
                    .setName(friendlyName)
                    .addLoreLine("")
                    .addLoreLine(" §fVantagem: §7Multiplicar os coins e rubis ganhos.")
                    .addLoreLine(" §fMultiplicador: §7"+multiplier+"x.")
                    .addLoreLine("")
                    .addLoreLine(loreBuy)
                    .addEnchant(Enchantment.DURABILITY, 1)
                    .addItemFlag(ItemFlag.HIDE_ENCHANTS)
                    .setLeatherArmorColor(armorColor);
        }

        if(isDefault) {
            itemBuilder = new ItemBuilder(Material.LEATHER_CHESTPLATE, 1, 0)
                    .setName(friendlyName)
                    .addLoreLine("")
                    .addLoreLine("§fClasse padrão do servidor e")
                    .addLoreLine("§fsem nenhúm bônus.")
                    .addLoreLine("")
                    .addLoreLine(loreBuy)
                    .setLeatherArmorColor(armorColor);
        }

        return itemBuilder.build();


    }

    public boolean has(Player player) {
        double rubis = 0;
        if(RubisManager.getUsers().containsKey(player.getName())) {
            rubis = RubisManager.getUsers().get(player.getName()).getAmount();
        } else {
            return false;
        }
        boolean has = MiningPlugin.getInstance().getEconomy().getBalance(player) >= this.priceCoins && rubis >= this.priceRubis;
        return has;
    }

}
