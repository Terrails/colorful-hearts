package terrails.colorfulhearts.neoforge.mixin.compat.appleskin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import squeek.appleskin.client.HUDOverlayHandler;

@Mixin(value = HUDOverlayHandler.class, remap = false)
public interface HUDOverlayHandlerAccessor {

    @Accessor static float getFlashAlpha() {
        throw new AssertionError();
    }
}
