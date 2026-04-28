package terrails.colorfulhearts.api.heart.drawing;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.jetbrains.annotations.NotNull;
import terrails.colorfulhearts.api.heart.Hearts;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Heart {

    /**
     * Set with all created {@link Heart} objects in order to avoid creating multiple objects with same values
     */
    private static final Set<Heart> CACHE = new HashSet<>();

    public static final Heart CONTAINER_FULL, CONTAINER_HALF, CONTAINER_NONE;

    static {
        CONTAINER_FULL = new Heart(Hearts.CONTAINER, false, null);
        CONTAINER_HALF = new Heart(Hearts.CONTAINER, true, null);
        CONTAINER_NONE = new Heart(null, false, null);
    }

    private final HeartDrawing drawing;
    private final boolean half;

    // used to draw background or heart a row under current
    private final Heart backgroundHeart;

    private Heart(HeartDrawing drawing, boolean half, Heart backgroundHeart) {
        this.drawing = drawing;
        this.half = half;
        this.backgroundHeart = backgroundHeart;
    }

    public static Heart full(@NotNull HeartDrawing drawing) {
        return CACHE.stream()
                .filter(h -> !h.half && Objects.equals(h.drawing, drawing) && h.backgroundHeart == CONTAINER_FULL)
                .findAny()
                .orElseGet(() -> {
                    Heart heart = new Heart(drawing, false, CONTAINER_FULL);
                    CACHE.add(heart);
                    return heart;
                });
    }

    public static Heart full(@NotNull HeartDrawing drawing, boolean half, @NotNull Heart background) {
        // comparing backgroundHeart by just == should work since there will always be a single instance of that specific type
        return CACHE.stream()
                .filter(h -> h.half == half && Objects.equals(h.drawing, drawing) && h.backgroundHeart == background)
                .findAny()
                .orElseGet(() -> {
                    Heart heart = new Heart(drawing, half, background);
                    CACHE.add(heart);
                    return heart;
                });
    }

    public static Heart full(@NotNull HeartDrawing drawing, @NotNull Heart background) {
        return full(drawing, true, background);
    }

    public static Heart half(@NotNull HeartDrawing drawing) {
        return full(drawing, true, CONTAINER_HALF);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Heart heart = (Heart) o;
        return this.half == heart.half && this.drawing == heart.drawing && this.backgroundHeart == heart.backgroundHeart;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.drawing, this.half, this.backgroundHeart);
    }

    public boolean isContainer() {
        return (this == CONTAINER_FULL || this == CONTAINER_HALF);
    }

    public boolean isEmpty() {
        return this == CONTAINER_NONE;
    }

    public void draw(GuiGraphicsExtractor guiGraphics, int x, int y, boolean hardcore, boolean highlightContainer, boolean highlightHeart) {
        if (this.isEmpty()) return;
        boolean hasBackground = this.backgroundHeart != null;
        if (hasBackground) {
            this.backgroundHeart.draw(guiGraphics, x, y, hardcore, highlightContainer, highlightHeart);
            // skip rendering HALF background on top of FULL background
            // special case for when hearts are set as blank in OverlayHeart
            if (this.drawing == Hearts.CONTAINER && this.half) return;
        }
        boolean highlight = this.isContainer() ? highlightContainer : highlightHeart;
        this.drawing.draw(guiGraphics, x, y, this.half, hardcore, highlight);
    }
}
