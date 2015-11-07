package da3dsoul.scaryGen.generate.feature;

import da3dsoul.ShapeGen.ShapeGen;
import net.minecraft.world.gen.feature.*;
import net.minecraft.world.*;
import java.util.*;
import net.minecraft.block.*;
import net.minecraft.init.*;

public class ScaryGen_WorldGenElevatedMangroveTree extends WorldGenerator
{
    World wo;
    Random rand;
    int rootRand;
    int rootAlt;
    private int[][][] check;
    private boolean planted;
    private int baseY;
    private Block hatWoodBlock;
    private Block hatLeafBlock;
    private Block hatBaseBlock1;
    private Block hatBaseBlock2;
    private int woodMeta;
    private int leafMeta;
    private int stuntmin;
    private int heightmin;
    private int heightmax;
    public boolean useShapeGen;
    
    public ScaryGen_WorldGenElevatedMangroveTree(final boolean flag) {
        super(flag);
        this.planted = flag;
        this.check = new int[9][9][9];
        this.rootRand = 0;
        this.rootAlt = 0;
    }
    
    public void setConfigOptions(final Block wood, final Block leaf, final int woodmeta, final int leafmeta, final Block Base1, final Block Base2, final int height1, final int height2, final int stunt) {
        this.hatWoodBlock = wood;
        this.hatLeafBlock = leaf;
        this.hatBaseBlock1 = Base1;
        this.hatBaseBlock2 = Base2;
        this.woodMeta = woodmeta;
        this.leafMeta = leafmeta;
        this.heightmin = height1;
        this.heightmax = height2;
        this.stuntmin = stunt;
    }
    
    private void setBlockAndMetadata(final int par1, final int par2, final int par3, final Block par4, final int par5) {
        try {
            if(useShapeGen) {
                ShapeGen.getShapeGen(wo).addBlock(par1, par2, par3, par4, par5);
            } else {
                this.wo.setBlock(par1, par2, par3, par4, par5, 3);
            }
        }
        catch (RuntimeException ex) {}
    }
    
    private void setBlockAndMetadataWithNotify(final int par1, final int par2, final int par3, final Block par4, final int par5) {
        setBlockAndMetadata(par1,par2,par3,par4,par5);
    }
    
    private Block getBlock(final int par1, final int par2, final int par3) {
        try {
            return this.wo.getBlock(par1, par2, par3);
        }
        catch (RuntimeException e) {
            return null;
        }
    }
    
    public boolean generate(final World world, final Random random, final int i, final int j, final int k) {
        return this.generateCustom(world, random, i, j, k, 4, 30);
    }
    
