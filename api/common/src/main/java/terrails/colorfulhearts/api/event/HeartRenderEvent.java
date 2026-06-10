package terrails.colorfulhearts.api.event;

import terrails.colorfulhearts.api.heart.drawing.OverlayHeart;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.entity.player.Player;

import java.util.Optional;

/**
 * A set of events useful to control the health renderer and render overlays
 */
public class HeartRenderEvent {

    /**
     * Called before health renderer draws anything on screen
     * Can be used to do any tweaks to the rendering
     */
    public static class Pre extends HeartRenderEvent {

        private boolean cancelled = false;

        public Pre(GuiGraphicsExtractor guiGraphics, Player player, int x, int y, int maxHealth, int currentHealth, int displayHealth, int absorption, boolean blinking, boolean hardcore, OverlayHeart overlayHeart) {
            super(guiGraphics, player, x, y, maxHealth, currentHealth, displayHealth, absorption, blinking, hardcore, overlayHeart);
        }

        public void setCancelled(boolean cancel) {
            this.cancelled = cancel;
        }

        public boolean isCancelled() {
            return this.cancelled;
        }

        public void setX(int x) {
            this.x = x;
        }

        public void setY(int y) {
            this.y = y;
        }

        public void setBlinking(boolean blinking) {
            this.blinking = blinking;
        }

        public void setHardcore(boolean hardcore) {
            this.hardcore = hardcore;
        }

        public void setOverlayHeart(OverlayHeart heart) {
            this.overlayHeart = heart;
        }
    }

    /**
     * Called after health renderer finishes drawing
     * Can be used to render additional overlays on top of health
     */
    public static class Post extends HeartRenderEvent {

        public Post(GuiGraphicsExtractor guiGraphics, Player player, int x, int y, int maxHealth, int currentHealth, int displayHealth, int absorption, boolean blinking, boolean hardcore, OverlayHeart overlayHeart) {
            super(guiGraphics, player, x, y, maxHealth, currentHealth, displayHealth, absorption, blinking, hardcore, overlayHeart);
        }
    }

    protected final GuiGraphicsExtractor guiGraphics;
    protected final Player player;
    protected int x, y;
    protected int maxHealth, health, displayHealth, absorption;
    protected boolean blinking, hardcore;
    protected OverlayHeart overlayHeart;

    public HeartRenderEvent(GuiGraphicsExtractor guiGraphics, Player player, int x, int y, int maxHealth, int currentHealth, int displayHealth, int absorption, boolean blinking, boolean hardcore, OverlayHeart overlayHeart) {
        this.guiGraphics = guiGraphics;
        this.player = player;
        this.x = x;
        this.y = y;
        this.maxHealth = maxHealth;
        this.health = currentHealth;
        this.displayHealth = displayHealth;
        this.absorption = absorption;
        this.blinking = blinking;
        this.hardcore = hardcore;
        this.overlayHeart = overlayHeart;
    }

    public GuiGraphicsExtractor getGuiGraphics() {
        return guiGraphics;
    }

    public Player getPlayer() {
        return player;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public int getHealth() {
        return health;
    }

    public int getDisplayHealth() {
        return displayHealth;
    }

    public int getAbsorption() {
        return absorption;
    }

    public boolean isBlinking() {
        return blinking;
    }

    public boolean isHardcore() {
        return hardcore;
    }

    public Optional<OverlayHeart> getOverlayHeart() {
        return Optional.ofNullable(overlayHeart);
    }
}
