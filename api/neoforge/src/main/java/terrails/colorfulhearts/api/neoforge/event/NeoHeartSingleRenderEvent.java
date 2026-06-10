package terrails.colorfulhearts.api.neoforge.event;

import net.neoforged.bus.api.Event;

import terrails.colorfulhearts.api.event.HeartSingleRenderEvent;
import terrails.colorfulhearts.api.heart.drawing.Heart;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public class NeoHeartSingleRenderEvent extends Event {

    final HeartSingleRenderEvent event;

    public NeoHeartSingleRenderEvent(Heart heart, GuiGraphicsExtractor guiGraphics, int index, int x, int y, boolean hardcore, boolean blinking, boolean blinkingHeart) {
        this.event = new HeartSingleRenderEvent(heart, guiGraphics, index, x, y, hardcore, blinking, blinkingHeart);
    }

    public HeartSingleRenderEvent getEvent() {
        return event;
    }

    public Heart getHeart() {
        return event.getHeart();
    }

    public GuiGraphicsExtractor getGuiGraphics() {
        return event.getGuiGraphics();
    }

    public int getIndex() {
        return event.getIndex();
    }

    public int getX() {
        return event.getX();
    }

    public int getY() {
        return event.getY();
    }

    public boolean isHardcoreEnabled() {
        return event.isHardcoreEnabled();
    }

    public boolean isBlinking() {
        return event.isBlinking();
    }

    public boolean isBlinkingHeart() {
        return event.isBlinkingHeart();
    }
}