    public boolean generateCustom(final World world, final Random random, final int i, final int j, final int k, final int hatBase, final int hatHeight) {
        this.wo = world;
        this.rand = random;
        this.baseY = j;
        int l = random.nextInt(this.heightmax - this.heightmin) + this.heightmin;
        if (j < 1) {
            return false;
        }
        if (j + l + 1 > 256) {
            l = 256 - j - 2;
            if (l < this.stuntmin) {
                return false;
            }
        }
        if (!this.planted) {
            if (this.hatBaseBlock1 != Blocks.air || this.hatBaseBlock2 != Blocks.air) {
                boolean flag = false;
                final Block id = this.getBlock(i, j - 1, k);
                if (this.hatBaseBlock1 != Blocks.air && id == this.hatBaseBlock1) {
                    flag = true;
                }
                if (this.hatBaseBlock2 != Blocks.air && id == this.hatBaseBlock2) {
                    flag = true;
                }
                if (!flag) {
                    return false;
                }
            }
            final Block id = this.getBlock(i, j, k);
            if (id != Blocks.air && id != this.hatLeafBlock) {
                return false;
            }
        }
        final double pitch = 1.2566370614359172;
        double pbias = 0.0;
        double dir = this.rand.nextFloat() * 3.141592653589793 / 2.0;
        final double spin = 3.8830085198369844;
        final double grow = 10.0;
        final double shrink = 0.618;
        final double len = (double)l;
        final double y = (double)l;
        final double rootSlope = 0.0;
        this.growRoot(i, j, k - 1, 0.6625, rootSlope);
        this.growRoot(i + 1, j, k - 1, 0.8375, rootSlope);
        this.growRoot(i + 2, j, k, 0.9125, rootSlope);
        this.growRoot(i + 2, j, k + 1, 0.0875, rootSlope);
        this.growRoot(i + 1, j, k + 2, 0.1625, rootSlope);
        this.growRoot(i, j, k + 2, 0.3375, rootSlope);
        this.growRoot(i - 1, j, k + 1, 0.4125, rootSlope);
        this.growRoot(i - 1, j, k, 0.5875, rootSlope);
        if (hatBase == 4) {
            for (int n = 0; n < l; ++n) {
                this.setBlockAndMetadata(i, j + n, k - 1, this.hatWoodBlock, this.woodMeta);
                this.setBlockAndMetadata(i + 1, j + n, k - 1, this.hatWoodBlock, this.woodMeta);
                this.setBlockAndMetadata(i + 2, j + n, k, this.hatWoodBlock, this.woodMeta);
                this.setBlockAndMetadata(i + 2, j + n, k + 1, this.hatWoodBlock, this.woodMeta);
                this.setBlockAndMetadata(i + 1, j + n, k + 2, this.hatWoodBlock, this.woodMeta);
                this.setBlockAndMetadata(i, j + n, k + 2, this.hatWoodBlock, this.woodMeta);
                this.setBlockAndMetadata(i - 1, j + n, k + 1, this.hatWoodBlock, this.woodMeta);
                this.setBlockAndMetadata(i - 1, j + n, k, this.hatWoodBlock, this.woodMeta);
            }
        }
        else if (hatBase == 5) {
            for (int n = 0; n < l - 1; ++n) {
                this.setBlockAndMetadata(i + 2, j + n, k - 1, this.hatWoodBlock, this.woodMeta);
                this.setBlockAndMetadata(i + 2, j + n, k, this.hatWoodBlock, this.woodMeta);
                this.setBlockAndMetadata(i + 2, j + n, k + 1, this.hatWoodBlock, this.woodMeta);
                this.setBlockAndMetadata(i - 2, j + n, k - 1, this.hatWoodBlock, this.woodMeta);
                this.setBlockAndMetadata(i - 2, j + n, k, this.hatWoodBlock, this.woodMeta);
                this.setBlockAndMetadata(i - 2, j + n, k + 1, this.hatWoodBlock, this.woodMeta);
                this.setBlockAndMetadata(i - 1, j + n, k + 2, this.hatWoodBlock, this.woodMeta);
                this.setBlockAndMetadata(i, j + n, k + 2, this.hatWoodBlock, this.woodMeta);
                this.setBlockAndMetadata(i + 1, j + n, k + 2, this.hatWoodBlock, this.woodMeta);
                this.setBlockAndMetadata(i - 1, j + n, k - 2, this.hatWoodBlock, this.woodMeta);
                this.setBlockAndMetadata(i, j + n, k - 2, this.hatWoodBlock, this.woodMeta);
                this.setBlockAndMetadata(i + 1, j + n, k - 2, this.hatWoodBlock, this.woodMeta);
            }
        }
        else if (hatBase == 7) {
            for (int n = 0; n < l - 2; ++n) {
                this.setBlockAndMetadata(i + 3, j + n, k - 1, this.hatWoodBlock, this.woodMeta);
                this.setBlockAndMetadata(i + 3, j + n, k, this.hatWoodBlock, this.woodMeta);
                this.setBlockAndMetadata(i + 3, j + n, k + 1, this.hatWoodBlock, this.woodMeta);
                this.setBlockAndMetadata(i - 3, j + n, k - 1, this.hatWoodBlock, this.woodMeta);
                this.setBlockAndMetadata(i - 3, j + n, k, this.hatWoodBlock, this.woodMeta);
                this.setBlockAndMetadata(i - 3, j + n, k + 1, this.hatWoodBlock, this.woodMeta);
                this.setBlockAndMetadata(i - 1, j + n, k + 3, this.hatWoodBlock, this.woodMeta);
                this.setBlockAndMetadata(i, j + n, k + 3, this.hatWoodBlock, this.woodMeta);
                this.setBlockAndMetadata(i + 1, j + n, k + 3, this.hatWoodBlock, this.woodMeta);
                this.setBlockAndMetadata(i - 1, j + n, k - 3, this.hatWoodBlock, this.woodMeta);
                this.setBlockAndMetadata(i, j + n, k - 3, this.hatWoodBlock, this.woodMeta);
                this.setBlockAndMetadata(i + 1, j + n, k - 3, this.hatWoodBlock, this.woodMeta);
                this.setBlockAndMetadata(i + 2, j + n, k + 2, this.hatWoodBlock, this.woodMeta);
                this.setBlockAndMetadata(i + 2, j + n, k - 2, this.hatWoodBlock, this.woodMeta);
                this.setBlockAndMetadata(i - 2, j + n, k + 2, this.hatWoodBlock, this.woodMeta);
                this.setBlockAndMetadata(i - 2, j + n, k - 2, this.hatWoodBlock, this.woodMeta);
            }
        }
        for (int n = 0; n < l; ++n) {
            this.growBranch(i, j + n, k, (double)(l - n) * 0.8, dir, 0.0, pbias, 0.0, 1);
            this.growBranch(i, j + n, k, (double)(l - n) * 0.8, dir + 3.141592653589793, 0.0, pbias, 0.0, 1);
            dir += spin;
            pbias = (pbias - 1.5707963267948966) * 0.9 + 1.5707963267948966;
        }
        for (int n = 0; n < l - 1; ++n) {
            this.setBlockAndMetadata(i, j + n, k, Blocks.air, 0);
            this.setBlockAndMetadata(i + 1, j + n, k, Blocks.air, 0);
            this.setBlockAndMetadata(i + 1, j + n, k + 1, Blocks.air, 0);
            this.setBlockAndMetadata(i, j + n, k + 1, Blocks.air, 0);
        }
        if (hatBase >= 5) {
            for (int n = 0; n < l - 2; ++n) {
                this.setBlockAndMetadata(i - 1, j + n, k + 1, Blocks.air, 0);
                this.setBlockAndMetadata(i - 1, j + n, k, Blocks.air, 0);
                this.setBlockAndMetadata(i - 1, j + n, k - 1, Blocks.air, 0);
                this.setBlockAndMetadata(i, j + n, k - 1, Blocks.air, 0);
                this.setBlockAndMetadata(i + 1, j + n, k - 1, Blocks.air, 0);
            }
        }
        if (hatBase == 7) {
            for (int n = 0; n < l - 3; ++n) {
                this.setBlockAndMetadata(i - 2, j + n, k - 1, Blocks.air, 0);
                this.setBlockAndMetadata(i - 2, j + n, k, Blocks.air, 0);
                this.setBlockAndMetadata(i - 2, j + n, k + 1, Blocks.air, 0);
                this.setBlockAndMetadata(i + 2, j + n, k - 1, Blocks.air, 0);
                this.setBlockAndMetadata(i + 2, j + n, k, Blocks.air, 0);
                this.setBlockAndMetadata(i + 2, j + n, k + 1, Blocks.air, 0);
                this.setBlockAndMetadata(i - 1, j + n, k - 2, Blocks.air, 0);
                this.setBlockAndMetadata(i, j + n, k - 2, Blocks.air, 0);
                this.setBlockAndMetadata(i + 1, j + n, k - 2, Blocks.air, 0);
                this.setBlockAndMetadata(i - 1, j + n, k + 2, Blocks.air, 0);
                this.setBlockAndMetadata(i, j + n, k + 2, Blocks.air, 0);
                this.setBlockAndMetadata(i + 1, j + n, k + 2, Blocks.air, 0);
            }
        }
        return true;
    }
    
