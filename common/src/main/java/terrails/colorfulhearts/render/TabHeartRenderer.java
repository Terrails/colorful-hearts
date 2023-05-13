package terrails.colorfulhearts.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.util.Mth;
import terrails.colorfulhearts.heart.Heart;
import terrails.colorfulhearts.heart.HeartPiece;
import terrails.colorfulhearts.heart.HeartType;

import java.util.Arrays;
import java.util.List;

import static terrails.colorfulhearts.CColorfulHearts.LOGGER;

public class TabHeartRenderer {

    public static final TabHeartRenderer INSTANCE = new TabHeartRenderer();

    private final Minecraft client = Minecraft.getInstance();

    private Heart[] hearts;
    private int lastHealth, lastDisplayHealth;

    public void renderPlayerListHud(int y, int x, int offset, PoseStack poseStack, int health, PlayerInfo playerInfo) {
        if (health != this.lastHealth || playerInfo.getDisplayHealth() != this.lastDisplayHealth || this.hearts == null) {
            // Use higher value to calculate hearts
            int value = Math.max(health, playerInfo.getDisplayHealth());
            // Fixed maxHealth value as it is not possible to attain it via the leaderboard.
            List<HeartPiece> healthColors = HeartPiece.getHeartPiecesForType(HeartType.NORMAL, false);
            List<HeartPiece> absorptionColors = HeartPiece.getHeartPiecesForType(HeartType.NORMAL, true);
            this.hearts = Heart.calculateHearts(0, value, value, healthColors, absorptionColors, false);
            this.lastHealth = health;
            this.lastDisplayHealth = playerInfo.getDisplayHealth();
            LOGGER.debug("Successfully updated tab hearts.\n{}", Arrays.toString(this.hearts));
        }

        health = Math.min(health, 20);
        int displayHealth = Math.min(playerInfo.getDisplayHealth(), 20);
        int displayHealthHearts = Mth.ceil(displayHealth / 2.0);

        boolean blinking = playerInfo.getHealthBlinkTime() > (long)this.client.gui.getGuiTicks() && (playerInfo.getHealthBlinkTime() - (long)this.client.gui.getGuiTicks()) / 3L % 2L == 1L;

        int spacingDivisor = Math.max(health, displayHealth) / 2;
        // Adds space between hearts when there are less than 10
        int spacingMultiplier = Mth.floor(Math.min((float) (offset - x - 4) / (float) spacingDivisor, 9.0F));

        RenderSystem.setShaderTexture(0, GuiComponent.GUI_ICONS_LOCATION);
        RenderSystem.enableBlend();
        for (int i = 0; i < this.hearts.length; i++) {
            Heart heart = this.hearts[i];
            if (heart == null) continue;

            int xPos = x + (i % 10) * spacingMultiplier;

            // Vanilla seems to highlight all background borders and only some hearts,
            // therefore both should be passed through to draw
            boolean blinkingHeart = blinking && i < displayHealthHearts;

            heart.draw(poseStack, xPos, y, blinking, blinkingHeart, HeartType.NORMAL);
        }
        RenderSystem.disableBlend();
    }
}
