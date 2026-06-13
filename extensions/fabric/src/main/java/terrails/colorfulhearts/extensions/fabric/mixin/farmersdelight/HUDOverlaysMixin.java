package terrails.colorfulhearts.extensions.fabric.mixin.farmersdelight;

import net.fabricmc.fabric.api.client.rendering.v1.hud.HudStatusBarHeightRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import vectorwing.farmersdelight.client.gui.HUDOverlays;

import net.minecraft.client.Minecraft;

@Mixin(value = HUDOverlays.class, remap = false)
public class HUDOverlaysMixin {

    /**
     * The mod tries to render the effect in the topmost heart row
     * Since the rows do not move here, it has to be kept constant
     */
    @ModifyVariable(method = "drawComfortOverlay", at = @At("STORE"), name = "leftHeightOffset")
    private static int colorfulhearts$drawComfortOverlay1(int leftHeightOffset) {
        return 0;
    }

    /**
     * Move the y value to the same height as the health bar using the builtin fabric API
     */
    @ModifyVariable(method = "drawComfortOverlay", at = @At("HEAD"), name = "top", argsOnly = true)
    private static int colorfulhearts$drawComfortOverlay2(int top) {
        var height = Minecraft.getInstance().getWindow().getGuiScaledHeight();
        return height - HudStatusBarHeightRegistry.getHeight(VanillaHudElements.HEALTH_BAR);
    }
}
