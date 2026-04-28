package terrails.colorfulhearts.api.heart.drawing;

import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import org.lwjgl.opengl.GL11;
import terrails.colorfulhearts.api.heart.Hearts;

import java.util.List;
import java.util.function.Predicate;

public class OverlayHeart {

    private final Identifier id;
    private final Predicate<Player> condition;
    private final List<HeartDrawing> healthDrawings, absorptionDrawings;
    private final boolean opaque;

    OverlayHeart(Identifier id, Predicate<Player> condition, HeartDrawing h1, HeartDrawing h2, HeartDrawing a1, HeartDrawing a2, boolean opaque) {
        this.id = id;
        this.condition = condition;
        this.healthDrawings = h1 == null || h2 == null ? List.of() : List.of(h1, h2);
        this.absorptionDrawings = a1 == null || a2 == null ? List.of() : List.of(a1, a2);
        this.opaque = opaque;
    }

    public boolean shouldDraw(Player player) {
        return this.condition.test(player);
    }

    public List<HeartDrawing> getHealthDrawings() {
        return this.healthDrawings;
    }

    public List<HeartDrawing> getAbsorptionDrawings() {
        return this.absorptionDrawings;
    }

    public Identifier getId() {
        return this.id;
    }

    public boolean isOpaque() {
        return this.opaque;
    }

    @Override
    public String toString() {
        return "OverlayHeart{" +
                "id=" + id +
                ", healthDrawings=" + healthDrawings +
                ", absorptionDrawings=" + absorptionDrawings +
                '}';
    }

    /**
     * Creates an instance of {@link Builder} to create an instance of {@link OverlayHeart}
     * @param id easily distinguishable ID useful for debugging
     * @param condition for this heart type to be active
     */
    public static Builder build(Identifier id, Predicate<Player> condition) {
        return new Builder(id, condition);
    }

    public static class Builder {

        final Identifier id;
        final Predicate<Player> condition;

        HeartDrawing healthFirst, healthSecond;
        HeartDrawing absorptionFirst, absorptionSecond;
        boolean opaque;

        Builder(Identifier id, Predicate<Player> condition) {
            this.id = id;
            this.condition = condition;
            this.opaque = true;
        }

        /**
         * Uses the provided {@link HeartDrawing} object for the first heart color and colors the same texture using the provided rgb values for the second heart color
         */
        public Builder addHealth(HeartDrawing drawing, float r, float g, float b) {
            return this.addHealth(drawing, r, g, b, 1.0f, GL11.GL_ONE, GL11.GL_SRC_COLOR);
        }

        /**
         * Uses the provided {@link HeartDrawing} object for the first heart color and colors the same texture using the provided rgba values and blending modes
         */
        public Builder addHealth(HeartDrawing drawing, float r, float g, float b, float alpha, int sourceFactor, int destinationFactor) {
            this.healthFirst = drawing;
            this.healthSecond = HeartDrawing.colorBlend(drawing, drawing.getId().withSuffix("_2"), r, g, b, alpha, sourceFactor, destinationFactor);;
            return this;
        }

        /**
         * Uses the provided {@link HeartDrawing} for both health icons
         */
        public Builder addHealth(HeartDrawing first, HeartDrawing second) {
            this.healthFirst = first;
            this.healthSecond = second;
            return this;
        }

        /**
         * Uses the provided {@link HeartDrawing} object for health
         */
        public Builder addHealth(HeartDrawing drawing) {
            this.healthFirst = this.healthSecond = drawing;
            return this;
        }

        /**
         * Uses the provided {@link HeartDrawing} object for the first heart color and colors the same texture using the provided rgb values for the second heart color
         */
        public Builder addAbsorption(HeartDrawing drawing, float r, float g, float b) {
            return this.addAbsorption(drawing, r, g, b, 1.0f, GL11.GL_ONE, GL11.GL_SRC_COLOR);
        }

        /**
         * Uses the provided {@link HeartDrawing} object for the first heart color and colors the same texture using the provided rgba values and blending modes
         */
        public Builder addAbsorption(HeartDrawing drawing, float r, float g, float b, float alpha, int sourceFactor, int destinationFactor) {
            this.absorptionFirst = drawing;
            this.absorptionSecond = HeartDrawing.colorBlend(drawing, drawing.getId().withSuffix("_2"), r, g, b, alpha, sourceFactor, destinationFactor);
            return this;
        }

        /**
         * Uses the provided {@link HeartDrawing} object for the first heart color and colors the same texture using the provided rgb values for the second heart color
         */
        public Builder addAbsorption(HeartDrawing first, HeartDrawing second) {
            this.absorptionFirst = first;
            this.absorptionSecond = second;
            return this;
        }

        /**
         * Uses the provided {@link HeartDrawing} object for absorption
         */
        public Builder addAbsorption(HeartDrawing drawing) {
            this.absorptionFirst = this.absorptionSecond = drawing;
            return this;
        }

        /**
         * Makes it so that absorption is replaced by empty containers when active
         */
        public Builder blankAbsorption() {
            this.absorptionFirst = Hearts.CONTAINER;
            this.absorptionSecond = Hearts.CONTAINER;
            return this;
        }

        /**
         * The given overlay is transparent and colored hearts below it should render normally
         */
        public Builder transparent() {
            this.opaque = false;
            return this;
        }

        /**
         * Finishes building the final overlay heart
         */
        public OverlayHeart finish() {
            if (this.healthFirst == null || this.healthSecond == null || this.absorptionFirst == null || this.absorptionSecond == null) {
                this.opaque = false;
            }

            return new OverlayHeart(this.id, this.condition, this.healthFirst, this.healthSecond, this.absorptionFirst, this.absorptionSecond, this.opaque);
        }
    }
}
