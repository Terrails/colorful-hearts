package terrails.colorfulhearts.neoforge;

import net.minecraft.client.gui.GuiGraphics;
import net.neoforged.neoforge.common.NeoForge;
import terrails.colorfulhearts.CColorfulHearts;
import terrails.colorfulhearts.api.event.HeartRenderEvent;
import terrails.colorfulhearts.heart.CHeartType;
import terrails.colorfulhearts.neoforge.api.event.ForgeHeartRenderEvent;

public class LoaderExpectPlatformImpl {

    public static void applyConfig() {
        ColorfulHearts.CONFIG_SPEC.save();
        CColorfulHearts.LOGGER.debug("Successfully saved changes to {} config file.", CColorfulHearts.MOD_ID + ".toml");
    }

    public static boolean forcedHardcoreHearts() {
        return false;
    }

    public static HeartRenderEvent.Pre preRenderEvent(
            GuiGraphics guiGraphics, int x, int y,
            boolean blinking, boolean hardcore,
            CHeartType healthType, CHeartType absorbingType
    ) {
        ForgeHeartRenderEvent.Pre event = new ForgeHeartRenderEvent.Pre(guiGraphics, x, y, blinking, hardcore, healthType, absorbingType);
        NeoForge.EVENT_BUS.post(event);
        return event.getEvent();
    }

    public static void postRenderEvent(
            GuiGraphics guiGraphics, int x, int y,
            boolean blinking, boolean hardcore,
            CHeartType healthType, CHeartType absorbingType
    ) {
        ForgeHeartRenderEvent.Post event = new ForgeHeartRenderEvent.Post(guiGraphics, x, y, blinking, hardcore, healthType, absorbingType);
        NeoForge.EVENT_BUS.post(event);
    }
}
