//package terrails.colorfulhearts.extensions.neoforge.mixin.farmersdelight;
//
//import org.spongepowered.asm.mixin.Mixin;
//import org.spongepowered.asm.mixin.injection.At;
//import org.spongepowered.asm.mixin.injection.ModifyVariable;
//import vectorwing.farmersdelight.client.gui.HUDOverlays;
//
//@Mixin(value = HUDOverlays.class, remap = false)
//public class HUDOverlaysMixin {
//
//    /**
//     * The mod tries to render the effect in the topmost heart row
//     * Since the rows do not move here, it has to be kept constant
//     */
//    @ModifyVariable(method = "drawComfortOverlay", at = @At("STORE"), ordinal = 10)
//    private static int colorfulhearts$drawComfortOverlay(int leftHeightOffset) {
//        return 0;
//    }
//}