    private void growBulk(final int i, final int j, final int k, double l, final double dir, final double pitch, final double pbias, final double pbias2, double grow) {
        double y = 0.0;
        grow /= 8.0;
        final double shrink = 0.9416;
        this.growBranch(i + 1, j + (int)y, k, l, dir + 0.0, pitch, pbias, pbias2, 0);
        y += grow;
        l *= shrink;
        this.growBranch(i + 1, j + (int)y, k + 1, l, dir + 0.7853981633974483, pitch, pbias, pbias2, 0);
        y += grow;
        l *= shrink;
        this.growBranch(i, j + (int)y, k + 1, l, dir + 1.5707963267948966, pitch, pbias, pbias2, 0);
        y += grow;
        l *= shrink;
        this.growBranch(i - 1, j + (int)y, k + 1, l, dir + 2.356194490192345, pitch, pbias, pbias2, 0);
        y += grow;
        l *= shrink;
        this.growBranch(i - 1, j + (int)y, k, l, dir + 3.141592653589793, pitch, pbias, pbias2, 0);
        y += grow;
        l *= shrink;
        this.growBranch(i - 1, j + (int)y, k - 1, l, dir + 3.9269908169872414, pitch, pbias, pbias2, 0);
        y += grow;
        l *= shrink;
        this.growBranch(i, j + (int)y, k - 1, l, dir + 4.71238898038469, pitch, pbias, pbias2, 0);
        y += grow;
        l *= shrink;
        this.growBranch(i + 1, j + (int)y, k - 1, l, dir + 5.497787143782138, pitch, pbias, pbias2, 0);
    }
    
