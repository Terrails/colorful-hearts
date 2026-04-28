package terrails.colorfulhearts.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.PlayerTabOverlay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import terrails.colorfulhearts.render.TabHeartRenderer;

import java.util.UUID;

@Mixin(PlayerTabOverlay.class)
public class PlayerTabOverlayMixin {

    @Inject(method = "extractTablistHearts", cancellable = true,
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;blitSprite(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIII)V")
    )
    private void colorfulhearts_renderTablistHearts(
            int yo, int left, int right, UUID profileId, GuiGraphicsExtractor guiGraphics, int scoreValue, CallbackInfo ci, @Local PlayerTabOverlay.HealthState healthState
    ) {
        // this handles just 2 rows and then uses vanilla NNhp format (default behaviour)
        TabHeartRenderer.INSTANCE.renderPlayerListHud(yo, left, right, guiGraphics, scoreValue, healthState);
        ci.cancel();
    }
}
