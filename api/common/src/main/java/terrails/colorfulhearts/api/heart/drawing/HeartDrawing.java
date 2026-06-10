package terrails.colorfulhearts.api.heart.drawing;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;

public abstract class HeartDrawing {

    /**
     * should handle every single heart variant for the specific type/colour
     *
     * @param half      render half a heart
     * @param hardcore  render hardcore variant of a heart
     * @param highlight render highlighted/blinking variant of a heart
     * @param argb      apply an ARGB color to the drawing
     */
    public abstract void draw(GuiGraphicsExtractor guiGraphics, int x, int y, boolean half, boolean hardcore, boolean highlight, int argb);

    /**
     * should handle every single heart variant for the specific type/colour
     *
     * @param half      render half a heart
     * @param hardcore  render hardcore variant of a heart
     * @param highlight render highlighted/blinking variant of a heart
     */
    public void draw(GuiGraphicsExtractor guiGraphics, int x, int y, boolean half, boolean hardcore, boolean highlight) {
        this.draw(guiGraphics, x, y, half, hardcore, highlight, -1);
    }

    /**
     * Used for easier differentiation while debugging
     *
     * @return drawing's id
     */
    public abstract Identifier getId();

    @Override
    public String toString() {
        return getId().toString();
    }

    public static HeartDrawing colorBlend(HeartDrawing drawing, Identifier id, float r, float g, float b, float a) {
        return new HeartDrawing() {
            @Override
            public void draw(GuiGraphicsExtractor guiGraphics, int x, int y, boolean half, boolean hardcore, boolean highlight, int argb) {
                drawing.draw(guiGraphics, x, y, half, hardcore, highlight, argb);
                drawing.draw(guiGraphics, x, y, half, hardcore, highlight, ARGB.colorFromFloat(a, r, g, b));
            }

            @Override
            public Identifier getId() {
                return id;
            }
        };
    }
}