    private void growBranch(int i, int j, int k, double len, double dir, double pitch, final double pbias, final double pbias2, final int size) {
        double dx = 0.0;
        double dy = 0.0;
        double dz = 0.0;
        double spin = 0.0;
        double heave = 0.0;
        final double blen = len * 0.75;
        this.plotWood(i, j, k, size);
        while (len > 1.0) {
            dy += Math.sin(pitch);
            final double dd = Math.cos(pitch);
            dx += Math.cos(dir) * dd;
            dz += Math.sin(dir) * dd;
            boolean step = false;
            if (dx >= 1.0) {
                ++i;
                --dx;
                step = true;
            }
            else if (dx <= -1.0) {
                --i;
                ++dx;
                step = true;
            }
            if (dy >= 1.0) {
                ++j;
                --dy;
                step = true;
            }
            else if (dy <= -1.0) {
                --j;
                ++dy;
                step = true;
            }
            if (dz >= 1.0) {
                ++k;
                --dz;
                step = true;
            }
            else if (dz <= -1.0) {
                --k;
                ++dz;
                step = true;
            }
            if (step) {
                final Block id = this.getBlock(i, j, k);
                if (id != Blocks.air && id != this.hatWoodBlock && id != this.hatLeafBlock) {
                    break;
                }
                this.plotWood(i, j, k, size);
            }
            spin += this.rand.nextFloat() * 0.1 - 0.05;
            heave += this.rand.nextFloat() * 0.1 - 0.05;
            dir += spin;
            if (pitch > pbias) {
                heave -= 0.01;
            }
            else {
                heave += 0.01;
            }
            if (heave > 0.2) {
                heave = 0.2;
            }
            if (heave < -0.2) {
                heave = -0.2;
            }
            pitch += heave;
            pitch = (pitch - pbias2) * 0.8 + pbias2;
            --len;
        }
        this.treeLeaf(i, j, k, 3);
    }
    
    private void plotProp(final int i, final int j, final int k) {
        int n = 1;
        while (n < 15) {
            if (this.getBlock(i + 1, j - n, k) == Blocks.fence) {
                return;
            }
            if (this.getBlock(i - 1, j - n, k) == Blocks.fence) {
                return;
            }
            if (this.getBlock(i, j - n, k + 1) == Blocks.fence) {
                return;
            }
            if (this.getBlock(i, j - n, k - 1) == Blocks.fence) {
                return;
            }
            final Block id = this.getBlock(i, j - n, k);
            if (id == Blocks.air || id == Blocks.tallgrass || id == Blocks.yellow_flower || id == Blocks.red_flower || id == Blocks.brown_mushroom || id == Blocks.red_mushroom || id == this.hatLeafBlock || id == this.hatWoodBlock) {
                ++n;
            }
            else {
                if (id == Blocks.grass || id == Blocks.dirt || id == Blocks.stone || id == Blocks.sand) {
                    break;
                }
                if (id == Blocks.gravel) {
                    break;
                }
                return;
            }
        }
        for (int q = 1; q <= n; ++q) {
            final Block id = this.getBlock(i, j - q, k);
            if (id == Blocks.air || id == Blocks.tallgrass || id == Blocks.yellow_flower || id == Blocks.red_flower || id == Blocks.brown_mushroom || id == Blocks.red_mushroom) {
                this.setBlockAndMetadata(i, j - q, k, Blocks.fence, 0);
            }
        }
    }
    
