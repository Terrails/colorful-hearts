package terrails.colorfulhearts.heart;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import terrails.colorfulhearts.CColorfulHearts;
import terrails.colorfulhearts.config.Configuration;
import terrails.colorfulhearts.render.RenderUtils;

import java.util.*;
import java.util.stream.Collectors;

import static terrails.colorfulhearts.CColorfulHearts.LOGGER;

public class HeartPiece {

    /** Two predefined vanilla heart parts. */
    public static final HeartPiece VANILLA_HEALTH, VANILLA_ABSORPTION;

    /** Hexadecimal color in Integer form.
     * If null we can take it that we're a vanilla heart and render accordingly.
     * @see #VANILLA_HEALTH
     * @see #VANILLA_ABSORPTION
     */
    private final Integer color;

    private final boolean absorption;

    protected HeartPiece(int color, boolean absorption) {
        this.absorption = absorption;
        this.color = color;
    }

    private HeartPiece(boolean absorption) {
        this.absorption = absorption;
        this.color = null;
    }

    static {
        VANILLA_HEALTH = new HeartPiece(false);
        VANILLA_ABSORPTION = new HeartPiece(true);
    }

    public static HeartPiece custom(int color, boolean absorption) {
        return new HeartPiece(color, absorption);
    }

    public static HeartPiece custom(String hexColor, boolean absorption) {
        return custom(Integer.parseInt(hexColor.substring(1), 16), absorption);
    }

    public Integer getColor() {
        return this.color;
    }

    public boolean isAbsorption() {
        return absorption;
    }

    public boolean isVanilla() {
        return this.color == null;
    }

