package terrails.colorfulhearts.neoforge.api.event;

import net.minecraft.client.gui.GuiGraphics;
import net.neoforged.bus.api.Event;
import terrails.colorfulhearts.api.event.HeartRenderEvent;
import terrails.colorfulhearts.heart.CHeartType;

/**
 * A set of events useful to render any overlays
 */
public class ForgeHeartRenderEvent<E extends HeartRenderEvent> extends Event {

    /**
     * Event executed before health renderer does anything
     */
    public static class Pre extends ForgeHeartRenderEvent<HeartRenderEvent.Pre> {

        public Pre(GuiGraphics guiGraphics,
                   int x, int y,
                   boolean blinking, boolean hardcore,
                   CHeartType healthType, CHeartType absorptionType
        ) {
            super(new HeartRenderEvent.Pre(guiGraphics, x, y, blinking, hardcore, healthType, absorptionType));
        }

        public void cancel() {
            event.cancel();
        }

        public boolean isCancelled() {
            return event.isCancelled();
        }
    }

    /**
     * Event executed after health renderer finished
     */
    public static class Post extends ForgeHeartRenderEvent<HeartRenderEvent.Post> {

        public Post(GuiGraphics guiGraphics,
                    int x, int y,
                    boolean blinking, boolean hardcore,
                    CHeartType healthType, CHeartType absorptionType
        ) {
            super(new HeartRenderEvent.Post(guiGraphics, x, y, blinking, hardcore, healthType, absorptionType));
        }
    }

    final E event;

    public ForgeHeartRenderEvent(E event) {
        this.event = event;
    }

    public E getEvent() {
        return this.event;
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
}