    private void plotWood(final int i, final int j, final int k, final int size) {
        this.setBlockAndMetadata(i, j, k, this.hatWoodBlock, this.woodMeta);
        if (size <= 1) {
            this.setBlockAndMetadata(i + 1, j, k, this.hatWoodBlock, this.woodMeta);
            this.setBlockAndMetadata(i + 1, j, k + 1, this.hatWoodBlock, this.woodMeta);
            this.setBlockAndMetadata(i, j, k + 1, this.hatWoodBlock, this.woodMeta);
            if (size == 0) {
                this.setBlockAndMetadata(i, j + 1, k, this.hatWoodBlock, this.woodMeta);
                this.setBlockAndMetadata(i + 1, j + 1, k, this.hatWoodBlock, this.woodMeta);
                this.setBlockAndMetadata(i + 1, j + 1, k + 1, this.hatWoodBlock, this.woodMeta);
                this.setBlockAndMetadata(i, j + 1, k + 1, this.hatWoodBlock, this.woodMeta);
            }
        }
    }
    
    private void treeLeaf(final int i, final int j, final int k, final int r) {
        if (r <= 0) {
            return;
        }
        final int rr = r * r + 1;
        final int rrr = (r - 1) * (r - 1);
        for (int ii = 0; ii <= r; ++ii) {
            for (int jj = -r / 2; jj <= r; ++jj) {
                for (int kk = 0; kk <= r; ++kk) {
                    final int zz = ii * ii + jj * jj + kk * kk;
                    if (zz <= rr) {
                        final boolean flag = zz >= rrr;
                        this.treeLeafSpot(i + ii, j + jj, k + kk, flag);
                        this.treeLeafSpot(i + ii, j + jj, k - kk, flag);
                        this.treeLeafSpot(i - ii, j + jj, k + kk, flag);
                        this.treeLeafSpot(i - ii, j + jj, k - kk, flag);
                    }
                }
            }
        }
        for (int n = -r; n <= r; ++n) {
            if (this.getBlock(i + r, j, k) == this.hatLeafBlock) {
                this.plotVine(i + r, j, k, 1);
                this.plotVine(i + r, j, k, 4);
            }
            if (this.getBlock(i, j, k + r) == this.hatLeafBlock) {
                this.plotVine(i, j, k + r, 2);
                this.plotVine(i, j, k + r, 8);
            }
        }
    }
    
    private void treeLeafSpot(final int i, final int j, final int k, final boolean flag) {
        if (this.getBlock(i, j, k) == Blocks.air) {
            this.setBlockAndMetadata(i, j, k, this.hatLeafBlock, this.leafMeta);
        }
    }
    
    private void plotVine(int i, int j, int k, int l) {
        int ii;
        int kk;
        if (l == 2) {
            ii = 1;
            kk = 0;
        }
        else if (l == 1) {
            ii = 0;
            kk = -1;
        }
        else if (l == 4) {
            ii = 0;
            kk = 1;
        }
        else {
            ii = -1;
            kk = 0;
            l = 8;
        }
        Block id = this.getBlock(i + ii, j, k + kk);
        for (int n = 0; n < 4 && id == this.hatLeafBlock; id = this.getBlock(i + ii, j, k + kk), ++n) {
            i += ii;
            k += kk;
        }
        if (id != Blocks.air) {
            return;
        }
        this.setBlockAndMetadataWithNotify(i, j, k, Blocks.vine, l);
        for (int i2 = 20; this.getBlock(i, --j, k) == Blocks.air && i2 > 0; --i2) {
            this.setBlockAndMetadataWithNotify(i, j, k, Blocks.vine, l);
        }
    }
    
    private int getMedium(final int i, final int j, final int k) {
        final Block[] canGrowOpen = { Blocks.air, Blocks.sapling, Blocks.flowing_water, Blocks.water, Blocks.flowing_lava, Blocks.lava, Blocks.log, Blocks.log2, Blocks.leaves, Blocks.leaves2 };
        final Block[] canGrowSolid = { Blocks.grass, Blocks.dirt, Blocks.sand, Blocks.gravel };
        final Block qq = this.getBlock(i, j, k);
        int medium = 0;
        for (int m = 0; m < canGrowOpen.length; ++m) {
            if (qq == canGrowOpen[m]) {
                medium = 1;
                break;
            }
        }
        if (medium == 0) {
            for (int m = 0; m < canGrowSolid.length; ++m) {
                if (qq == canGrowSolid[m]) {
                    medium = 2;
                    break;
                }
            }
        }
        return medium;
    }
    
