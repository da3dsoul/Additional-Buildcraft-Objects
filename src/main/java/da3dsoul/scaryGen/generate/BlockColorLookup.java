package da3dsoul.scaryGen.generate;

public enum BlockColorLookup {
    STONE(0x7d7d7d, "minecraft:stone"),
    GRANITE(0xad846a, "GeoStrata:geostrata_rock_granite_smooth"),
    BASALT(0x2c2c2e, "GeoStrata:geostrata_rock_basalt_smooth"),
    MARBLE(0xaeaeb6, "GeoStrata:geostrata_rock_marble_smooth"),
    LIMESTONE(0xccbfae, "GeoStrata:geostrata_rock_limestone_smooth"),
    SHALE(0x686a71, "GeoStrata:geostrata_rock_shale_smooth"),
    SANDSTONE(0xbb9d81, "GeoStrata:geostrata_rock_sandstone_smooth"),
    PUMICE(0xbab8b1, "GeoStrata:geostrata_rock_pumice_smooth"),
    SLATE(0x47494e, "GeoStrata:geostrata_rock_slate_smooth"),
    GNEISS(0x9b9d9d, "GeoStrata:geostrata_rock_gneiss_smooth"),
    PERIDOTITE(0x5b6b5a, "GeoStrata:geostrata_rock_peridotite_smooth"),
    QUARTZ(0xc0c7ce, "GeoStrata:geostrata_rock_quartz_smooth"),
    GRANULITE(0xb6b6aa, "GeoStrata:geostrata_rock_granulite_smooth"),
    HORNFEL(0x7f828b, "GeoStrata:geostrata_rock_hornfel_smooth"),
    MIGMATITE(0x81817c, "GeoStrata:geostrata_rock_migmatite_smooth"),
    SCHIST(0x4f4e57, "GeoStrata:geostrata_rock_schist_smooth"),
    ONYX(0x424242, "GeoStrata:geostrata_rock_onyx_smooth"),
    OPAL(0xc892f4, "GeoStrata:geostrata_rock_opal_smooth"),
    COLORED_STONE(0xd1d1d2, "ExtraUtilities:color_stone");

    BlockColorLookup(int color, String name){
        this.color = color;
        this.name = name;
    }

    public String name = "";
    public int color = 0;

}
