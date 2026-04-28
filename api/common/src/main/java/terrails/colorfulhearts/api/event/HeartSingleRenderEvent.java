package terrails.colorfulhearts.api.event;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import terrails.colorfulhearts.api.heart.drawing.Heart;

/**
 * An event called after a single heart icon has been drawn to the screen
 */
public class HeartSingleRenderEvent {

    protected Heart heart;
    protected GuiGraphicsExtractor guiGraphics;
    protected int index, x, y;
    protected boolean hardcore, blinking, blinkingHeart;

    public HeartSingleRenderEvent(Heart heart, GuiGraphicsExtractor guiGraphics, int index, int x, int y, boolean hardcore, boolean blinking, boolean blinkingHeart) {
        this.heart = heart;
        this.guiGraphics = guiGraphics;
        this.index = index;
        this.x = x;
        this.y = y;
        this.hardcore = hardcore;
        this.blinking = blinking;
        this.blinkingHeart = blinkingHeart;
    }

    public Heart getHeart() {
        return heart;
    }

    public GuiGraphicsExtractor getGuiGraphics() {
        return guiGraphics;
    }

    public int getIndex() {
        return index;
    }

    public int getX() {
        return x;
    }
    public int getY() {
        return y;
    }

    public boolean isHardcoreEnabled() {
        return hardcore;
    }
    public boolean isBlinking() {
        return blinking;
    }
    public boolean isBlinkingHeart() {
        return blinkingHeart;
    }
}
