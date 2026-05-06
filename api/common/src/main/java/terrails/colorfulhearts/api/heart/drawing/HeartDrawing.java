package terrails.colorfulhearts.api.heart.drawing;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
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
    public abstract void draw(GuiGraphics guiGraphics, int x, int y, boolean half, boolean hardcore, boolean highlight, int argb);

    /**
     * should handle every single heart variant for the specific type/colour
     *
     * @param half      render half a heart
     * @param hardcore  render hardcore variant of a heart
     * @param highlight render highlighted/blinking variant of a heart
     */
    public void draw(GuiGraphics guiGraphics, int x, int y, boolean half, boolean hardcore, boolean highlight) {
        this.draw(guiGraphics, x, y, half, hardcore, highlight, -1);
    }

    /**
     * Used for easier differentiation while debugging
     *
     * @return drawing's id
     */
    public abstract ResourceLocation getId();

    @Override
    public String toString() {
        return getId().toString();
    }

    public static HeartDrawing colorBlend(HeartDrawing drawing, ResourceLocation id, float r, float g, float b, float a) {
        return new HeartDrawing() {
            @Override
            public void draw(GuiGraphics guiGraphics, int x, int y, boolean half, boolean hardcore, boolean highlight, int argb) {
                drawing.draw(guiGraphics, x, y, half, hardcore, highlight, argb);
                drawing.draw(guiGraphics, x, y, half, hardcore, highlight, ARGB.colorFromFloat(a, r, g, b));
            }

            @Override
            public ResourceLocation getId() {
                return id;
            }
        };
    }
}
