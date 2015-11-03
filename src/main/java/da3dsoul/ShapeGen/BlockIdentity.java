package da3dsoul.ShapeGen;

import com.sun.swing.internal.plaf.metal.resources.metal;
import net.minecraft.block.Block;

/**
 * Created by Thomas Baer on 11/3/2015.
 */
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
    public String toString() {
        return super.toString();
    }
}
