package terrails.colorfulhearts.fabric.compat;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import squeek.appleskin.api.AppleSkinApi;
import squeek.appleskin.api.event.HUDOverlayEvent;
import squeek.appleskin.client.HUDOverlayHandler;
import terrails.colorfulhearts.compat.AppleSkinCompat;
import terrails.colorfulhearts.fabric.api.event.FabHeartRenderEvent;
import terrails.colorfulhearts.fabric.mixin.compat.appleskin.HUDOverlayHandlerAccessor;

public class AppleSkinFabCompat extends AppleSkinCompat implements AppleSkinApi {

    private int modifiedHealth;

    @Override
    public void registerEvents() {
        HUDOverlayEvent.HealthRestored.EVENT.register(event -> {
            // on forge this event never executes as it depends on another event that never gets called, but it does here
            // we can take advantage of this and take the value of modifiedHealth without having to calculate it
            event.isCanceled = true;
            modifiedHealth = Mth.ceil(event.modifiedHealth);
        });
        // register own custom renderer and use modifiedHealth that AppleSkin's event provided
        FabHeartRenderEvent.POST.register(event -> {
            Player player = client.player;
            assert player != null;

            int health = Mth.ceil(player.getHealth());
            if (modifiedHealth <= health) {
                return;
            }

            int absorbing = Mth.ceil(player.getAbsorptionAmount());

            // this value never reaches 1.0, so the health colors will always be somewhat mixed
            // I'll leave this behaviour as is at it makes the differentiation easier
            float alpha = ((HUDOverlayHandlerAccessor) HUDOverlayHandler.INSTANCE).getFlashAlpha();

            drawHealthOverlay(event.getGuiGraphics(), event.getX(), event.getY(), absorbing, health, modifiedHealth, alpha);
            // set value back to 0
            modifiedHealth = 0;
        });
    }
}
