package terrails.colorfulhearts.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import terrails.colorfulhearts.LoaderExpectPlatform;
import terrails.colorfulhearts.api.event.HeartRenderEvent;
import terrails.colorfulhearts.heart.CHeartType;
import terrails.colorfulhearts.heart.Heart;

public class HeartRenderer {

    public static final HeartRenderer INSTANCE = new HeartRenderer();

    private final Minecraft client = Minecraft.getInstance();
    private final RandomSource random = RandomSource.create();

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
            boolean blinking) {

        long tickCount = this.client.gui.getGuiTicks();
        // synchronize random with vanilla
        this.random.setSeed(tickCount * 312871);

        int healthHearts = Mth.ceil(Math.min(maxHealth, 20) / 2.0);
        int displayHealthHearts = Mth.ceil(Math.min(displayHealth, 20) / 2.0);

        boolean hardcore = LoaderExpectPlatform.forcedHardcoreHearts() || (player.level().getLevelData().isHardcore());

        int regenIndex = -1;
        if (player.hasEffect(MobEffects.REGENERATION)) {
            regenIndex = (int) tickCount % Mth.ceil(Math.min(maxHealth, 20) + 5);
        }

        CHeartType healthType = CHeartType.forPlayer(player, true);
        CHeartType absorbingType = CHeartType.forPlayer(player, false);

        HeartRenderEvent.Pre event = LoaderExpectPlatform.preRenderEvent(guiGraphics, x, y, blinking, hardcore, healthType, absorbingType);
        if (event.isCancelled()) return;

        x = event.getX();
        y = event.getY();
        blinking = event.isBlinking();
        hardcore = event.isHardcore();
        healthType = event.getHealthType();
        absorbingType = event.getAbsorbingType();

        if (this.lastHardcore != hardcore || this.lastHealth != currentHealth || this.lastMaxHealth != maxHealth || this.lastAbsorption != absorption
                || this.lastHealthType != healthType || this.lastAbsorbingType != absorbingType || this.hearts == null) {
            this.hearts = Heart.calculateHearts(currentHealth, maxHealth, absorption, healthType, absorbingType);
            this.lastHardcore = hardcore;
            this.lastHealth = currentHealth;
            this.lastMaxHealth = maxHealth;
            this.lastAbsorption = absorption;
            this.lastHealthType = healthType;
            this.lastAbsorbingType = absorbingType;
        }

        for (int index = 0; index < this.hearts.length; index++) {
            Heart heart = this.hearts[index];
            int xPos = x + (index % 10) * 8;
            int yPos = y - (index > 9 ? 10 : 0);

            // low health "shakiness"
            if (currentHealth + absorption <= 4) {
                yPos += this.random.nextInt(2);
            }

            if (heart == null) continue;

            // move up and down while under regeneration status effect.
            if (index < healthHearts && index == regenIndex) {
                yPos -= 2;
            }

            // vanilla seems to highlight all background borders and only some hearts,
            // therefore both should be passed through to draw
            boolean blinkingHeart = blinking && index < displayHealthHearts;

            heart.draw(guiGraphics, xPos, yPos, hardcore, blinking, blinkingHeart);
        }

        LoaderExpectPlatform.postRenderEvent(guiGraphics, x, y, blinking, hardcore, healthType, absorbingType);
    }
}