    public String getHexColor() {
        // Returns a hex value with 6 digits prefixed by #
        if (this.isVanilla()) {
            return "Vanilla";
        } else {
            return "#" + String.format("%06X", this.color).toUpperCase(Locale.ROOT);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj != null && this.getClass() == obj.getClass()) {
            HeartPiece part = (HeartPiece) obj;
            return Objects.equals(this.color, part.color) && this.absorption == part.absorption;
        } else return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.absorption, this.color);
    }

    @Override
    public String toString() {
        return (this.isAbsorption() ? "Absorption " : "Health ") + this.getHexColor();
    }

    public void draw(PoseStack poseStack, int xPos, int yPos, boolean blinking, boolean hardcore, HeartType type) {
        this.draw(poseStack, xPos, yPos, blinking, hardcore, type, Part.FULL);
    }

    public void draw(PoseStack poseStack, int xPos, int yPos, boolean blinking, boolean hardcore, HeartType type, boolean firstHalf) {
        this.draw(poseStack, xPos, yPos, blinking, hardcore, type, firstHalf ? Part.FIRST_HALF : Part.SECOND_HALF);
    }

    private void draw(PoseStack poseStack, int xPos, int yPos, boolean blinking, boolean hardcore, HeartType type, Part part) {
        xPos += part == Part.SECOND_HALF ? 5 : 0;

        int xTex = type.getX(part.getXOffset(), this.isVanilla(), this.isAbsorption(), blinking);

        assert Minecraft.getInstance().level != null;
        int yTex = hardcore ? (this.isVanilla() ? 45 : 36) : 0;

        int x2 = (part == Part.FULL || part == Part.FIRST_HALF) ? 9 : 5;
        int y2 = 9;

        if (this.isVanilla()) {
            RenderSystem.setShaderTexture(0, CColorfulHearts.GUI_ICONS_LOCATION);
            // Draw vanilla heart
            RenderUtils.drawTexture(poseStack, xPos, xPos + x2, yPos, yPos + y2, xTex, xTex + x2, yTex, yTex + y2);
        } else {
            RenderSystem.setShaderTexture(0, this.isAbsorption() ? CColorfulHearts.ABSORPTION_ICONS_LOCATION : CColorfulHearts.HEALTH_ICONS_LOCATION);

            // Draw colored heart
            RenderUtils.drawTexture(poseStack, xPos, xPos + x2, yPos, yPos + y2, xTex, xTex + x2, yTex, yTex + y2, this.getColor(), 255);

            // Add hardcore overlay / white dot
            yTex += y2;
            if (hardcore) {
                RenderUtils.drawTexture(poseStack, xPos, xPos + x2, yPos, yPos + y2, xTex, xTex + x2, yTex, yTex + y2, 255);
            } else {
                RenderUtils.drawTexture(poseStack, xPos, xPos + x2, yPos, yPos + y2, xTex, xTex + x2, yTex, yTex + y2, 255);
            }

            // Add shading / withered overlay
            yTex += y2;
            RenderUtils.drawTexture(poseStack, xPos, xPos + x2, yPos, yPos + y2, xTex, xTex + x2, yTex, yTex + y2, 255);

            // Add blinking
            if (blinking && !this.isAbsorption()) {
                yTex -= 2 * y2;
                RenderUtils.drawTexture(poseStack, xPos, xPos + x2, yPos, yPos + y2, xTex, xTex + x2, yTex, yTex + y2, type == HeartType.WITHERED ? 56 : 127);
            }

            RenderSystem.setShaderTexture(0, CColorfulHearts.GUI_ICONS_LOCATION);
        }
    }

    private enum Part {
        FULL(0),
        FIRST_HALF(9),
        SECOND_HALF(5);

        private final int x;

        Part(int x) {
            this.x = x;
        }

        public int getXOffset() {
            return x;
        }
    }

    /** Utility function */
    public static List<HeartPiece> getColorsFromConfig(List<? extends String> configValue, boolean isAbsorption, boolean isEffect) {
        List<HeartPiece> colors = new ArrayList<>();
        int valueCount = configValue.size();
        String typeName = (isAbsorption ? "Absorption" : "Health") + (isEffect ? " Effect" : "");

        boolean noValues = configValue.isEmpty();
        if (noValues) {
            String error = typeName + " colors not defined";
            LOGGER.error(error);
            throw new IllegalArgumentException(error);
        }

        boolean notEnoughAbsorptionEffectColours = isAbsorption && isEffect && valueCount != 2;
        if (notEnoughAbsorptionEffectColours) {
            String error = typeName + " colors must be empty or have 2 values.";
            LOGGER.error(error);
            throw new IllegalArgumentException(error);
        }

        // vanilla absorption texture
        if (!isEffect && isAbsorption && Configuration.ABSORPTION.vanillaHearts.get()) {
            colors.add(HeartPiece.VANILLA_ABSORPTION);
        }
        // vanilla health texture
        else if (!isEffect && !isAbsorption && Configuration.HEALTH.vanillaHearts.get()) {
            colors.add(HeartPiece.VANILLA_HEALTH);
        }
        // single custom effect color for health
        else if (isEffect && !isAbsorption && valueCount == 1) {
            colors.add(HeartPiece.VANILLA_HEALTH);
        }

        for (String value : configValue) {
            HeartPiece piece = HeartPiece.custom(value, isAbsorption);
            colors.add(piece);
        }

        return ImmutableList.copyOf(colors);
    }

    public static List<String> getColorList(List<HeartPiece> pieces) {
        return pieces.stream().filter(value -> !value.isVanilla()).map(HeartPiece::getHexColor).collect(Collectors.toList());
    }

    public static List<HeartPiece> getHeartPiecesForType(HeartType heartType, boolean absorption) {
        if (absorption) {
            return switch (heartType) {
                case NORMAL -> HeartPiece.getColorsFromConfig(Configuration.ABSORPTION.colors.get(), true, false);
                case POISONED -> HeartPiece.getColorsFromConfig(Configuration.ABSORPTION.poisonedColors.get(), true, true);
                case WITHERED -> HeartPiece.getColorsFromConfig(Configuration.ABSORPTION.witheredColors.get(), true, true);
                case FROZEN -> HeartPiece.getColorsFromConfig(Configuration.ABSORPTION.frozenColors.get(), true, true);
            };
        } else {
            return switch (heartType) {
                case NORMAL -> HeartPiece.getColorsFromConfig(Configuration.HEALTH.colors.get(), false, false);
                case POISONED -> HeartPiece.getColorsFromConfig(Configuration.HEALTH.poisonedColors.get(), false, true);
                case WITHERED -> HeartPiece.getColorsFromConfig(Configuration.HEALTH.witheredColors.get(), false, true);
                case FROZEN -> HeartPiece.getColorsFromConfig(Configuration.HEALTH.frozenColors.get(), false, true);
            };
        }
    }

}
