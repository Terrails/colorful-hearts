package terrails.colorfulhearts.heart;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import terrails.colorfulhearts.CColorfulHearts;
import terrails.colorfulhearts.LoaderExpectPlatform;
import terrails.colorfulhearts.config.Configuration;
import terrails.colorfulhearts.render.RenderUtils;

import java.util.*;

import static terrails.colorfulhearts.CColorfulHearts.LOGGER;

public class Heart {

    /** Set with all created {@link Heart} objects
     * in order to avoid creating multiple objects with same values.
     */
    private static final Set<Heart> HEART_CACHE;

    /** Two predefined background/container hearts that should always be present and easier to access. */
    public static final Heart CONTAINER_FULL, CONTAINER_HALF;

    /** Heart background/container that is always present, either full or halved. */
    private final boolean isBackgroundFull;

    /** First half of a heart. This value may never be null outside the two predefined instances.
     * @see #CONTAINER_FULL
     * @see #CONTAINER_HALF
     */
    private final HeartPiece firstHalf;
    /** Second half of a heart. May be null, equal or different from firstHalf. */
    private final HeartPiece secondHalf;

    private Heart(boolean isBackgroundFull, HeartPiece firstHalf, HeartPiece secondHalf) {
        this.isBackgroundFull = isBackgroundFull;
        this.firstHalf = firstHalf;
        this.secondHalf = secondHalf;
    }

    static {
        HEART_CACHE = new HashSet<>();
        CONTAINER_FULL = new Heart(true, null, null);
        CONTAINER_HALF = new Heart(false, null, null);
    }

    public static Heart full(HeartPiece firstHalf, HeartPiece secondHalf) {
        if (firstHalf == null || secondHalf == null) {
            LOGGER.error("Something went very wrong with heart creation. HeartPiece cannot be null, returning heart container to prevent crashes...");
            return CONTAINER_FULL;
        }
        Optional<Heart> optional = HEART_CACHE.stream()
                .filter(heart -> heart.isBackgroundFull && Objects.equals(heart.firstHalf, firstHalf) && Objects.equals(heart.secondHalf, secondHalf))
                .findAny();

        return optional.orElseGet(() -> {
            Heart heart = new Heart(true, firstHalf, secondHalf);
            HEART_CACHE.add(heart);
            return heart;
        });
    }

    public static Heart full(HeartPiece heartPiece) {
        if (heartPiece == null) {
            LOGGER.error("Something went very wrong with heart creation. HeartPiece cannot be null, returning heart container to prevent crashes...");
            return CONTAINER_FULL;
        }

        Optional<Heart> optional = HEART_CACHE.stream()
                .filter(heart -> heart.isBackgroundFull && Objects.equals(heart.firstHalf, heartPiece) && Objects.equals(heart.secondHalf, heartPiece))
                .findAny();

        return optional.orElseGet(() -> {
            Heart heart = new Heart(true, heartPiece, heartPiece);
            HEART_CACHE.add(heart);
            return heart;
        });
    }

