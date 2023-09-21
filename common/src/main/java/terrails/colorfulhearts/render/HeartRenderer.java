package terrails.colorfulhearts.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import terrails.colorfulhearts.config.Configuration;
import terrails.colorfulhearts.heart.Heart;
import terrails.colorfulhearts.heart.HeartPiece;
import terrails.colorfulhearts.heart.HeartType;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static terrails.colorfulhearts.CColorfulHearts.LOGGER;

public class HeartRenderer {

    public static final HeartRenderer INSTANCE = new HeartRenderer();

    private final Minecraft client = Minecraft.getInstance();
    private final Random random = new Random();

    private int lastHealth, lastMaxHealth, lastAbsorption;
    public HeartType lastHeartType;
    private Heart[] hearts;

    public void renderPlayerHearts(
            GuiGraphics guiGraphics,
            Player player,
            int x,
            int y,
            int maxHealth,
            int currentHealth,
            int displayHealth,
            int absorption,
            boolean renderHighlight) {

        int healthHearts = Mth.ceil(Math.min(maxHealth, 20) / 2.0);
        int displayHealthHearts = Mth.ceil(Math.min(displayHealth, 20) / 2.0);

        boolean absorptionSameRow = Configuration.ABSORPTION.renderOverHealth.get();

        int regenIndex = -1;
        if (player.hasEffect(MobEffects.REGENERATION)) {
            long tickCount = this.client.gui.getGuiTicks();
            // Count absorption into the index if in same row and if (health + absorption) > maxHealth
            if (absorptionSameRow && (absorption + Math.max(currentHealth, displayHealth)) > maxHealth) {
                // limit regeneration to 20 when in same row
                int value = Math.min(20, Math.max(currentHealth, displayHealth) + absorption);
                regenIndex = (int) tickCount % Mth.ceil(value + 5);
            } else {
                regenIndex = (int) tickCount % Mth.ceil(Math.min(maxHealth, 20) + 5);
            }
        }

        HeartType heartType = HeartType.forPlayer(player);
        if (this.lastHealth != currentHealth || this.lastMaxHealth != maxHealth || this.lastAbsorption != absorption || this.lastHeartType != heartType || this.hearts == null) {
            List<HeartPiece> healthColors = HeartPiece.getHeartPiecesForType(heartType, false);
            List<HeartPiece> absorptionColors = HeartPiece.getHeartPiecesForType(heartType, true);
            this.hearts = Heart.calculateHearts(absorption, currentHealth, maxHealth, healthColors, absorptionColors, absorptionSameRow);
            this.lastHealth = currentHealth;
            this.lastMaxHealth = maxHealth;
            this.lastAbsorption = absorption;
            this.lastHeartType = heartType;
            LOGGER.debug("Successfully updated hearts.\n{}", Arrays.toString(this.hearts));
        }

        RenderSystem.enableBlend();
        for (int index = 0; index < this.hearts.length; index++) {
            Heart heart = this.hearts[index];
            if (heart == null) continue;

            int xPos = x + (index % 10) * 8;
            int yPos = y - (index > 9 ? 10 : 0);

            // Low health "shakiness"
            if (currentHealth + absorption <= 4) {
                yPos += this.random.nextInt(2);
            }

            // Only health hearts should move up and down while under regeneration status effect.
            // This behavior is applied to absorption ONLY when absorptionSameRow is enabled
            if ((index < healthHearts || absorptionSameRow) && index == regenIndex) {
                yPos -= 2;
            }

            // Vanilla seems to highlight all background borders and only some hearts,
            // therefore both should be passed through to draw
            boolean highlightHeart = renderHighlight && index < displayHealthHearts;

            heart.draw(guiGraphics.pose(), xPos, yPos, renderHighlight, highlightHeart, heartType);
        }
        RenderSystem.disableBlend();
    }
}
