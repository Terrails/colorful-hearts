package terrails.colorfulhearts.api.event;

import net.minecraft.client.gui.GuiGraphics;
import terrails.colorfulhearts.heart.CHeartType;

/**
 * A set of events useful to render any overlays
 */
public class HeartOverlayEvent {

    /**
     * Event executed before health renderer does anything
     */
    public static class Pre extends HeartOverlayEvent {

        private boolean cancelled = false;

        public Pre(
                GuiGraphics guiGraphics, int x, int y,
                boolean blinking, boolean hardcore,
                CHeartType healthType, CHeartType absorbingType
        ) {
            super(guiGraphics, x, y, blinking, hardcore, healthType, absorbingType);
        }

        public void cancel() {
            this.cancelled = true;
        }

        public boolean isCancelled() {
            return this.cancelled;
        }
    }

    /**
     * Event executed after health renderer finished
     */
    public static class Post extends HeartOverlayEvent {

        public Post(
                GuiGraphics guiGraphics, int x, int y,
                boolean blinking, boolean hardcore,
                CHeartType healthType, CHeartType absorbingType
        ) {
            super(guiGraphics, x, y, blinking, hardcore, healthType, absorbingType);
        }
    }

    private final GuiGraphics guiGraphics;
    private int x, y;
    private boolean blinking, hardcore;
    private CHeartType healthType, absorbingType;

    public HeartOverlayEvent(
            GuiGraphics guiGraphics, int x, int y,
            boolean blinking, boolean hardcore,
            CHeartType healthType, CHeartType absorbingType
    ) {
        this.guiGraphics = guiGraphics;
        this.x = x;
        this.y = y;
        this.blinking = blinking;
        this.hardcore = hardcore;
        this.healthType = healthType;
        this.absorbingType = absorbingType;
    }

    public GuiGraphics getGuiGraphics() {
        return guiGraphics;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public boolean isBlinking() {
        return blinking;
    }

    public void setBlinking(boolean blinking) {
        this.blinking = blinking;
    }

    public boolean isHardcore() {
        return hardcore;
    }

    public void setHardcore(boolean hardcore) {
        this.hardcore = hardcore;
    }

    public CHeartType getHealthType() {
        return healthType;
    }

    public void setHealthType(CHeartType healthType) {
        this.healthType = healthType;
    }

    public CHeartType getAbsorbingType() {
        return absorbingType;
    }

    public void setAbsorbingType(CHeartType absorbingType) {
        this.absorbingType = absorbingType;
    }
}
