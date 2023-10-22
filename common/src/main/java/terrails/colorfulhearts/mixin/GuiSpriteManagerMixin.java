package terrails.colorfulhearts.mixin;

import com.mojang.serialization.Codec;
import net.minecraft.client.gui.GuiSpriteManager;
import net.minecraft.client.renderer.texture.atlas.SpriteSources;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import terrails.colorfulhearts.CColorfulHearts;
import terrails.colorfulhearts.render.atlas.sources.ColoredHearts;

@Mixin(GuiSpriteManager.class)
public class GuiSpriteManagerMixin {

    /**
     * Registers custom SpriteSource in order to be able to add dynamically colored hearts to vanilla gui atlas
     * <p>
     * Would've done this inject directly into the static constructor of SpriteSources, but a mixin error occurs there.
     * I assume it is due to it calling {@link SpriteSources#register(String, Codec)} earlier than it is declared.
     * Moving the inject to TAIL didn't offer a fix.
     */
    @Inject(method = "<init>", at = @At("TAIL"))
    private void colorfulhearts_init(CallbackInfo c) {
        ColoredHearts.COLORED_HEARTS = SpriteSources.register(CColorfulHearts.MOD_ID + ":colored_hearts", ColoredHearts.CODEC);
    }
}
