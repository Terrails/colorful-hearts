package terrails.colorfulhearts.extensions.fabric.compat;

import squeek.appleskin.api.AppleSkinApi;
import squeek.appleskin.api.event.HUDOverlayEvent;
import squeek.appleskin.client.HUDOverlayHandler;

import terrails.colorfulhearts.api.fabric.event.FabHeartEvents;
import terrails.colorfulhearts.extensions.compat.AppleSkinCommonCompat;
import terrails.colorfulhearts.extensions.fabric.mixin.appleskin.HUDOverlayHandlerAccessor;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;

public class AppleSkinCompat extends AppleSkinCommonCompat implements AppleSkinApi {

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
        FabHeartEvents.POST_RENDER.register(event -> {
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

            drawHealthOverlay(event.getGuiGraphics(), event.getX(), event.getY(), absorbing, health, modifiedHealth, alpha, event.isHardcore());
            // set value back to 0
            modifiedHealth = 0;
        });

        FabHeartEvents.UPDATE.register(() -> this.lastHealth = 0);
    }
}
