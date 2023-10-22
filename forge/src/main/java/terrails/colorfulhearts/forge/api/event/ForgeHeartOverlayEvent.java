package terrails.colorfulhearts.forge.api.event;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraftforge.eventbus.api.Event;
import terrails.colorfulhearts.api.event.HeartOverlayEvent;
import terrails.colorfulhearts.heart.CHeartType;

/**
 * A set of events useful to render any overlays
 */
public class ForgeHeartOverlayEvent extends Event {

    /**
     * Event executed before health renderer does anything
     */
    public static class Pre extends ForgeHeartOverlayEvent {

        public Pre(
                GuiGraphics guiGraphics, int x, int y,
                boolean blinking, boolean hardcore,
                CHeartType healthType, CHeartType absorbingType
        ) {
            super(new HeartOverlayEvent.Pre(guiGraphics, x, y, blinking, hardcore, healthType, absorbingType));
        }

        @Override
        public boolean isCancelable() {
            return true;
        }

        @Override
        public boolean isCanceled() {
            return ((HeartOverlayEvent.Pre) this.getEvent()).isCancelled();
        }

        @Override
        public void setCanceled(boolean cancel) {
            if (cancel) {
                ((HeartOverlayEvent.Pre) this.getEvent()).cancel();
            }
            super.setCanceled(cancel);
        }
    }

    /**
     * Event executed after health renderer finished
     */
    public static class Post extends ForgeHeartOverlayEvent {

        public Post(
                GuiGraphics guiGraphics, int x, int y,
                boolean blinking, boolean hardcore,
                CHeartType healthType, CHeartType absorbingType
        ) {
            super(new HeartOverlayEvent.Post(guiGraphics, x, y, blinking, hardcore, healthType, absorbingType));
        }
    }

    private final HeartOverlayEvent event;

    public ForgeHeartOverlayEvent(HeartOverlayEvent event) {
        this.event = event;
    }

    public HeartOverlayEvent getEvent() {
        return event;
    }

    public GuiGraphics getGuiGraphics() {
        return event.getGuiGraphics();
    }

    public int getX() {
        return event.getX();
    }

    public void setX(int x) {
        event.setX(x);
    }

    public int getY() {
        return event.getY();
    }

    public void setY(int y) {
        event.setY(y);
    }

    public boolean isBlinking() {
        return event.isBlinking();
    }

    public void setBlinking(boolean blinking) {
        event.setBlinking(blinking);
    }

    public boolean isHardcore() {
        return event.isHardcore();
    }

    public void setHardcore(boolean hardcore) {
        event.setHardcore(hardcore);
    }

    public CHeartType getHealthType() {
        return event.getHealthType();
    }

    public void setHealthType(CHeartType healthType) {
        event.setHealthType(healthType);
    }

    public CHeartType getAbsorbingType() {
        return event.getAbsorbingType();
    }

    public void setAbsorbingType(CHeartType absorbingType) {
        event.setAbsorbingType(absorbingType);
    }

    @Override
    public boolean isCancelable() {
        return false;
    }
}
