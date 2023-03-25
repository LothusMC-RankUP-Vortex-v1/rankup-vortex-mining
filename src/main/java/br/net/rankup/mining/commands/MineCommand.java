package br.net.rankup.mining.commands;

import br.net.rankup.mining.MiningPlugin;
import br.net.rankup.mining.cache.EditorCache;
import br.net.rankup.mining.cache.MineCache;
import br.net.rankup.mining.handler.MineHandler;
import br.net.rankup.mining.inventory.Defaultlnventory;
import br.net.rankup.mining.inventory.MinesInventory;
import br.net.rankup.mining.misc.BukkitUtils;
import br.net.rankup.mining.misc.InventoryUtils;
import br.net.rankup.mining.model.editor.EditorModel;
import br.net.rankup.mining.model.editor.EditorSelectionModel;
import br.net.rankup.mining.model.mine.MineModel;
import io.github.rysefoxx.inventory.plugin.pagination.RyseInventory;
import me.saiintbrisson.minecraft.command.annotation.Command;
import me.saiintbrisson.minecraft.command.command.Context;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.material.MaterialData;

import java.io.IOException;

public class MineCommand {


   private final MiningPlugin plugin;
   private final MineCache mineCache;
   private final MineHandler mineHandler;
   private final EditorCache editorCache;

   public MineCommand() {
      this.plugin = MiningPlugin.getInstance();
      this.mineCache = this.plugin.getMineCache();
      this.mineHandler = this.plugin.getMineHandler();
      this.editorCache = this.plugin.getEditorCache();
   }

   @Command(
      name = "mine",
      aliases = {"mina", "minas"}
   )
   public void handleMineCommand(final Context<CommandSender> context) {
      final CommandSender execution = context.getSender();
      final Player player = (Player)execution;
      if(!InventoryUtils.getList().contains(player.getName())) {
         RyseInventory inventory = new Defaultlnventory(MiningPlugin.getInstance().getConfig()).build();
         inventory.open(player);
         InventoryUtils.addDelay(player);
      }
      player.playSound(player.getLocation(), Sound.ORB_PICKUP, 5.0F, 5.0F);
   }

   @Command(name = "mine.help", aliases = {"ajuda"}, permission = "commands.mine")
   public void handleHelpCommand(final Context<CommandSender> context) {
      final CommandSender execution = context.getSender();
      final Player player = (Player)execution;
      BukkitUtils.sendMessage(player, "");
      BukkitUtils.sendMessage(player, " &a/mine create &7- &fCriar uma nova mina.");
      BukkitUtils.sendMessage(player, " &a/mine delete <id> &7- &fDeletar uma mina existente.");
      BukkitUtils.sendMessage(player, " &a/mine reset <name> &7- &fResetar os blocos de uma mina.");
      BukkitUtils.sendMessage(player, "");

   }

   @Command(name = "mine.create", usage = "mina create <id> <name> <reset-time> <material-id> <material-data>", permission = "commands.mine")
   public void handleCreateMineCommand(final Context<CommandSender> context, final String id, String name, final long resetTime, final int materialId, final byte materialData) throws IOException {
      final CommandSender execution = context.getSender();
      final Player player = (Player)execution;
      final EditorModel editorModel = this.editorCache.getById(player.getUniqueId());
      if (editorModel != null && editorModel.getMine() != null) {
         final MineModel mine = editorModel.getMine();
         if (mine.getCuboid() != null) {
            mine.setSpawnLocation(player.getLocation());
            this.mineCache.addElements(mine);
            this.mineHandler.set(mine.getId(), mine);
            this.editorCache.removeElement(editorModel);
            mine.reset(this.plugin.getBlockHandler());
            execution.sendMessage("§aMina '" + mine.getName() + "' criada com sucesso.");
         }
      }
      else {
         final MineModel mineModel = MineModel.builder().id(id).name(name).resetTime(resetTime * 1000L).compound(new MaterialData(materialId, materialData)).build();
         this.editorCache.addElement(new EditorModel(player.getUniqueId(), mineModel, new EditorSelectionModel()));
         execution.sendMessage(new String[] { "", "§e Agora, selecione a área da mina.", "" });
      }
   }

   @Command(name = "mine.delete", usage = "mine delete <id>", permission = "commands.mine")
   public void handleDeleteMineCommand(final Context<CommandSender> context, final String id) {
      final CommandSender execution = context.getSender();
      final Player player = (Player)execution;
      final MineModel mineModel = this.mineCache.getAndRemove($ -> $.getId().equalsIgnoreCase(id));
      if (mineModel == null) {
         execution.sendMessage("§cEsta mina n\u00e3o existe.");
         return;
      }
      this.editorCache.removeIf(editorModel -> editorModel.getMine().equals(mineModel));
      execution.sendMessage("§aMina deletada com sucesso.");
   }

   @Command(name = "mine reset", usage = "mine reset <name>", permission = "commands.mine")
   public void handleResetMineCommand(final Context<CommandSender> context, final String mine) {
      final CommandSender execution = context.getSender();
      final Player player = (Player)execution;
      final MineModel mineModel = this.mineCache.getByName(mine);
      if (mineModel == null) {
         execution.sendMessage("§cEsta mina não existe.");
         return;
      }
      mineModel.reset(this.plugin.getBlockHandler());
      execution.sendMessage("§aMina '" + mine + "' resetada com sucesso!");
   }

}