package terrails.colorfulhearts.forge.mixin.compat.appleskin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import squeek.appleskin.client.HUDOverlayHandler;

@Mixin(HUDOverlayHandler.class)
public interface HUDOverlayHandlerAccessor {

    @Accessor static float getFlashAlpha() {
        throw new AssertionError();
    }
}
