package terrails.colorfulhearts.heart;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class Heart {

    /**
     * Set with all created {@link Heart} objects
     * in order to avoid creating multiple objects with same values
     */
    private static final Set<Heart> CACHE = new HashSet<>();

    public static final Heart CONTAINER_FULL, CONTAINER_HALF;

    static {
        CONTAINER_FULL = new Heart(CHeartType.CONTAINER, null, false, null);
        CONTAINER_HALF = new Heart(CHeartType.CONTAINER, null, true, null);
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

    public static Heart full(@NotNull CHeartType heartType, Integer color, @NotNull Heart background) {
        // comparing backgroundHeart by just == should work since there will always be a single instance of that specific type
        return CACHE.stream()
                .filter(h -> h.half && Objects.equals(h.color, color) && Objects.equals(h.heartType, heartType) && h.backgroundHeart == background)
                .findAny()
                .orElseGet(() -> {
                    Heart heart = new Heart(heartType, color, true, background);
                    CACHE.add(heart);
                    return heart;
                });
    }

    public static Heart half(@NotNull CHeartType heartType, Integer color) {
        return CACHE.stream()
                .filter(h -> h.half && Objects.equals(h.color, color) && Objects.equals(h.heartType, heartType) && h.backgroundHeart == CONTAINER_HALF)
                .findAny()
                .orElseGet(() -> {
                    Heart heart = new Heart(heartType, color, true, CONTAINER_HALF);
                    CACHE.add(heart);
                    return heart;
                });
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

    public void draw(GuiGraphics guiGraphics, int x, int y, boolean hardcore, boolean highlightContainer, boolean highlightHeart) {
        if (this.backgroundHeart != null) this.backgroundHeart.draw(guiGraphics, x, y, hardcore, highlightContainer, highlightHeart);

        boolean highlight = this.isContainer() ? highlightContainer : highlightHeart;
        ResourceLocation spriteLocation = this.heartType.getSprite(hardcore, highlight, this.half, this.color, true);
        guiGraphics.blitSprite(spriteLocation, x, y, 9, 9);
    }

    /**
     * A very over-engineered way to render a heart bar
     * <p>
     * In case that health/absorption is some really high value I found it wasteful to draw rows of hearts over each other.
     * Instead, I decided to write this monstrosity that is supposed to calculate the position of each heart when health/absorption/heart-type state changes
     * <p>
     * It might be overwhelming at first, but I have tried to document what the code is supposed to do at relevant parts.
     *
     * @return An array of Heart objects. The array length is 20 at most, with first 10 elements being in health row and latter 10 in absorption row
     *          Only case where array length is 10 at most is when renderOverHealth config option is enabled as absorption has to be in the same row as health then
     */
    public static Heart[] calculateHearts(int absorption, int health, int maxHealth, CHeartType healthType, CHeartType absorbingType, boolean absorptionSameRow) {
        final Integer[] // Indices: 0 == Top, 1 == Bottom
                healthPieces,
                absorptionPieces;

        final Integer[] healthColors = healthType.getColors();
        final Integer[] absorbingColors = absorption > 0 ? absorbingType.getColors() : new Integer[2];
        assert healthColors != null && absorbingColors != null;

        // Index for bottom color
        final int healthColorIndex = Math.max((int) (health / 20.0) % healthColors.length - 1, 0);
        // Can there be a top color?
        final boolean hasTopHealth = (health % 20 != 0 && health > 20);

        healthPieces = new Integer[]{
                hasTopHealth ? healthColors[(healthColorIndex + 1) % healthColors.length] : 0,
                healthColors[healthColorIndex]
        };

        // Usually there are only 10 absorption hearts, but there is a special case when there are more (when there are less than 10 health hearts)
        final int maxAbsorptionHearts = absorptionSameRow ? 20 : (maxHealth >= 19 ? 20 : 40 - maxHealth - (maxHealth % 2));

        // Index for bottom color
        final int absorptionColorIndex = Math.max((absorption / maxAbsorptionHearts) % absorbingColors.length - 1, 0);
        // Can there be a top color?
        final boolean hasTopAbsorption = (absorption % maxAbsorptionHearts != 0 && absorption > maxAbsorptionHearts);

        absorptionPieces = new Integer[]{
                hasTopAbsorption ? absorbingColors[(absorptionColorIndex + 1) % absorbingColors.length] : 0,
                absorbingColors[absorptionColorIndex]
        };

        // Common counters
        int topHealthCount = health > 20 ? (health % 20) : 0;
        int bottomHealthCount = health <= 20 ? health : 20 - topHealthCount;
        int emptyHealthCount = health < 20 ? Math.min(20, maxHealth) - bottomHealthCount : 0;



        final List<Heart> hearts = new ArrayList<>();
        if (absorptionSameRow && absorption > 0 && absorption < 20) {
            /* Same row style:
             * 1. absorption == 0
             *     Go to vanilla render style in else block
             * 2. absorption >= 20
             *     Go to vanilla render style in else block and set health counters to 0.
             *       Results in absorption being in indices 0-9
             * 3. (maxHealth + absorption) <= 20
             *      Topmost Health -> Absorption -> Bottom health or empty containers
             * 4. (maxHealth + absorption) > 20
             *      Overflown Absorption -> Topmost Health -> Bottom Absorption
             */

            final int spaceAfterTopMostHealth = 20 - (health > 20 ? topHealthCount : bottomHealthCount);
            int overflownAbsorptionCount = absorption > spaceAfterTopMostHealth ? absorption - spaceAfterTopMostHealth : 0;
            int bottomAbsorptionCount = Math.min(absorption, spaceAfterTopMostHealth);

            emptyHealthCount = Math.max(0, emptyHealthCount - bottomAbsorptionCount);
            topHealthCount = Math.max(0, topHealthCount - overflownAbsorptionCount);
            bottomHealthCount = Math.max(0, bottomHealthCount - (health > 20 ? bottomAbsorptionCount : overflownAbsorptionCount));

            while ((topHealthCount + bottomHealthCount + overflownAbsorptionCount + bottomAbsorptionCount + emptyHealthCount) > 0) {

                if (overflownAbsorptionCount > 0) {
                    if (overflownAbsorptionCount > 1) {
                        hearts.add(Heart.full(absorbingType, absorptionPieces[1]));
                        overflownAbsorptionCount -= 2;
                    } else {
                        if (topHealthCount > 0) {
                            hearts.add(Heart.full(absorbingType, absorptionPieces[1], Heart.full(healthType, healthPieces[0])));
                            overflownAbsorptionCount--;
                            topHealthCount--;
                        } else if (bottomHealthCount > 0) {
                            hearts.add(Heart.full(absorbingType, absorptionPieces[1], Heart.full(healthType, healthPieces[1])));
                            overflownAbsorptionCount--;
                            bottomHealthCount--;
                        } else {
                            hearts.add(Heart.full(absorbingType, absorptionPieces[1]));
                            overflownAbsorptionCount--;
                            emptyHealthCount--;
                        }
                    }
                } else if (topHealthCount > 0) {
                    if (topHealthCount > 1) {
                        hearts.add(Heart.full(healthType, healthPieces[0]));
                        topHealthCount -= 2;
                    } else {
                        hearts.add(Heart.full(healthType, healthPieces[0], Heart.full(absorbingType, absorptionPieces[1])));
                        topHealthCount--;
                        bottomAbsorptionCount--;
                    }
                } else if (health >= 20) {
                    if (bottomAbsorptionCount > 0) {
                        if (bottomAbsorptionCount > 1) {
                            hearts.add(Heart.full(absorbingType, absorptionPieces[1]));
                            bottomAbsorptionCount -= 2;
                        } else {
                            if (bottomHealthCount > 0) {
                                hearts.add(Heart.full(absorbingType, absorptionPieces[1], Heart.full(healthType, healthPieces[1])));
                                bottomAbsorptionCount--;
                                bottomHealthCount--;
                            } else if (emptyHealthCount > 0) {
                                hearts.add(Heart.full(absorbingType, absorptionPieces[1]));
                                bottomAbsorptionCount--;
                                emptyHealthCount--;
                            }
                        }
                    } else if (bottomHealthCount > 0) {
                        hearts.add(Heart.full(healthType, healthPieces[1]));
                        bottomHealthCount -= 2;
                    }
                } else {
                    if (bottomHealthCount > 0) {
                        if (bottomHealthCount > 1) {
                            hearts.add(Heart.full(healthType, healthPieces[1]));
                            bottomHealthCount -= 2;
                        } else if (bottomAbsorptionCount > 0) {
                            hearts.add(Heart.full(healthType, healthPieces[1], Heart.full(absorbingType, absorptionPieces[1])));
                            bottomHealthCount--;
                            bottomAbsorptionCount--;
                        } else if (emptyHealthCount > 0) {
                            hearts.add(Heart.full(healthType, healthPieces[1]));
                            bottomHealthCount--;
                            emptyHealthCount--;
                        } else {
                            hearts.add(Heart.half(healthType, healthPieces[1]));
                            bottomHealthCount--;
                        }
                    } else if (bottomAbsorptionCount > 0) {
                        if (bottomAbsorptionCount > 1) {
                            hearts.add(Heart.full(absorbingType, absorptionPieces[1]));
                            bottomAbsorptionCount -= 2;
                        } else if (emptyHealthCount > 0) {
                            hearts.add(Heart.full(absorbingType, absorptionPieces[1]));
                            bottomAbsorptionCount--;
                            emptyHealthCount--;
                        } else {
                            hearts.add(Heart.half(absorbingType, absorptionPieces[1]));
                            bottomAbsorptionCount--;
                        }
                    } else if (emptyHealthCount > 0) {
                        if (emptyHealthCount > 1) {
                            hearts.add(Heart.CONTAINER_FULL);
                            emptyHealthCount -= 2;
                        } else {
                            hearts.add(Heart.CONTAINER_HALF);
                            emptyHealthCount--;
                        }
                    }
                }
            }
        } else {
            /*
             * Vanilla render style:
             * 1. health >= 20 && absorption >= 0
             *     Health in indices 0-9, absorption in indices 10-19
             * 2. health < 19 && absorption > 0
             *     There is one full heart of space left in health row
             *     Health until N index (max 9), absorption goes from N+1 until 19
             *     [Due to this behaviour, there can be more than 10 absorption hearts]
             */

            int topAbsorptionCount = absorption > maxAbsorptionHearts ? (absorption % maxAbsorptionHearts) : 0;
            int bottomAbsorptionCount = absorption <= maxAbsorptionHearts ? absorption : maxAbsorptionHearts - topAbsorptionCount;

            // Explained in "Same row style 2." above
            if (absorptionSameRow && absorption >= 20) {
                topHealthCount = 0;
                bottomHealthCount = 0;
                emptyHealthCount = 0;
            }

            while ((topHealthCount + bottomHealthCount + emptyHealthCount + topAbsorptionCount + bottomAbsorptionCount) > 0) {

                if (topHealthCount > 0) {
                    if (topHealthCount > 1) {
                        hearts.add(Heart.full(healthType, healthPieces[0]));
                        topHealthCount -= 2;
                    } else {
                        hearts.add(Heart.full(healthType, healthPieces[0], Heart.full(healthType, healthPieces[1])));
                        topHealthCount--;
                        bottomHealthCount--;
                    }
                } else if (bottomHealthCount > 0) {
                    if (bottomHealthCount > 1) {
                        hearts.add(Heart.full(healthType, healthPieces[1]));
                        bottomHealthCount -= 2;
                    } else if (emptyHealthCount > 0) {
                        hearts.add(Heart.full(healthType, healthPieces[1], Heart.CONTAINER_FULL));
                        bottomHealthCount--;
                        emptyHealthCount--;
                    } else {
                        hearts.add(Heart.half(healthType, healthPieces[1]));
                        bottomHealthCount--;
                    }
                } else if (emptyHealthCount > 0) {
                    if (emptyHealthCount > 1) {
                        hearts.add(Heart.CONTAINER_FULL);
                        emptyHealthCount -= 2;
                    } else {
                        hearts.add(Heart.CONTAINER_HALF);
                        emptyHealthCount--;
                    }
                } else if (topAbsorptionCount > 0) {
                    if (topAbsorptionCount > 1) {
                        hearts.add(Heart.full(absorbingType, absorptionPieces[0]));
                        topAbsorptionCount -= 2;
                    } else {
                        hearts.add(Heart.full(absorbingType, absorptionPieces[0], Heart.full(absorbingType, absorptionPieces[1])));
                        topAbsorptionCount--;
                        bottomAbsorptionCount--;
                    }
                } else if (bottomAbsorptionCount > 0) {
                    if (bottomAbsorptionCount > 1) {
                        hearts.add(Heart.full(absorbingType, absorptionPieces[1]));
                        bottomAbsorptionCount -= 2;
                    } else {
                        hearts.add(Heart.half(absorbingType, absorptionPieces[1]));
                        bottomAbsorptionCount--;
                    }
                }
            }
        }

        return hearts.toArray(Heart[]::new);
    }
}
