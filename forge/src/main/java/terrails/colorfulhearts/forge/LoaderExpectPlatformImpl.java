package terrails.colorfulhearts.forge;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraftforge.common.MinecraftForge;
import terrails.colorfulhearts.CColorfulHearts;
import terrails.colorfulhearts.api.event.HeartRenderEvent;
import terrails.colorfulhearts.forge.api.event.ForgeHeartRenderEvent;
import terrails.colorfulhearts.heart.CHeartType;

import static terrails.colorfulhearts.CColorfulHearts.LOGGER;

public class LoaderExpectPlatformImpl {

    public static void applyConfig() {
        ColorfulHearts.CONFIG_SPEC.save();
        LOGGER.debug("Successfully saved changes to {} config file.", CColorfulHearts.MOD_ID + ".toml");
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
        MinecraftForge.EVENT_BUS.post(event);
        return event.getEvent();
    }

    public static void postRenderEvent(
            GuiGraphics guiGraphics, int x, int y,
            boolean blinking, boolean hardcore,
            CHeartType healthType, CHeartType absorbingType
    ) {
        ForgeHeartRenderEvent.Post event = new ForgeHeartRenderEvent.Post(guiGraphics, x, y, blinking, hardcore, healthType, absorbingType);
        MinecraftForge.EVENT_BUS.post(event);
    }
}
