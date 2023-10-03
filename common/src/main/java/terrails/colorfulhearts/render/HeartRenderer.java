package terrails.colorfulhearts.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import terrails.colorfulhearts.LoaderExpectPlatform;
import terrails.colorfulhearts.config.Configuration;
import terrails.colorfulhearts.heart.CHeartType;
import terrails.colorfulhearts.heart.Heart;

import java.util.Random;

public class HeartRenderer {

    public static final HeartRenderer INSTANCE = new HeartRenderer();

    private final Minecraft client = Minecraft.getInstance();
    private final Random random = new Random();

    private boolean lastHardcore;
    private int lastHealth, lastMaxHealth, lastAbsorption;
    public CHeartType lastHealthType, lastAbsorbingType;
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
        boolean hardcore = LoaderExpectPlatform.forcedHardcoreHearts() || (player.level().getLevelData().isHardcore());

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

        CHeartType healthType = CHeartType.forPlayer(player, true);
        CHeartType absorbingType = CHeartType.forPlayer(player, false);
        if (this.lastHardcore != hardcore || this.lastHealth != currentHealth || this.lastMaxHealth != maxHealth || this.lastAbsorption != absorption
                || this.lastHealthType != healthType || this.lastAbsorbingType != absorbingType || this.hearts == null) {
            this.hearts = Heart.calculateHearts(absorption, currentHealth, maxHealth, healthType, absorbingType, absorptionSameRow);
            this.lastHardcore = hardcore;
            this.lastHealth = currentHealth;
            this.lastMaxHealth = maxHealth;
            this.lastAbsorption = absorption;
            this.lastHealthType = healthType;
            this.lastAbsorbingType = absorbingType;
        }

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

            heart.draw(guiGraphics, xPos, yPos, hardcore, renderHighlight, highlightHeart);
        }
    }
}
