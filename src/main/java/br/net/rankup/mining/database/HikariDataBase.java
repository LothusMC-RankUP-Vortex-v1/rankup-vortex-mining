package br.net.rankup.mining.database;

import br.net.rankup.mining.MiningPlugin;
import br.net.rankup.mining.repository.UsersRepository;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.Getter;
import org.bukkit.Bukkit;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HikariDataBase {

    @Getter
    public HikariDataSource dataSource;

    public HikariDataBase(String ip, String database, String user, String password) throws Exception {
        openConnection(ip, database, user, password);
    }

    private void openConnection(String ip, String database, String user, String password) throws Exception {
        if (dataSource != null) return;

        try {
            HikariConfig hikariConfig = new HikariConfig();
            hikariConfig.setUsername(user);
            hikariConfig.setPassword(password);
            hikariConfig.setDriverClassName("org.mariadb.jdbc.Driver");
            hikariConfig.setJdbcUrl(String.format("jdbc:mariadb://%s/%s", ip, database));

            dataSource = new HikariDataSource(hikariConfig);

            Logger.getLogger("com.zaxxer.hikari").setLevel(Level.OFF);
        } catch (Exception e) {
            throw new Exception("Não foi possivel iniciar a conexão com banco de dados MySQL Hikari.", e);
        }
    }

    public void executeAsync(Runnable runnable) {
        CompletableFuture.runAsync(runnable);
    }

    public Connection getConnection() {
        try {
            return dataSource.getConnection();
        } catch (SQLException exception) {
            return null;
        }
    }

    public static void prepareDatabase() {
        try {
            String ip = MiningPlugin.getInstance().getConfig().getString("Database.IP");
            String database = MiningPlugin.getInstance().getConfig().getString("Database.Database");
            String user = MiningPlugin.getInstance().getConfig().getString("Database.User");
            String password = MiningPlugin.getInstance().getConfig().getString("Database.Password");

            MiningPlugin.getInstance().setHikariDataBase(new HikariDataBase(ip, database, user, password));

            MiningPlugin.getInstance().setUsersRepository(new UsersRepository());
            MiningPlugin.getInstance().getUsersRepository().createTable();
        } catch (Exception e) {
            e.printStackTrace();
            Bukkit.getServer().shutdown();
        }
    }

}