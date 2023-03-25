
package br.net.rankup.mining;

import br.net.rankup.mining.cache.EditorCache;
import br.net.rankup.mining.cache.EnchantmentAttributesCache;
import br.net.rankup.mining.cache.MineCache;
import br.net.rankup.mining.cache.UserCache;
import br.net.rankup.mining.commands.MineCommand;
import br.net.rankup.mining.database.HikariDataBase;
import br.net.rankup.mining.drops.cache.BonusCache;
import br.net.rankup.mining.drops.controller.BonusController;
import br.net.rankup.mining.drops.manager.DropManager;
import br.net.rankup.mining.enchantment.EnchantmentTypeBehavior;
import br.net.rankup.mining.enchantment.attributes.EnchantmentAttributesLoader;
import br.net.rankup.mining.enchantment.tool.EnchantmentTool;
import br.net.rankup.mining.handler.BlockHandler;
import br.net.rankup.mining.handler.MineHandler;
import br.net.rankup.mining.hook.PlaceHolderHook;
import br.net.rankup.mining.listeners.EditorListeners;
import br.net.rankup.mining.listeners.MineListeners;
import br.net.rankup.mining.listeners.PlayerListeners;
import br.net.rankup.mining.listeners.RepositoryListener;
import br.net.rankup.mining.manager.ClassesManager;
import br.net.rankup.mining.misc.BukkitUtils;
import br.net.rankup.mining.misc.ConfigUtils;
import br.net.rankup.mining.model.user.UserModel;
import br.net.rankup.mining.registry.PlaceHolderRegistry;
import br.net.rankup.mining.repository.UsersRepository;
import br.net.rankup.mining.tasks.ResetMineTask;
import io.github.rysefoxx.inventory.plugin.pagination.InventoryManager;
import lombok.Getter;
import lombok.Setter;
import me.saiintbrisson.bukkit.command.BukkitFrame;
import me.saiintbrisson.minecraft.command.message.MessageHolder;
import me.saiintbrisson.minecraft.command.message.MessageType;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Iterator;

@Getter
@Setter
public final class MiningPlugin extends JavaPlugin {

    private BonusCache bonusCache;
    private BonusController bonusController;
    private UserCache userCache;
    private MineCache mineCache;
    private EditorCache editorCache;
    private EnchantmentAttributesCache attributesCache;
    private MineHandler mineHandler;
    private BlockHandler blockHandler;
    private EnchantmentTool enchantmentTool;
    private EnchantmentTypeBehavior enchantmentBehavior;
    private HikariDataBase hikariDataBase;
    private UsersRepository usersRepository;

    public static FileConfiguration getConfiguration() {
        return configuration.getConfig();
    }

    @Override
    public void onEnable() {
        instance = this;

        start = System.currentTimeMillis();
        configuration = new ConfigUtils(this,"config.yml");
        configuration.saveDefaultConfig();

        this.userCache = new UserCache(this);
        this.mineCache = new MineCache(this);
        this.editorCache = new EditorCache(this);
        this.attributesCache = new EnchantmentAttributesCache(this);
        this.mineHandler = new MineHandler(this.getDataFolder(), "mines.db");
        this.blockHandler = new BlockHandler(this);
        this.enchantmentTool = new EnchantmentTool(this);
        this.enchantmentBehavior = new EnchantmentTypeBehavior(this);
        this.mineCache.addElements(this.mineHandler.getAll());
        (new EnchantmentAttributesLoader()).load(this.attributesCache, configuration.getConfig());
        this.bonusCache = new BonusCache(this);
        this.bonusController = new BonusController(this);
        new DropManager().loadAll();
        PlaceHolderRegistry.init();
        loadCommands();
        loadInventory();
        new EditorListeners(this);
        new MineListeners();
        new PlayerListeners(this);
        new RepositoryListener(this);
        new ClassesManager().loadAll();
        HikariDataBase.prepareDatabase();
        Constants.loadMineRewards(this);
        Constants.loadPrivateMineRewards(this);
        final RegisteredServiceProvider<Economy> registration = (RegisteredServiceProvider<Economy>)this.getServer().getServicesManager().getRegistration((Class)Economy.class);
        if (registration != null) {
            this.economy = (Economy)registration.getProvider();
        }


        (new ResetMineTask(this)).runTaskTimer(this, 20L, 50L);

        getBukkitFrame().registerCommands(
                new MineCommand());

        BukkitUtils.sendMessage(Bukkit.getConsoleSender(), "&aplugin started successfully ({time} ms)"
                .replace("{time}",""+(System.currentTimeMillis() - start)));
    }

    @Override
    public void onDisable() {
        Iterator var1 = Bukkit.getOnlinePlayers().iterator();

        while(var1.hasNext()) {
            Player onlinePlayer = (Player)var1.next();
            UserModel userModel = this.userCache.getById(onlinePlayer.getUniqueId());
            if (userModel != null) {
                this.getUsersRepository().update(userModel);
            }
        }

        BukkitUtils.sendMessage(Bukkit.getConsoleSender(), "&cplugin successfully turned off!");
    }

    @Getter
    private InventoryManager inventoryManager;
    @Getter
    private  BukkitFrame bukkitFrame;
    static long start = 0;
    private static ConfigUtils configuration;
    public static long getStart() {
        return start;
    }
    private static MiningPlugin instance;
    private Economy economy;
    public static MiningPlugin getInstance() { return instance; }


    private void loadInventory() {
        inventoryManager = new InventoryManager(this);
        inventoryManager.invoke();
    }

    public Economy getEconomy() {
        return economy;
    }

    private void loadCommands() {
        bukkitFrame = new BukkitFrame(MiningPlugin.getInstance());
        MessageHolder messageHolder = getBukkitFrame().getMessageHolder();
        messageHolder.setMessage(MessageType.ERROR, "§cOcorreu um erro durante a execução deste comando, erro: §7{error}§c.");
        messageHolder.setMessage(MessageType.INCORRECT_USAGE, "§cUtilize: /{usage}");
        messageHolder.setMessage(MessageType.NO_PERMISSION, "§cVocê não tem permissão para executar esse comando.");
        messageHolder.setMessage(MessageType.INCORRECT_TARGET, "§cVocê não pode utilizar este comando pois ele é direcionado apenas para {target}.");
    }


    public UserCache getUserCache() {
        return this.userCache;
    }

    public MineCache getMineCache() {
        return this.mineCache;
    }

    public EditorCache getEditorCache() {
        return this.editorCache;
    }

    public EnchantmentAttributesCache getAttributesCache() {
        return this.attributesCache;
    }

    public MineHandler getMineHandler() {
        return this.mineHandler;
    }

    public BlockHandler getBlockHandler() {
        return this.blockHandler;
    }

    public EnchantmentTool getEnchantmentTool() {
        return this.enchantmentTool;
    }

    public EnchantmentTypeBehavior getEnchantmentBehavior() {
        return this.enchantmentBehavior;
    }


        }
