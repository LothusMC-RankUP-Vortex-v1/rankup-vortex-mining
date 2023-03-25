package br.net.rankup.mining.cache;

import br.net.rankup.mining.MiningPlugin;
import br.net.rankup.mining.model.editor.EditorModel;
import br.net.rankup.mining.utils.Cache;

import java.util.*;

public class EditorCache extends Cache<EditorModel>
{
    private final MiningPlugin plugin;
    
    public EditorCache(final MiningPlugin plugin) {
        this.plugin = plugin;
    }
    
    public EditorModel getById(final UUID id) {
        return this.get(editorModel -> editorModel.getId().equals(id));
    }
    
    public MiningPlugin getPlugin() {
        return this.plugin;
    }
}
