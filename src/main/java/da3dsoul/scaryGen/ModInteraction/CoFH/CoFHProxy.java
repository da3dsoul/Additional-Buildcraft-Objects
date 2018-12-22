package da3dsoul.scaryGen.ModInteraction.CoFH;

import abo.ABO;
import da3dsoul.ImplicationEventHandler;
import da3dsoul.scaryGen.generate.GeostrataGen.Ore.COFH.COFHOverride;

public class CoFHProxy {
    public static void preinit()
    {
        if(ABO.geostrataInstalled && ABO.cofhInstalled) {
            ABO.aboLog.info("COFH is Loaded");
            COFHOverride.overrideCOFHWordGen(0);
        }
        ABO.thermalImplication = new ItemThermalImplication();
        ImplicationEventHandler.initialize();
    }

    public static void init()
    {

    }
}