    void growRoot(final int l, int m, final int n, double theta, double phi) {
        if (this.rootAlt == 1) {
            this.rootRand = this.rand.nextInt(2);
            m -= this.rootRand;
            this.rootAlt = 2;
        }
        else if (this.rootAlt == 2) {
            if (this.rootRand == 0) {
                --m;
            }
            this.rootAlt = 0;
        }
        else if (this.rootAlt == 10) {
            m -= this.rand.nextInt(2);
        }
        ++m;
        phi -= this.rand.nextFloat() * 0.05;
        theta += this.rand.nextFloat() * 0.1 - 0.05;
        double direction = 6.283185307179586 * theta;
        double curl = (double)(this.rand.nextFloat() * 0.4f - 0.2f);
        double pitch = 6.283185307179586 * phi;
        int length = 14 + this.rand.nextInt(2);
        double x;
        if (l > 0) {
            x = l + 0.5;
        }
        else {
            x = l - 0.5;
        }
        double y = m + 0.5;
        double z;
        if (n > 0) {
            z = n + 0.5;
        }
        else {
            z = n - 0.5;
        }
        int i = (int)x;
        int j = (int)y;
        int k = (int)z;
        int med = this.getMedium(i, j, k);
        int cnt = 0;
        while (length > 0.0) {
            --length;
            curl = curl + this.rand.nextFloat() * 0.06f - 0.029999999329447746;
            if (med == 1) {
                pitch = (pitch + 1.5707963267948966) * 0.7 - 1.5707963267948966;
            }
            else {
                pitch = (pitch + 1.5707963267948966) * 0.9 - 1.5707963267948966;
            }
            final double hoz = Math.cos(pitch);
            double x2 = x + Math.cos(direction) * hoz;
            final double y2 = y + Math.sin(pitch);
            double z2 = z + Math.sin(direction) * hoz;
            int i2 = (int)x2;
            final int j2 = (int)y2;
            int k2 = (int)z2;
            if (i2 != i || j2 != j || k2 != k) {
                this.setBlockAndMetadata(i, j, k, this.hatWoodBlock, this.woodMeta);
                if (++cnt < 4 && (j2 != j - 1 || i2 != i || k2 != k)) {
                    this.setBlockAndMetadata(i, j - 1, k, this.hatWoodBlock, this.woodMeta);
                }
                med = this.getMedium(i2, j2, k2);
                if (med != 0) {
                    x = x2;
                    y = y2;
                    z = z2;
                    i = i2;
                    j = j2;
                    k = k2;
                }
                else {
                    med = this.getMedium(i, j - 1, k);
                    if (med != 0) {
                        --y;
                        --j;
                        pitch = -1.5707963267948966;
                    }
                    else {
                        x2 = x + Math.cos(direction);
                        z2 = z + Math.sin(direction);
                        i2 = (int)x2;
                        k2 = (int)z2;
                        med = this.getMedium(i2, j, k2);
                        if (med != 0) {
                            x = x2;
                            z = z2;
                            i = i2;
                            k = k2;
                            pitch = 0.0;
                        }
                        else {
                            int dir = (int)(direction * 8.0 / 3.141592653589793);
                            if (dir < 0) {
                                dir = 15 - (15 - dir) % 16;
                            }
                            else {
                                dir %= 16;
                            }
                            final int pol = dir % 2;
                            int di = i2 - i;
                            int dk = k2 - k;
                            final int[] tdir = { 0, 0, 0, 0 };
                            if (di == 0 && dk == 0) {
                                if (dir < 1) {
                                    di = 1;
                                    dk = 0;
                                }
                                else if (dir < 3) {
                                    di = 1;
                                    dk = 1;
                                }
                                else if (dir < 5) {
                                    di = 0;
                                    dk = 1;
                                }
                                else if (dir < 7) {
                                    di = -1;
                                    dk = 1;
                                }
                                else if (dir < 9) {
                                    di = -1;
                                    dk = 0;
                                }
                                else if (dir < 11) {
                                    di = -1;
                                    dk = -1;
                                }
                                else if (dir < 13) {
                                    di = 0;
                                    dk = -1;
                                }
                                else if (dir < 15) {
                                    di = 1;
                                    dk = -1;
                                }
                                else {
                                    di = 1;
                                    dk = 0;
                                }
                            }
                            if (dk == 0) {
                                if (di > 0) {
                                    if (pol == 1) {
                                        tdir[0] = 2;
                                        tdir[1] = 14;
                                        tdir[2] = 4;
                                        tdir[3] = 12;
                                    }
                                    else {
                                        tdir[0] = 14;
                                        tdir[tdir[1] = 2] = 12;
                                        tdir[3] = 4;
                                    }
                                }
                                else if (pol == 1) {
                                    tdir[0] = 6;
                                    tdir[1] = 10;
                                    tdir[2] = 4;
                                    tdir[3] = 12;
                                }
                                else {
                                    tdir[0] = 10;
                                    tdir[1] = 6;
                                    tdir[2] = 12;
                                    tdir[3] = 4;
                                }
                            }
                            else if (di == 0) {
                                if (dk > 0) {
                                    if (pol == 1) {
                                        tdir[0] = 2;
                                        tdir[1] = 6;
                                        tdir[2] = 0;
                                        tdir[3] = 8;
                                    }
                                    else {
                                        tdir[0] = 6;
                                        tdir[tdir[1] = 2] = 8;
                                        tdir[3] = 0;
                                    }
                                }
                                else if (pol == 1) {
                                    tdir[0] = 10;
                                    tdir[1] = 14;
                                    tdir[2] = 8;
                                    tdir[3] = 0;
                                }
                                else {
                                    tdir[0] = 14;
                                    tdir[1] = 10;
                                    tdir[2] = 0;
                                    tdir[3] = 8;
                                }
                            }
                            else if (dk > 0) {
                                if (di > 0) {
                                    if (pol == 1) {
                                        tdir[0] = 0;
                                        tdir[1] = 4;
                                        tdir[2] = 14;
                                        tdir[3] = 6;
                                    }
                                    else {
                                        tdir[0] = 4;
                                        tdir[1] = 0;
                                        tdir[2] = 6;
                                        tdir[3] = 14;
                                    }
                                }
                                else if (pol == 1) {
                                    tdir[0] = 4;
                                    tdir[1] = 8;
                                    tdir[2] = 2;
                                    tdir[3] = 10;
                                }
                                else {
                                    tdir[0] = 8;
                                    tdir[1] = 4;
                                    tdir[2] = 10;
                                    tdir[3] = 2;
                                }
                            }
                            else if (di > 0) {
                                if (pol == 1) {
                                    tdir[0] = 12;
                                    tdir[1] = 0;
                                    tdir[2] = 10;
                                    tdir[3] = 2;
                                }
                                else {
                                    tdir[0] = 0;
                                    tdir[1] = 12;
                                    tdir[2] = 2;
                                    tdir[3] = 10;
                                }
                            }
                            else if (pol == 1) {
                                tdir[0] = 8;
                                tdir[1] = 12;
                                tdir[2] = 6;
                                tdir[3] = 14;
                            }
                            else {
                                tdir[0] = 12;
                                tdir[1] = 8;
                                tdir[2] = 14;
                                tdir[3] = 6;
                            }
                            for (int q = 0; q < 4; ++q) {
                                if (tdir[q] == 0) {
                                    di = 1;
                                    dk = 0;
                                }
                                else if (tdir[q] == 2) {
                                    di = 1;
                                    dk = 1;
                                }
                                else if (tdir[q] == 4) {
                                    di = 0;
                                    dk = 1;
                                }
                                else if (tdir[q] == 6) {
                                    di = -1;
                                    dk = 1;
                                }
                                else if (tdir[q] == 8) {
                                    di = -1;
                                    dk = 0;
                                }
                                else if (tdir[q] == 10) {
                                    di = -1;
                                    dk = -1;
                                }
                                else if (tdir[q] == 12) {
                                    di = 0;
                                    dk = -1;
                                }
                                else {
                                    di = 1;
                                    dk = -1;
                                }
                                i2 = i + di;
                                k2 = k + dk;
                                med = this.getMedium(i2, j, k2);
                                if (med != 0) {
                                    i = i2;
                                    k = k2;
                                    x = i + 0.5;
                                    z = k + 0.5;
                                    pitch = 0.0;
                                    direction = tdir[q] * 2.0 * 3.141592653589793 / 16.0;
                                    break;
                                }
                            }
                            if (med == 0) {
                                return;
                            }
                            continue;
                        }
                    }
                }
            }
        }
    }
}
