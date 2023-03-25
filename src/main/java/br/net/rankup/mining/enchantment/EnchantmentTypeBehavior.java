package br.net.rankup.mining.enchantment;

import br.net.rankup.mining.MiningPlugin;
import br.net.rankup.mining.model.mine.MineModel;
import br.net.rankup.mining.model.user.UserModel;
import br.net.rankup.mining.utils.Cuboid;
import br.net.rankup.mining.utils.blocks.BlockUtil;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class EnchantmentTypeBehavior {
   private final MiningPlugin plugin;

   public EnchantmentTypeBehavior(MiningPlugin plugin) {
      this.plugin = plugin;
   }

   public double processPerfurator(Player player, UserModel userModel, MineModel mineModel, Block initialBlock) {
      Cuboid cuboid = mineModel.getCuboid();
      List<Block> layer = cuboid.getLayer(initialBlock.getY());
      return BlockUtil.breakBlocks(mineModel, layer);
   }

   public double processLaser(Player player, UserModel userModel, MineModel mineModel, Block initialBlock) {
      Cuboid cuboid = mineModel.getCuboid();
      int level = userModel.getEnchantmentLevel(EnchantmentType.LASER);
      int radius = 250;
      List<Block> blocks = new ArrayList();

      int z;
      Location location;
      for(z = initialBlock.getX() - radius; z <= initialBlock.getX() + radius; ++z) {
         new Vector(z, initialBlock.getY(), initialBlock.getZ());
         location = new Location(initialBlock.getWorld(), (double)z, (double)initialBlock.getY(), (double)initialBlock.getZ());
         if (cuboid.contains(location)) {
            blocks.add(location.getBlock());
         }
      }

      for(z = initialBlock.getZ() - radius; z <= initialBlock.getZ() + radius; ++z) {
         new Vector(initialBlock.getX(), initialBlock.getY(), z);
         location = new Location(initialBlock.getWorld(), (double)initialBlock.getX(), (double)initialBlock.getY(), (double)z);
         if (cuboid.contains(location)) {
            blocks.add(location.getBlock());
         }
      }

      return BlockUtil.breakBlocks(mineModel, blocks);
   }

   public double processNuker(Player player, UserModel userModel, MineModel mineModel, Block initialBlock) {
      Cuboid cuboid = mineModel.getCuboid();
      List<Block> blocks = cuboid.getBlocks();
      player.playSound(player.getLocation(), Sound.EXPLODE, 10.0f, 1.0f);
      return BlockUtil.breakBlocks(mineModel, blocks);
   }

   public double processExplosive(Player player, UserModel userModel, MineModel mineModel, Block initialBlock) {
      int level = userModel.getEnchantmentLevel(EnchantmentType.EXPLOSIVE);
      int explosionSize = level < 100 ? 1 : (level < 500 ? 2 : (level < 1000 ? 3 : (level < 2500 ? 4 : (level < 7500 ? 5 : 6))));
      List<Block> blocks = (List)this.getNearbyBlocks(initialBlock.getLocation(), explosionSize, explosionSize, explosionSize).stream().filter((block) -> {
         return mineModel.getCuboid().contains(block);
      }).collect(Collectors.toList());
      player.playSound(player.getLocation(), Sound.EXPLODE, 10.0f, 1.0f);
      return BlockUtil.breakBlocks(mineModel, blocks);
   }

   public void processSpeed(Player player, UserModel userModel) {
      int level = userModel.getEnchantmentLevel(EnchantmentType.SPEED);
      if (level > 0) {
         if (!player.hasPotionEffect(PotionEffectType.SPEED)) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 300, 3));
            player.sendTitle("§6§lMINA", "§eVocê recebeu velocidade!");
            player.playSound(player.getLocation(), Sound.LEVEL_UP, 5.0F, 5.0F);
         }

      }
   }

   public void processTanker(Player player, UserModel userModel) {
      int level = userModel.getEnchantmentLevel(EnchantmentType.TANKER);
      if (level > 0) {
         int randomInt = 5 * level;
         Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "maquinas givefuel " + player.getName() + " gasolinacomum " + randomInt + " 1");
         player.sendTitle("§6§lMINA", "§e+ 1 Combustível de " + randomInt + "L");
         player.playSound(player.getLocation(), Sound.LEVEL_UP, 5.0F, 5.0F);
      }
   }

   public void processLocksmith(Player player, UserModel userModel) {
      int level = userModel.getEnchantmentLevel(EnchantmentType.LOCKSMITH);
      if (level > 0) {
         int limit = level < 150 ? 1 : (level < 500 ? 2 : (level < 1000 ? 3 : (level < 2500 ? 4 : (level < 7500 ? 5 : 6))));
         Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "crates give " + player.getName() + " mineracao "+limit);
         player.sendTitle("§6§lMINA", "§e+ "+limit+" Key Mineração");
         player.playSound(player.getLocation(), Sound.LEVEL_UP, 5.0F, 5.0F);
      }
   }

   public void processLimiter(Player player, UserModel userModel) {
      int level = userModel.getEnchantmentLevel(EnchantmentType.LIMITER);
      if (level > 0) {
         int limit = level < 150 ? 1 : (level < 500 ? 2 : (level < 1000 ? 3 : (level < 2500 ? 4 : (level < 7500 ? 5 : 6))));
         Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "maquinas givelimit " + player.getName() + " " + limit);
         player.sendTitle("§6§lMINA", "§e+ " + limit + " Limite de Compra");
         player.playSound(player.getLocation(), Sound.LEVEL_UP, 5.0F, 5.0F);
      }
   }

   public void processDrawer(Player player, UserModel userModel) {
      int level = userModel.getEnchantmentLevel(EnchantmentType.DRAWER);
      if (level > 0) {
         List<EnchantmentType> list = new ArrayList(Arrays.asList(EnchantmentType.values()));
         int random = (new Random()).nextInt(list.size());
         EnchantmentType enchantmentType = (EnchantmentType)list.get(random);
         userModel.addEnchantment(enchantmentType, 1);
         player.sendTitle("§9§lSORTEADOR", "§eVocê recebeu 1 nível de " + enchantmentType.getFriendlyName());
         player.playSound(player.getLocation(), Sound.LEVEL_UP, 5.0F, 5.0F);
      }
   }

   public double process(EnchantmentType type, Player player, UserModel userModel, MineModel mineModel, Block initialBlock) {
      switch(type) {
      case PERFORATOR:
         return this.processPerfurator(player, userModel, mineModel, initialBlock);
      case EXPLOSIVE:
         return this.processExplosive(player, userModel, mineModel, initialBlock);
      case NUKER:
         return this.processNuker(player, userModel, mineModel, initialBlock);
      case LASER:
         return this.processLaser(player, userModel, mineModel, initialBlock);
      case SPEED:
         this.processSpeed(player, userModel);
         break;
      case LOCKSMITH:
         this.processLocksmith(player, userModel);
         break;
      case TANKER:
         this.processTanker(player, userModel);
         break;
      case DRAWER:
         this.processDrawer(player, userModel);
         break;
      case LIMITER:
         this.processLimiter(player, userModel);
      }

      return 0.0D;
   }

   private Block relative(Block block, int x, int y, int z) {
      return block.getRelative(x, y, z);
   }

   private List<Block> getNearbyBlocks(Location location, int i, int j, int k) {
      List<Block> blocks = Lists.newArrayList();

      for(int x = location.getBlockX() - i; x <= location.getBlockX() + i; ++x) {
         for(int y = location.getBlockY() - j; y <= location.getBlockY() + j; ++y) {
            for(int z = location.getBlockZ() - k; z <= location.getBlockZ() + k; ++z) {
               blocks.add(location.getWorld().getBlockAt(x, y, z));
            }
         }
      }

      return blocks;
   }

   public MiningPlugin getPlugin() {
      return this.plugin;
   }
}