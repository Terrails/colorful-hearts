package terrails.colorfulhearts.api.heart.drawing;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.resources.Identifier;

public abstract class HeartDrawing {

    /**
     * should handle every single heart variant for the specific type/colour
     * @param half render half a heart
     * @param hardcore render hardcore variant of a heart
     * @param highlight render highlighted/blinking variant of a heart
     */
    public abstract void draw(GuiGraphicsExtractor guiGraphics, int x, int y, boolean half, boolean hardcore, boolean highlight);

    /**
     * Used for easier differentiation while debugging
     * @return drawing's id
     */
    public abstract Identifier getId();

    @Override
    public String toString() {
        return getId().toString();
    }

    public static HeartDrawing colorBlend(HeartDrawing drawing, Identifier id, float r, float g, float b, float a, int sourceFactor, int destinationFactor) {
        return new HeartDrawing() {
            @Override
            public void draw(GuiGraphicsExtractor guiGraphics, int x, int y, boolean half, boolean hardcore, boolean highlight) {
                drawing.draw(guiGraphics, x, y, half, hardcore, highlight);
                //TODO
//                RenderSystem.enableBlend();
//                RenderSystem.setShaderColor(r, g, b, a);
//                RenderSystem.blendFunc(sourceFactor, destinationFactor);
                drawing.draw(guiGraphics, x, y, half, hardcore, highlight);
//                RenderSystem.defaultBlendFunc();
//                RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
//                RenderSystem.disableBlend();
            }

            @Override
            public Identifier getId() {
                return id;
            }
        };
    }
}