    public static Heart half(HeartPiece heartPiece, boolean isBackgroundFull) {
        if (heartPiece == null) {
            LOGGER.error("Something went very wrong with heart creation. HeartPiece cannot be null, returning heart container to prevent crashes...");
            return (isBackgroundFull ? CONTAINER_FULL : CONTAINER_HALF);
        }

        Optional<Heart> optional = HEART_CACHE.stream()
                .filter(heart -> heart.isBackgroundFull == isBackgroundFull && Objects.equals(heart.firstHalf, heartPiece) && heart.secondHalf == null)
                .findAny();

        return optional.orElseGet(() -> {
            Heart heart = new Heart(isBackgroundFull, heartPiece, null);
            HEART_CACHE.add(heart);
            return heart;
        });
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj != null && this.getClass() == obj.getClass()) {
            Heart heart = (Heart) obj;
            return this.isBackgroundFull == heart.isBackgroundFull && Objects.equals(this.firstHalf, heart.firstHalf) && Objects.equals(this.secondHalf, heart.secondHalf);
        } else return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.isBackgroundFull, this.firstHalf, this.secondHalf);
    }

    @Override
    public String toString() {
        return "{" +
                "isBackgroundFull=" + isBackgroundFull +
                ", firstHalf=" + firstHalf +
                ", secondHalf=" + secondHalf +
                "}";
    }

    public void draw(PoseStack poseStack, int xPos, int yPos, boolean blinkBackground, boolean blinkHeart, HeartType type) {
        final Minecraft client = Minecraft.getInstance();
        boolean hardcore = LoaderExpectPlatform.forcedHardcoreHearts() || (client.level != null && client.level.getLevelData().isHardcore());
        boolean canBlink = Configuration.ABSORPTION.renderOverHealth.get() || (this.firstHalf == null || !this.firstHalf.isAbsorption());

        // Draw background/container
        if (this.isBackgroundFull) {
            RenderSystem.setShaderTexture(0, CColorfulHearts.GUI_ICONS_LOCATION);
            RenderUtils.drawTexture(poseStack, xPos, yPos, 16 + (canBlink && blinkBackground ? 9 : 0), hardcore ? 45 : 0);
        } else {
            RenderSystem.setShaderTexture(0, CColorfulHearts.HALF_HEART_ICONS_LOCATION);
            RenderUtils.drawTexture(poseStack, xPos, yPos, 0, (canBlink && blinkBackground ? 9 : 0));
            RenderSystem.setShaderTexture(0, CColorfulHearts.GUI_ICONS_LOCATION);
        }

        if (this.firstHalf != null) {

            if (this.secondHalf == null) {
                // If second half is null draw only the first half
                this.firstHalf.draw(poseStack, xPos, yPos, blinkHeart, hardcore, type, true);
            } else if (Objects.equals(this.firstHalf, this.secondHalf)) {
                // If halves are equal draw one whole heart
                this.firstHalf.draw(poseStack, xPos, yPos, blinkHeart, hardcore, type);
            } else {
                // If halves are not equal render the first and second half separately
                this.firstHalf.draw(poseStack, xPos, yPos, blinkHeart, hardcore, type, true);
                this.secondHalf.draw(poseStack, xPos, yPos, blinkHeart, hardcore, type, false);
            }
        }
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
    public static Heart[] calculateHearts(int absorption, int health, int maxHealth, List<HeartPiece> healthColors, List<HeartPiece> absorptionColors, boolean absorptionSameRow) {
        final HeartPiece[] // Indices: 0 == Top, 1 == Bottom
                healthPieces,
                absorptionPieces;

        // Index for bottom color
        final int healthColorIndex = Math.max((int) (health / 20.0) % healthColors.size() - 1, 0);
        // Can there be a top color?
        final boolean hasTopHealth = (health % 20 != 0 && health > 20);

        healthPieces = new HeartPiece[]{
                hasTopHealth ? healthColors.get((healthColorIndex + 1) % healthColors.size()) : null,
                healthColors.get(healthColorIndex)
        };

        // Usually there are only 10 absorption hearts, but there is a special case when there are more (when there are less than 10 health hearts)
        final int maxAbsorptionHearts = absorptionSameRow ? 20 : (maxHealth >= 19 ? 20 : 40 - maxHealth - (maxHealth % 2));

        // Index for bottom color
        final int absorptionColorIndex = Math.max((absorption / maxAbsorptionHearts) % absorptionColors.size() - 1, 0);
        // Can there be a top color?
        final boolean hasTopAbsorption = (absorption % maxAbsorptionHearts != 0 && absorption > maxAbsorptionHearts);

        absorptionPieces = new HeartPiece[]{
                hasTopAbsorption ? absorptionColors.get((absorptionColorIndex + 1) % absorptionColors.size()) : null,
                absorptionColors.get(absorptionColorIndex)
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
                        hearts.add(Heart.full(absorptionPieces[1]));
                        overflownAbsorptionCount -= 2;
                    } else {
                        if (topHealthCount > 0) {
                            hearts.add(Heart.full(absorptionPieces[1], healthPieces[0]));
                            overflownAbsorptionCount--;
                            topHealthCount--;
                        } else if (bottomHealthCount > 0) {
                            hearts.add(Heart.full(absorptionPieces[1], healthPieces[1]));
                            overflownAbsorptionCount--;
                            bottomHealthCount--;
                        } else {
                            hearts.add(Heart.half(absorptionPieces[1], true));
                            overflownAbsorptionCount--;
                            emptyHealthCount--;
                        }
                    }
                } else if (topHealthCount > 0) {
                    if (topHealthCount > 1) {
                        hearts.add(Heart.full(healthPieces[0]));
                        topHealthCount -= 2;
                    } else {
                        hearts.add(Heart.full(healthPieces[0], absorptionPieces[1]));
                        topHealthCount--;
                        bottomAbsorptionCount--;
                    }
                } else if (health >= 20) {
                    if (bottomAbsorptionCount > 0) {
                        if (bottomAbsorptionCount > 1) {
                            hearts.add(Heart.full(absorptionPieces[1]));
                            bottomAbsorptionCount -= 2;
                        } else {
                            if (bottomHealthCount > 0) {
                                hearts.add(Heart.full(absorptionPieces[1], healthPieces[1]));
                                bottomAbsorptionCount--;
                                bottomHealthCount--;
                            } else if (emptyHealthCount > 0) {
                                hearts.add(Heart.half(absorptionPieces[1], true));
                                bottomAbsorptionCount--;
                                emptyHealthCount--;
                            }
                        }
                    } else if (bottomHealthCount > 0) {
                        hearts.add(Heart.full(healthPieces[1]));
                        bottomHealthCount -= 2;
                    }
                } else {
                    if (bottomHealthCount > 0) {
                        if (bottomHealthCount > 1) {
                            hearts.add(Heart.full(healthPieces[1]));
                            bottomHealthCount -= 2;
                        } else if (bottomAbsorptionCount > 0) {
                            hearts.add(Heart.full(healthPieces[1], absorptionPieces[1]));
                            bottomHealthCount--;
                            bottomAbsorptionCount--;
                        } else if (emptyHealthCount > 0) {
                            hearts.add(Heart.half(healthPieces[1], true));
                            bottomHealthCount--;
                            emptyHealthCount--;
                        } else {
                            hearts.add(Heart.half(healthPieces[1], false));
                            bottomHealthCount--;
                        }
                    } else if (bottomAbsorptionCount > 0) {
                        if (bottomAbsorptionCount > 1) {
                            hearts.add(Heart.full(absorptionPieces[1]));
                            bottomAbsorptionCount -= 2;
                        } else if (emptyHealthCount > 0) {
                            hearts.add(Heart.half(absorptionPieces[1], true));
                            bottomAbsorptionCount--;
                            emptyHealthCount--;
                        } else {
                            hearts.add(Heart.half(absorptionPieces[1], false));
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
                        hearts.add(Heart.full(healthPieces[0]));
                        topHealthCount -= 2;
                    } else {
                        hearts.add(Heart.full(healthPieces[0], healthPieces[1]));
                        topHealthCount--;
                        bottomHealthCount--;
                    }
                } else if (bottomHealthCount > 0) {
                    if (bottomHealthCount > 1) {
                        hearts.add(Heart.full(healthPieces[1]));
                        bottomHealthCount -= 2;
                    } else if (emptyHealthCount > 0) {
                        hearts.add(Heart.half(healthPieces[1], true));
                        bottomHealthCount--;
                        emptyHealthCount--;
                    } else {
                        hearts.add(Heart.half(healthPieces[1], false));
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
                        hearts.add(Heart.full(absorptionPieces[0]));
                        topAbsorptionCount -= 2;
                    } else {
                        hearts.add(Heart.full(absorptionPieces[0], absorptionPieces[1]));
                        topAbsorptionCount--;
                        bottomAbsorptionCount--;
                    }
                } else if (bottomAbsorptionCount > 0) {
                    if (bottomAbsorptionCount > 1) {
                        hearts.add(Heart.full(absorptionPieces[1]));
                        bottomAbsorptionCount -= 2;
                    } else {
                        hearts.add(Heart.half(absorptionPieces[1], false));
                        bottomAbsorptionCount--;
                    }
                }
            }
        }

        return hearts.toArray(Heart[]::new);
    }
}
