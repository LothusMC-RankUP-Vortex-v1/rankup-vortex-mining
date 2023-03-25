package br.net.rankup.mining.listeners;

import br.net.rankup.mining.MiningPlugin;
import br.net.rankup.mining.cache.EditorCache;
import br.net.rankup.mining.model.editor.EditorModel;
import br.net.rankup.mining.model.editor.EditorSelectionModel;
import br.net.rankup.mining.model.mine.MineModel;
import org.bukkit.event.player.*;
import org.bukkit.*;
import org.bukkit.event.block.*;
import org.bukkit.entity.*;
import org.bukkit.inventory.*;
import org.bukkit.block.*;
import org.bukkit.event.*;

public class EditorListeners implements Listener
{
    private final MiningPlugin plugin;
    private final EditorCache editorCache;
    
    public EditorListeners(MiningPlugin miningPlugin) {
        this.plugin = MiningPlugin.getInstance();
        this.editorCache = this.plugin.getEditorCache();
        miningPlugin.getServer().getPluginManager().registerEvents(this, miningPlugin);
        this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
    }
    
    @EventHandler
    public void onPlayerInteract(final PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        final EditorModel editorModel = this.editorCache.getById(player.getUniqueId());
        if (editorModel != null) {
            final ItemStack itemInHand = player.getItemInHand();
            if (itemInHand != null && itemInHand.getType() == Material.BLAZE_ROD) {
                event.setCancelled(true);
                final Block clickedBlock = event.getClickedBlock();
                final EditorSelectionModel selection = editorModel.getSelection();
                if (selection == null) {
                    return;
                }
                if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                    selection.setMin(clickedBlock.getLocation());
                    player.sendMessage("§aPosi\u00e7\u00e3o 1 da mina definida.");
                }
                else if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
                    selection.setMax(clickedBlock.getLocation());
                    player.sendMessage("§aPosi\u00e7\u00e3o 2 da mina definida.");
                }
                if (selection.isValid()) {
                    final MineModel mine = editorModel.getMine();
                    if (mine == null) {
                        return;
                    }
                    mine.setCuboid(selection.asCuboid());
                    selection.clear();
                    editorModel.setSelection(null);
                    player.sendMessage(new String[] { "", "§e Agora, v\u00e1 at\u00e9 o spawn da mina e digite o", "§e comando de criar a mina novamente.", "" });
                }
            }
        }
    }
}
