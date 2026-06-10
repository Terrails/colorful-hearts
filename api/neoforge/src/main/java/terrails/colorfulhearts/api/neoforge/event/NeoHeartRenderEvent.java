package terrails.colorfulhearts.api.neoforge.event;

import net.neoforged.bus.api.Event;

import terrails.colorfulhearts.api.event.HeartRenderEvent;
import terrails.colorfulhearts.api.heart.drawing.OverlayHeart;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.entity.player.Player;

import java.util.Optional;

public class NeoHeartRenderEvent<E extends HeartRenderEvent> extends Event {

    public static class Pre extends NeoHeartRenderEvent<HeartRenderEvent.Pre> {

        public Pre(GuiGraphicsExtractor guiGraphics, Player player, int x, int y, int maxHealth, int currentHealth, int displayHealth, int absorption, boolean blinking, boolean hardcore, OverlayHeart overlayHeart) {
            super(new HeartRenderEvent.Pre(guiGraphics, player, x, y, maxHealth, currentHealth, displayHealth, absorption, blinking, hardcore, overlayHeart));
        }

        public void setCancelled(boolean cancel) {
            event.setCancelled(cancel);
        }

        public boolean isCancelled() {
            return event.isCancelled();
        }

        public void setX(int x) {
            event.setX(x);
        }

        public void setY(int y) {
            event.setY(y);
        }

        public void setBlinking(boolean blinking) {
            event.setBlinking(blinking);
        }

        public void setHardcore(boolean hardcore) {
            event.setHardcore(hardcore);
        }

        public void setOverlayHeart(OverlayHeart heart) {
            event.setOverlayHeart(heart);
        }
    }

    public static class Post extends NeoHeartRenderEvent<HeartRenderEvent.Post> {

        public Post(GuiGraphicsExtractor guiGraphics, Player player, int x, int y, int maxHealth, int currentHealth, int displayHealth, int absorption, boolean blinking, boolean hardcore, OverlayHeart overlayHeart) {
            super(new HeartRenderEvent.Post(guiGraphics, player, x, y, maxHealth, currentHealth, displayHealth, absorption, blinking, hardcore, overlayHeart));
        }
    }

    final E event;

    public NeoHeartRenderEvent(E event) {
        this.event = event;
    }

    public E getEvent() {
        return this.event;
    }

    public GuiGraphicsExtractor getGuiGraphics() {
        return event.getGuiGraphics();
    }

    public Player getPlayer() {
        return event.getPlayer();
    }

    public int getX() {
        return event.getX();
    }

    public int getY() {
        return event.getY();
    }

    public int getMaxHealth() {
        return event.getMaxHealth();
    }

    public int getHealth() {
        return event.getHealth();
    }

    public int getDisplayHealth() {
        return event.getDisplayHealth();
    }

    public int getAbsorption() {
        return event.getAbsorption();
    }

    public boolean isBlinking() {
        return event.isBlinking();
    }

    public boolean isHardcore() {
        return event.isHardcore();
    }

    public Optional<OverlayHeart> getOverlayHeart() {
        return event.getOverlayHeart();
    }
}
