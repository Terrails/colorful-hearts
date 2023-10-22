package terrails.colorfulhearts.heart;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class Heart {

    /**
     * Set with all created {@link Heart} objects
     * in order to avoid creating multiple objects with same values
     */
    private static final Set<Heart> CACHE = new HashSet<>();

    public static final Heart CONTAINER_FULL, CONTAINER_HALF, CONTAINER_NONE;

    static {
        CONTAINER_FULL = new Heart(CHeartType.CONTAINER, null, false, null);
        CONTAINER_HALF = new Heart(CHeartType.CONTAINER, null, true, null);
        CONTAINER_NONE = new Heart(null, null, false, null);
    }

    private final CHeartType heartType;
    private final Integer color;
    private final boolean half;

    // used to draw background or heart a row under current
    private final Heart backgroundHeart;

    private Heart(CHeartType heartType, Integer color, boolean half, Heart backgroundHeart) {
        this.heartType = heartType;
        this.color = color;
        this.half = half;
        this.backgroundHeart = backgroundHeart;
    }

    public static Heart full(@NotNull CHeartType heartType, Integer color) {
        return CACHE.stream()
                .filter(h -> !h.half && Objects.equals(h.color, color) && Objects.equals(h.heartType, heartType) && h.backgroundHeart == CONTAINER_FULL)
                .findAny()
                .orElseGet(() -> {
                    Heart heart = new Heart(heartType, color, false, CONTAINER_FULL);
                    CACHE.add(heart);
                    return heart;
                });
    }

    public static Heart full(@NotNull CHeartType heartType, Integer color, boolean half, @NotNull Heart background) {
        // comparing backgroundHeart by just == should work since there will always be a single instance of that specific type
        return CACHE.stream()
                .filter(h -> h.half == half && Objects.equals(h.color, color) && Objects.equals(h.heartType, heartType) && h.backgroundHeart == background)
                .findAny()
                .orElseGet(() -> {
                    Heart heart = new Heart(heartType, color, half, background);
                    CACHE.add(heart);
                    return heart;
                });
    }

    public static Heart full(@NotNull CHeartType heartType, Integer color, @NotNull Heart background) {
        return full(heartType, color, true, background);
    }

    public static Heart half(@NotNull CHeartType heartType, Integer color) {
        return full(heartType, color, true, CONTAINER_HALF);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Heart heart = (Heart) o;
        return this.half == heart.half && this.heartType == heart.heartType
                && Objects.equals(this.color, heart.color) && this.backgroundHeart == heart.backgroundHeart;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.heartType, this.color, this.half, this.backgroundHeart);
    }

    public boolean isContainer() {
        return (this == CONTAINER_FULL || this == CONTAINER_HALF);
    }

    public boolean isEmpty() {
        return this == CONTAINER_NONE;
    }

    public void draw(GuiGraphics guiGraphics, int x, int y, boolean hardcore, boolean highlightContainer, boolean highlightHeart) {
        if (this.isEmpty()) return;
        if (this.backgroundHeart != null) this.backgroundHeart.draw(guiGraphics, x, y, hardcore, highlightContainer, highlightHeart);

        boolean highlight = this.isContainer() ? highlightContainer : highlightHeart;
        ResourceLocation spriteLocation = this.heartType.getSprite(hardcore, highlight, this.half, this.color, true);
        guiGraphics.blitSprite(spriteLocation, x, y, 9, 9);
    }

    public static Heart[] calculateHearts(int health, int maxHealth, int absorbing, CHeartType healthType, CHeartType absorbingType) {
        Integer[] healthColors = healthType.getColors();
        Integer[] absorbingColors = absorbingType.getColors();
        assert healthColors != null && absorbingColors != null;

        final int topHealth = health > 20 ? health % 20 : 0;
        final int bottomHealthRow = Math.max(0, Mth.floor(health / 20.0f) - 1);
        final int healthColorIndex = bottomHealthRow % healthColors.length;
        healthColors = new Integer[]{
                healthColors[(healthColorIndex + 1) % healthColors.length],
                healthColors[healthColorIndex]
        };

        // usually there are only 10 absorption hearts, but there is a special case when there are more (when there are less than 10 health hearts)
        final int maxAbsorbing = maxHealth >= 19 ? 20 : 40 - maxHealth - (maxHealth % 2);

        final int topAbsorbing = absorbing > maxAbsorbing ? absorbing % maxAbsorbing : 0;
        final int bottomAbsorptionRow = Math.max(0, Mth.floor(absorbing / (float) maxAbsorbing) - 1);
        final int absorptionColorIndex = bottomAbsorptionRow % absorbingColors.length;
        absorbingColors = new Integer[]{
                absorbingColors[(absorptionColorIndex + 1) % absorbingColors.length],
                absorbingColors[absorptionColorIndex]
        };

        maxHealth = Math.min(20, maxHealth);
        absorbing = Math.min(maxAbsorbing, absorbing);

        // offset added to index in for loop below to render absorbing hearts at correct positions
        // needed in case where absorbing hearts are rendered in same row as health (when there are less than 10 health hearts)
        final int absorbingOffset = Math.min(10, Mth.ceil(maxHealth / 2.0));

        final int maxHealthHearts = Mth.ceil(maxHealth / 2.0);
        final int maxAbsorbingHearts = Mth.ceil(maxAbsorbing / 2.0);

        // first 10 elements are meant to be health and other 10 are meant to be absorption
        final Heart[] hearts = new Heart[20];
        for (int i = 0; i < Math.max(maxHealthHearts, maxAbsorbingHearts); i++) {
            int value = i * 2;

            if (value < topHealth) {
                boolean half = value + 1 == topHealth;

                if (half) {
                    hearts[i] = Heart.full(healthType, healthColors[0], Heart.full(healthType, healthColors[1]));
                } else {
                    hearts[i] = Heart.full(healthType, healthColors[0]);
                }
            } else if (value < health) {
                boolean halfBackground = value + 1 == maxHealth;
                boolean half = value + 1 == health;

                if (halfBackground) {
                    hearts[i] = Heart.half(healthType, healthColors[1]);
                } else if (half) {
                    hearts[i] = Heart.full(healthType, healthColors[1], CONTAINER_FULL);
                } else {
                    hearts[i] = Heart.full(healthType, healthColors[1]);
                }
            } else if (value < maxHealth) {
                boolean halfBackground = value + 1 == maxHealth;
                hearts[i] = halfBackground ? CONTAINER_HALF : CONTAINER_FULL;
            }

            if (value < topAbsorbing) {
                boolean half = value + 1 == topAbsorbing;

                if (half) {
                    hearts[i + absorbingOffset] = Heart.full(absorbingType, absorbingColors[0], Heart.full(absorbingType, absorbingColors[1]));
                } else {
                    hearts[i + absorbingOffset] = Heart.full(absorbingType, absorbingColors[0]);
                }
            } else if (value < absorbing) {
                boolean half = value + 1 == absorbing;

                if (half) {
                    hearts[i + absorbingOffset] = Heart.half(absorbingType, absorbingColors[1]);
                } else {
                    hearts[i + absorbingOffset] = Heart.full(absorbingType, absorbingColors[1]);
                }
            }
        }
        return hearts;
    }
}
