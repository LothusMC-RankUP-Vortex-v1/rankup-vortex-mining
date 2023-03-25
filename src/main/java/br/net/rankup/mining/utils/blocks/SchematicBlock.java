package br.net.rankup.mining.utils.blocks;

public class SchematicBlock
{
    private int x;
    private int y;
    private int z;
    private int blockId;
    private byte data;
    
    public SchematicBlock(final int x, final int y, final int z, final int blockId, final byte data) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.blockId = blockId;
        this.data = data;
    }
    
    public int getX() {
        return this.x;
    }
    
    public int getY() {
        return this.y;
    }
    
    public int getZ() {
        return this.z;
    }
    
    public int getBlockId() {
        return this.blockId;
    }
    
    public byte getData() {
        return this.data;
    }
}
