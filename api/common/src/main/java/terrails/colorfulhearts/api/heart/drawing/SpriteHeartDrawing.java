package terrails.colorfulhearts.api.heart.drawing;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;

public class SpriteHeartDrawing extends HeartDrawing {

    final Identifier id;
    final Identifier full, fullBlinking, half, halfBlinking, hardcoreFull, hardcoreFullBlinking, hardcoreHalf, hardcoreHalfBlinking;

    SpriteHeartDrawing(Identifier id,
                       Identifier full, Identifier fullBlinking, Identifier half, Identifier halfBlinking,
                       Identifier hardcoreFull, Identifier hardcoreFullBlinking, Identifier hardcoreHalf, Identifier hardcoreHalfBlinking) {
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
    public Identifier getId() {
        return id;
    }

    @Override
    public void draw(GuiGraphicsExtractor guiGraphics, int x, int y, boolean half, boolean hardcore, boolean highlight, int argb) {
        Identifier spriteLocation;
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
        guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, spriteLocation, x, y, 9, 9, argb);
    }

    public static SpriteHeartBuilder build(Identifier id) {
        return new SpriteHeartBuilder(id);
    }

    public static class SpriteHeartBuilder {

        final Identifier id;

        SpriteHeartBuilder(Identifier id) {
            this.id = id;
        }

        public SpriteHeartDrawing finish(Identifier full, Identifier fullBlinking, Identifier half, Identifier halfBlinking,
                                         Identifier hardcoreFull, Identifier hardcoreFullBlinking, Identifier hardcoreHalf, Identifier hardcoreHalfBlinking) {
            return new SpriteHeartDrawing(this.id, full, fullBlinking, half, halfBlinking, hardcoreFull, hardcoreFullBlinking, hardcoreHalf, hardcoreHalfBlinking);
        }
    }
}
