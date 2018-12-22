package da3dsoul.scaryGen.ModInteraction.ExtraUtilities;

import abo.ABO;
import da3dsoul.scaryGen.ModInteraction.ExtraUtilities.AutoCompressor.CompressionEventHandler;

public class ExtraUtilitiesProxy {
    public static void preinit()
    {
        ABO.autoCompressor = new ItemAutoCompressor();
        CompressionEventHandler.initialize();
    }
}
