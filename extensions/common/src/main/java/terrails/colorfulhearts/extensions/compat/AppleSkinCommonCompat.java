package terrails.colorfulhearts.extensions.compat;

import terrails.colorfulhearts.api.heart.Hearts;
import terrails.colorfulhearts.api.heart.drawing.Heart;
import terrails.colorfulhearts.api.heart.drawing.HeartDrawing;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;

import java.util.List;

public class AppleSkinCommonCompat {

    private final RandomSource random = RandomSource.create();

    protected int lastHealth, lastModifiedHealth;
    private Heart[] hearts;

    public void drawHealthOverlay(GuiGraphics guiGraphics, int x, int y, int absorbing, int health, int modifiedHealth, float alpha, boolean hardcore) {
        long tickCount = Minecraft.getInstance().gui.getGuiTicks();
        // synchronize random with vanilla
        this.random.setSeed(tickCount * 312871);

        if (this.hearts == null || this.lastHealth != health || this.lastModifiedHealth != modifiedHealth) {
            this.hearts = calculateHearts(health, modifiedHealth);
            this.lastHealth = health;
            this.lastModifiedHealth = modifiedHealth;
        }

        for (int index = 0; index < this.hearts.length; index++) {
            Heart heart = this.hearts[index];
            int xPos = x + (index % 10) * 8;
            int yPos = y - (index > 9 ? 10 : 0);

            // low health "shakiness"
            if (health + absorbing <= 4) {
                yPos += this.random.nextInt(2);
            }

            if (heart == null) continue;

            heart.draw(guiGraphics, xPos, yPos, hardcore, false, false, ARGB.colorFromFloat(alpha, 1.0f, 1.0f, 1.0f));
        }
    }

    private Heart[] calculateHearts(int health, int modifiedHealth) {
        List<HeartDrawing> healthDrawings = Hearts.COLORED_HEALTH_HEARTS;

        int healthDiff = modifiedHealth - health;
        final int topHealth = modifiedHealth > 20 ? modifiedHealth % 20 : 0;
        final int bottomIndex = Math.max(0, Mth.floor(modifiedHealth / 20.0) - 1) % healthDrawings.size();
        HeartDrawing[] healthColors = new HeartDrawing[]{
                healthDrawings.get((bottomIndex + 1) % healthDrawings.size()),
                healthDrawings.get(bottomIndex)
        };

        int currentTopHealth = healthDiff >= 20 ? 0 : Math.max(0, health % 20);
        int bottomHealth = Math.min(20, healthDiff + currentTopHealth);

        currentTopHealth -= 1; // subtract 1 in order to handle heart halves

        final int modifiedHearts = Mth.ceil(bottomHealth / 2.0);

        final Heart[] hearts = new Heart[modifiedHearts];
        for (int i = 0; i < modifiedHearts; i++) {
            int value = i * 2;

            if (value < topHealth) {
                boolean half = (value + 1) == topHealth;
                boolean includeBackground = value > currentTopHealth;
                Heart background = includeBackground ? Heart.full(healthColors[1], false, Heart.CONTAINER_NONE) : Heart.CONTAINER_NONE;

                if (half) {
                    hearts[i] = Heart.full(healthColors[0], background);
                } else {
                    // full heart with another colored background in order to mix colors correctly
                    hearts[i] = Heart.full(healthColors[0], false, background);
                }
            } else if (value < currentTopHealth) {
                // skips constructing any hearts that do not need to be
                hearts[i] = null;
            } else if (value < bottomHealth) {
                boolean half = (value + 1) == bottomHealth;

                if (half) {
                    hearts[i] = Heart.full(healthColors[1], true, Heart.CONTAINER_NONE);
                } else {
                    hearts[i] = Heart.full(healthColors[1], false, Heart.CONTAINER_NONE);
                }
            }
        }
        return hearts;
    }
}
