package br.net.rankup.mining.utils.blocks;

import java.util.Iterator;
import java.util.List;

import br.net.rankup.mining.MiningPlugin;
import br.net.rankup.mining.model.mine.MineModel;
import com.boydti.fawe.FaweAPI;
import com.boydti.fawe.object.FaweQueue;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;

public class BlockUtil {
   private static final MiningPlugin PLUGIN = MiningPlugin.getInstance();

   public static double breakBlocks(final MineModel mineModel, List<Block> blocks) {
      FaweQueue queue = FaweAPI.createQueue(FaweAPI.getWorld(mineModel.getSpawnLocation().getWorld().getName()), false);
      double brokenBlocks = 0.0D;
      Iterator var5 = blocks.iterator();

      while(var5.hasNext()) {
         Block block = (Block)var5.next();
         if (block.getType() != Material.AIR) {
            queue.setBlock(block.getX(), block.getY(), block.getZ(), 0);
            ++brokenBlocks;
         }
      }

      queue.flush();
      if (mineModel.isEmpty()) {
         Bukkit.getScheduler().runTaskLater(PLUGIN, new Runnable() {
            public void run() {
               mineModel.reset(BlockUtil.PLUGIN.getBlockHandler());
            }
         }, 40L);
      }

      return brokenBlocks;
   }

   public static void resetMine(MineModel mineModel) {
      FaweQueue queue = FaweAPI.createQueue(FaweAPI.getWorld(mineModel.getSpawnLocation().getWorld().getName()), false);
      Iterator var2 = mineModel.getCuboid().iterator();

      while(var2.hasNext()) {
         Block block = (Block)var2.next();
         if (block.getType() == Material.AIR) {
            queue.setBlock(block.getX(), block.getY(), block.getZ(), mineModel.getCompound().getItemTypeId(), mineModel.getCompound().getData());
         }
      }

      queue.flush();
   }
}