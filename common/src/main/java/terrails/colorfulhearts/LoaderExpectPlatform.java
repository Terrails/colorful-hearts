package terrails.colorfulhearts;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.client.gui.GuiGraphics;
import terrails.colorfulhearts.api.event.HeartRenderEvent;
import terrails.colorfulhearts.heart.CHeartType;

public class LoaderExpectPlatform {

    /**
     * Applies changes to night-config's FileConfig
     * Technically not needed if autosave were to be enabled on both loaders
     */
    @ExpectPlatform
    public static void applyConfig() { throw new AssertionError(); }

    /**
     * A way for other mods to force usage of hardcore hearts
     * Currently only possible with Fabric via ObjectShare
     * @return if hardcore textures should be used even if a world is not hardcore
     */
    @ExpectPlatform
    public static boolean forcedHardcoreHearts() { throw new AssertionError(); }

    @ExpectPlatform
    public static HeartRenderEvent.Pre preRenderEvent(
            GuiGraphics guiGraphics, int x, int y,
            boolean blinking, boolean hardcore,
            CHeartType healthType, CHeartType absorbingType
    ) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static void postRenderEvent(
            GuiGraphics guiGraphics, int x, int y,
            boolean blinking, boolean hardcore,
            CHeartType healthType, CHeartType absorbingType
    ) {
        throw new AssertionError();
    }
}
