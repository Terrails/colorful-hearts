package terrails.colorfulhearts.forge.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import vectorwing.farmersdelight.client.gui.ComfortHealthOverlay;

@Mixin(value = ComfortHealthOverlay.class, remap = false)
public class ComfortHealthOverlayMixin {

    /**
     * Makes sure that the effect overlay is always in the same position.
     * <p>
     * The mod tries to render the effect in the bottommost heart row
     * by calculating the position from the highest row down.
     * Since the rows do not move here, it has to be kept constant
     */
    @ModifyVariable(method = "drawComfortOverlay", at = @At("STORE"), ordinal = 10)
    private static int colorfulhearts$drawComfortOverlay(int originalValue) {
        return 0;
    }

}
