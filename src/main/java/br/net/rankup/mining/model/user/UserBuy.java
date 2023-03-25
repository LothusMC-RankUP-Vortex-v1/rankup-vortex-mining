package br.net.rankup.mining.model.user;

import br.net.rankup.mining.enchantment.EnchantmentType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserBuy
{
    private final UserModel userModel;
    private final EnchantmentType type;
    private String message;

    public UserBuy(UserModel userModel, EnchantmentType type) {
        this.userModel = userModel;
        this.type = type;
    }
}
