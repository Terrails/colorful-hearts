package terrails.colorfulhearts.extensions.fabric.compat;

import vectorwing.farmersdelight.client.gui.HUDOverlays;

import terrails.colorfulhearts.api.event.HeartRenderEvent;
import terrails.colorfulhearts.api.fabric.event.FabHeartEvents;
import net.minecraft.client.Minecraft;

public class FarmersDelightCompat {

    public FarmersDelightCompat() {
        FabHeartEvents.POST_RENDER.register(this::render);
    }

    public void render(HeartRenderEvent.Post event) {
        HUDOverlays.ComfortOverlay.INSTANCE.render(event.getGuiGraphics(), Minecraft.getInstance().getDeltaTracker());
    }
}
