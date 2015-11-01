//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package da3dsoul.scaryGen.generate.GeostrataGen.Ore.COFH;

import abo.ABO;
import cofh.api.world.IGeneratorParser;
import cofh.lib.util.WeightedRandomBlock;
import com.google.gson.JsonObject;
import java.util.List;
import net.minecraft.world.gen.feature.WorldGenerator;
import org.apache.logging.log4j.Logger;

public class ClusterParserOverride implements IGeneratorParser {
    private final boolean sparse;

    public ClusterParserOverride(boolean var1) {
        this.sparse = var1;
    }

    public WorldGenerator parseGenerator(String var1, JsonObject var2, Logger var3, List<WeightedRandomBlock> var4, int var5, List<WeightedRandomBlock> var6) {
        ABO.aboLog.info("Used ClusterParserOverride");
        return (WorldGenerator)(this.sparse?new WorldGenSparseMinableClusterOverride(var4, var5, var6):new WorldGenMinableClusterOverride(var4, var5, var6));
    }
}
