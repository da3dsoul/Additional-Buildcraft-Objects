package da3dsoul.scaryGen.generate.GeostrataGen.Ore.COFH;

import abo.ABO;
import cofh.api.world.IGeneratorParser;
import cofh.core.world.FeatureParser;
import cofh.core.world.decoration.ClusterParser;
import org.apache.logging.log4j.Level;

import java.lang.reflect.Field;
import java.util.*;

public class COFHOverride {

    public static int overrideCOFHWordGen(int j) {
        try {
            Class classFeatureParser = FeatureParser.class;
            Field fieldGeneratorHandlers = classFeatureParser.getDeclaredField("generatorHandlers");
            fieldGeneratorHandlers.setAccessible(true);
            HashMap<String, IGeneratorParser> generatorHandlers = (HashMap<String, IGeneratorParser>) fieldGeneratorHandlers.get(null);
            Iterator<Map.Entry<String,IGeneratorParser>> it = generatorHandlers.entrySet().iterator();
            ArrayList<String> keysClusterFalse = new ArrayList<String>();
            ArrayList<String> keysClusterTrue = new ArrayList<String>();
            do {
                if(!it.hasNext()) break;
                Map.Entry<String, IGeneratorParser> entry = it.next();
                if(entry.getValue() instanceof ClusterParser && entry.getKey() != null && entry.getKey().contains("sparse")) {
                    keysClusterTrue.add(entry.getKey());
                    it.remove();
                } else if(entry.getValue() instanceof ClusterParser) {
                    keysClusterFalse.add(entry.getKey());
                    it.remove();
                }
            }while(true);

            for(String key : keysClusterFalse) {
                FeatureParser.registerGenerator(key, new ClusterParserOverride(false));
            }
            for(String key : keysClusterTrue) {
                FeatureParser.registerGenerator(key, new ClusterParserOverride(true));
            }

            generatorHandlers = (HashMap<String, IGeneratorParser>) fieldGeneratorHandlers.get(null);
            it = generatorHandlers.entrySet().iterator();

            do {
                if(!it.hasNext()) break;
                Map.Entry<String, IGeneratorParser> entry = it.next();
                if(entry.getValue() instanceof ClusterParserOverride) {
                    ABO.aboLog.info("Replaced " + entry.getKey() + " in FeatureParser with an override");
                    j++;
                }
            }while(true);
        } catch(Throwable t) {
            ABO.aboLog.catching(Level.INFO, t);
        }

        return j;
    }
}
