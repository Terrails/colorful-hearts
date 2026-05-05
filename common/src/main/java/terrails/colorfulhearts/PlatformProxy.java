package terrails.colorfulhearts;

import terrails.colorfulhearts.api.event.HeartRegistry;
import terrails.colorfulhearts.api.event.HeartRenderEvent;
import terrails.colorfulhearts.api.heart.drawing.Heart;
import terrails.colorfulhearts.api.heart.drawing.OverlayHeart;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;

public interface PlatformProxy {

    /**
     * Returns the lowercase name of current modloader
     *
     * @return modloader name
     */
    String getLoader();

    /**
     * Applies changes to night-config's FileConfig
     * Technically not needed if autosave were to be enabled
     */
    void applyConfig();

    /**
     * A way for other mods to force usage of hardcore hearts
     * Currently only possible with Fabric via ObjectShare
     *
     * @return if hardcore textures should be used even if not in a hardcore world
     */
    boolean forcedHardcoreHearts();

    /**
     * Event to register custom hearts. Current use is for overlay type hearts
     *
     * @param registry heart registry
     */
    void heartRegistryEvent(HeartRegistry registry);

    /**
     * Called before health renderer draws anything on screen
     *
     * @param guiGraphics   used by renderer
     * @param player        non-null client player
     * @param x             final position of the hearts
     * @param y             final position of the hearts
     * @param maxHealth     player max health given to the renderer
     * @param currentHealth player health given to the renderer
     * @param displayHealth player health that should blink
     * @param absorption    player absorption given to the renderer
     * @param blinking      eligible hearts should blink
     * @param hardcore      hearts should be of hardcore type
     * @param overlayHeart  type of overlay heart, null otherwise
     * @return the event with modified values
     */
    HeartRenderEvent.Pre preRenderEvent(GuiGraphics guiGraphics, Player player, int x, int y, int maxHealth, int currentHealth, int displayHealth, int absorption, boolean blinking, boolean hardcore, OverlayHeart overlayHeart);

    /**
     * A way to call the PlayerHeartTypeEvent from NeoForge to be able to change the heart type to a different effect.
     *
     * @param player       non-null client player
     * @param overlayHeart the default overlay heart that will be used if no override is done
     * @return the new overlay heart if it has changed
     */
    OverlayHeart playerHeartTypeEvent(Player player, OverlayHeart overlayHeart);

    /**
     * Called after health renderer finishes drawing
     *
     * @param guiGraphics   used by renderer
     * @param player        non-null client player
     * @param x             final position of the hearts
     * @param y             final position of the hearts
     * @param maxHealth     player max health given to the renderer
     * @param currentHealth player health given to the renderer
     * @param displayHealth player health that should blink
     * @param absorption    player absorption given to the renderer
     * @param blinking      eligible hearts blink
     * @param hardcore      hearts are of hardcore type
     * @param overlayHeart  type of overlay heart, null otherwise
     */
    void postRenderEvent(GuiGraphics guiGraphics, Player player, int x, int y, int maxHealth, int currentHealth, int displayHealth, int absorption, boolean blinking, boolean hardcore, OverlayHeart overlayHeart);

    /**
     * Called after a single heart icon finishes drawing
     *
     * @param heart         the heart that was rendered
     * @param guiGraphics   used by renderer
     * @param index         the index of the drawn heart. Usually 0-9 is health and 10-19 absorption (can be different if max health is lower)
     * @param x             position of the heart
     * @param y             position of the heart
     * @param hardcore      is hardcore enabled
     * @param blinking      blinking flag, usually means that the heart background is blinking/white
     * @param blinkingHeart heart blinking flag, usually means that the heart itself is blinking by being drawn as a lighter color
     */
    void singleRenderEvent(Heart heart, GuiGraphics guiGraphics, int index, int x, int y, boolean hardcore, boolean blinking, boolean blinkingHeart);

    /**
     * Just an empty event used to notify about in-game changes from the Config Screen
     */
    void heartUpdateEvent();
}
