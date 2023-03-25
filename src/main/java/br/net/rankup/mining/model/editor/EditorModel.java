package br.net.rankup.mining.model.editor;

import br.net.rankup.mining.model.mine.MineModel;

import java.util.*;

public class EditorModel
{
    private final UUID id;
    private final MineModel mine;
    private EditorSelectionModel selection;
    
    public EditorModel(final UUID id, final MineModel mine, final EditorSelectionModel selection) {
        this.id = id;
        this.mine = mine;
        this.selection = selection;
    }
    
    public UUID getId() {
        return this.id;
    }
    
    public MineModel getMine() {
        return this.mine;
    }
    
    public EditorSelectionModel getSelection() {
        return this.selection;
    }
    
    public void setSelection(final EditorSelectionModel selection) {
        this.selection = selection;
    }
}
