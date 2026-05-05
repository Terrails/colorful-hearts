package terrails.colorfulhearts.api.heart.drawing;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

public class SpriteHeartDrawing extends HeartDrawing {

    final ResourceLocation id;
    final ResourceLocation full, fullBlinking, half, halfBlinking, hardcoreFull, hardcoreFullBlinking, hardcoreHalf, hardcoreHalfBlinking;

    SpriteHeartDrawing(ResourceLocation id,
                       ResourceLocation full, ResourceLocation fullBlinking, ResourceLocation half, ResourceLocation halfBlinking,
                       ResourceLocation hardcoreFull, ResourceLocation hardcoreFullBlinking, ResourceLocation hardcoreHalf, ResourceLocation hardcoreHalfBlinking) {
        this.id = id;
        this.full = full;
        this.fullBlinking = fullBlinking;
        this.half = half;
        this.halfBlinking = halfBlinking;
        this.hardcoreFull = hardcoreFull;
        this.hardcoreFullBlinking = hardcoreFullBlinking;
        this.hardcoreHalf = hardcoreHalf;
        this.hardcoreHalfBlinking = hardcoreHalfBlinking;
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public void draw(GuiGraphics guiGraphics, int x, int y, boolean half, boolean hardcore, boolean highlight) {
        ResourceLocation spriteLocation;
        if (hardcore) {
            if (highlight) {
                spriteLocation = half ? this.hardcoreHalfBlinking : this.hardcoreFullBlinking;
            } else {
                spriteLocation = half ? this.hardcoreHalf : this.hardcoreFull;
            }
        } else {
            if (highlight) {
                spriteLocation = half ? this.halfBlinking : this.fullBlinking;
            } else {
                spriteLocation = half ? this.half : this.full;
            }
        }
        guiGraphics.blitSprite(RenderType::guiTextured, spriteLocation, x, y, 9, 9);
    }

    public static SpriteHeartBuilder build(ResourceLocation id) {
        return new SpriteHeartBuilder(id);
    }

    public static class SpriteHeartBuilder {

        final ResourceLocation id;

        SpriteHeartBuilder(ResourceLocation id) {
            this.id = id;
        }

        public SpriteHeartDrawing finish(ResourceLocation full, ResourceLocation fullBlinking, ResourceLocation half, ResourceLocation halfBlinking,
                                         ResourceLocation hardcoreFull, ResourceLocation hardcoreFullBlinking, ResourceLocation hardcoreHalf, ResourceLocation hardcoreHalfBlinking) {
            return new SpriteHeartDrawing(this.id, full, fullBlinking, half, halfBlinking, hardcoreFull, hardcoreFullBlinking, hardcoreHalf, hardcoreHalfBlinking);
        }
    }
}
