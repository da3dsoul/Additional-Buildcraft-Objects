package da3dsoul.asm;

import com.google.common.eventbus.EventBus;
import cpw.mods.fml.common.DummyModContainer;
import cpw.mods.fml.common.LoadController;
import cpw.mods.fml.common.ModMetadata;
import cpw.mods.fml.relauncher.IFMLCallHook;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
import da3dsoul.reference.Metadata;

import java.util.Map;

@IFMLLoadingPlugin.TransformerExclusions({"da3dsoul.asm"})
public class LoadingPlugin implements IFMLLoadingPlugin {

    // Start CoreMod

    public String[] getASMTransformerClass() {
        return new String[] { "da3dsoul.asm.da3dsoulClassTransformer" };
    }

    public String getModContainerClass() {
        return DA3DSOULASMFIXDummyContainer.class.getName();
    }

    public String getSetupClass() {
        return DA3DSOULASMFIXDummyContainer.class.getName();
    }

    public void injectData(final Map<String, Object> data) {
    }

    public String getAccessTransformerClass() {
        return null;
    }

    public static class DA3DSOULASMFIXDummyContainer extends DummyModContainer implements IFMLCallHook
    {
        public DA3DSOULASMFIXDummyContainer() {
            super(new ModMetadata());
            final ModMetadata md = this.getMetadata();
            Metadata.init(md);
        }

        public void injectData(final Map<String, Object> data) {
        }

        public boolean registerBus(final EventBus bus, final LoadController controller) {
            bus.register((Object)this);
            return true;
        }

        public Void call() throws Exception {
            return null;
        }
    }
}
