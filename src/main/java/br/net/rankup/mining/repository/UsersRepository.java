package br.net.rankup.mining.repository;

import br.net.rankup.mining.MiningPlugin;
import br.net.rankup.mining.database.HikariDataBase;
import br.net.rankup.mining.enchantment.EnchantmentType;
import br.net.rankup.mining.model.user.UserModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.bukkit.entity.Player;

import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

public class UsersRepository {

    private final Gson gson = new Gson();
    private final Type type = new TypeToken<Map<EnchantmentType, Integer>>() {}.getType();

    private static final HikariDataBase hikariDataBase = MiningPlugin.getInstance().getHikariDataBase();
    public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS mining_users (" +
            "id INTEGER NOT NULL AUTO_INCREMENT, " +
            "uuid CHAR(36) NOT NULL UNIQUE, " +
            "blocks DOUBLE NOT NULL, " +
            "class DOUBLE NOT NULL, " +
            "enchantments TEXT, " +
            "PRIMARY KEY (id));";
    public static final String CLEAR_TABLE = "DELETE FROM mining_users;";
    public static final String SELECT_QUERY = "SELECT * FROM mining_users WHERE uuid = ?;";
    public static final String UPDATE_QUERY = "INSERT INTO mining_users " +
            "(uuid, blocks, class, enchantments) VALUES (?, ?, ?, ?) " +
            "ON DUPLICATE KEY UPDATE blocks=?, class=?, enchantments=?;";

    public void createTable() {
        try (final Connection connection = hikariDataBase.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(CREATE_TABLE)) {
                statement.execute();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void clearTable() {
        Runnable runnable = () -> {
            try (Connection connection = hikariDataBase.getConnection()) {
                try (PreparedStatement statement = connection.prepareStatement(CLEAR_TABLE)) {
                    statement.execute();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        };
        MiningPlugin.getInstance().getHikariDataBase().executeAsync(runnable);
    }

    public void load(Player player) {
        try (Connection connection = hikariDataBase.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(SELECT_QUERY)) {
                statement.setString(1, player.getUniqueId().toString());
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        final UserModel userModel = new UserModel(player.getUniqueId(), player.getName());
                        userModel.setCount(resultSet.getDouble("blocks"));
                        userModel.setPlayerClass(resultSet.getDouble("class"));
                        userModel.getEnchantments()
                                .putAll((Map<? extends EnchantmentType, ? extends Integer>)
                                        this.gson.fromJson(resultSet.getString("enchantments"), this.type));
                        MiningPlugin.getInstance().getUserCache().addElements(userModel);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean exists(String uuid) {
        try (Connection connection = hikariDataBase.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(SELECT_QUERY)) {
                statement.setString(1, uuid);
                try (ResultSet resultSet = statement.executeQuery()) {
                    return resultSet.next();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


    public void update(UserModel userModel) {
        Runnable runnable = () -> {
            try (Connection connection = hikariDataBase.getConnection()) {
                try (PreparedStatement statement = connection.prepareStatement(UPDATE_QUERY)) {
                    statement.setString(1, userModel.getId().toString());
                    statement.setDouble(2, userModel.getCount());
                    statement.setDouble(3, userModel.getPlayerClass());
                    statement.setString(4, this.gson.toJson(userModel.getEnchantments()));
                    statement.setDouble(5, userModel.getCount());
                    statement.setDouble(6, userModel.getPlayerClass());
                    statement.setString(7, this.gson.toJson(userModel.getEnchantments()));
                    statement.executeUpdate();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        };
        MiningPlugin.getInstance().getHikariDataBase().executeAsync(runnable);
    }

}
