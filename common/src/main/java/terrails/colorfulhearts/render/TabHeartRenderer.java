package terrails.colorfulhearts.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.PlayerTabOverlay;
import net.minecraft.util.Mth;
import terrails.colorfulhearts.LoaderExpectPlatform;
import terrails.colorfulhearts.heart.CHeartType;
import terrails.colorfulhearts.heart.Heart;

import java.util.Arrays;

import static terrails.colorfulhearts.CColorfulHearts.LOGGER;

public class TabHeartRenderer {

    public static final TabHeartRenderer INSTANCE = new TabHeartRenderer();

    private final Minecraft client = Minecraft.getInstance();

    private Heart[] hearts;
    private int lastHealth, lastDisplayHealth;

    public void renderPlayerListHud(int y, int x, int offset, GuiGraphics guiGraphics, int health, PlayerTabOverlay.HealthState healthState) {
        if (health != this.lastHealth || healthState.displayedValue() != this.lastDisplayHealth || this.hearts == null) {
            // Use higher value to calculate hearts
            int value = Math.max(health, healthState.displayedValue());
            // Fixed maxHealth value as it is not possible to attain it via the leaderboard.
            this.hearts = Heart.calculateHearts(0, value, value, CHeartType.HEALTH, CHeartType.ABSORBING, false);
            this.lastHealth = health;
            this.lastDisplayHealth = healthState.displayedValue();
            LOGGER.debug("Successfully updated tab hearts.\n{}", Arrays.toString(this.hearts));
        }

        health = Math.min(health, 20);
        int displayHealth = Math.min(healthState.displayedValue(), 20);
        int displayHealthHearts = Mth.ceil(displayHealth / 2.0);

        boolean blinking = healthState.isBlinking(this.client.gui.getGuiTicks());

        int spacingDivisor = Math.max(health, displayHealth) / 2;
        // Adds space between hearts when there are less than 10
        int spacingMultiplier = Mth.floor(Math.min((float) (offset - x - 4) / (float) spacingDivisor, 9.0F));

        boolean hardcore = LoaderExpectPlatform.forcedHardcoreHearts() || (this.client.level != null && this.client.level.getLevelData().isHardcore());

        for (int i = 0; i < this.hearts.length; i++) {
            Heart heart = this.hearts[i];
            if (heart == null) continue;

            int xPos = x + (i % 10) * spacingMultiplier;

            // Vanilla seems to highlight all background borders and only some hearts,
            // therefore both should be passed through to draw
            boolean blinkingHeart = blinking && i < displayHealthHearts;

            heart.draw(guiGraphics, xPos, y, hardcore, blinking, blinkingHeart);
        }
    }
}
