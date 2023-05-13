package terrails.colorfulhearts.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.PlayerTabOverlay;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.world.scores.Objective;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import terrails.colorfulhearts.render.TabHeartRenderer;

@Mixin(PlayerTabOverlay.class)
public class PlayerTabOverlayMixin {

    @Inject(method = "renderTablistScore", cancellable = true,
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/components/PlayerTabOverlay;blit(Lcom/mojang/blaze3d/vertex/PoseStack;IIIIII)V",
                    opcode = 0,
                    shift = At.Shift.BEFORE),
            locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    private void renderTablistScore(Objective objective, int y, String string, int x, int offset, PlayerInfo playerInfo, PoseStack poseStack, CallbackInfo ci) {
        int scoreValue = objective.getScoreboard().getOrCreatePlayerScore(string, objective).getScore();
        // this handles just 2 rows and then uses vanilla NNhp format (default behaviour)
        TabHeartRenderer.INSTANCE.renderPlayerListHud(y, x, offset, poseStack, scoreValue, playerInfo);
        ci.cancel();
    }
}
