package da3dsoul.ShapeGen;

import net.minecraft.block.Block;

public class BlockIdentity {

    private final Block block;
    private final int meta;

    public BlockIdentity(Block b, int m) {
        block = b;
        meta = m;
    }

    public BlockIdentity(Block b) {
        block = b;
        meta = 0;
    }

    public Block getBlock() { return block; }
    public int getMeta() { return meta; }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof BlockIdentity) {
            if(block == ((BlockIdentity) obj).getBlock() && meta == ((BlockIdentity) obj).getMeta()) return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Block.getIdFromBlock(block) << 8 | meta;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return new BlockIdentity(block,meta);
    }

    @Override
    public String toString() {
        return "" + Block.blockRegistry.getNameForObject(block) + "," + meta;
    }
}
