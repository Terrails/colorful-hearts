package terrails.colorfulhearts.api.heart;

import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import terrails.colorfulhearts.api.heart.drawing.HeartDrawing;
import terrails.colorfulhearts.api.heart.drawing.OverlayHeart;
import terrails.colorfulhearts.api.heart.drawing.SpriteHeartDrawing;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class Hearts {

    public static final HeartDrawing CONTAINER = SpriteHeartDrawing.build(Identifier.fromNamespaceAndPath("colorfulhearts", "container")).finish(
            Identifier.withDefaultNamespace("hud/heart/container"), Identifier.withDefaultNamespace("hud/heart/container_blinking"),
            Identifier.fromNamespaceAndPath("colorfulhearts", "heart/container_half"), Identifier.fromNamespaceAndPath("colorfulhearts", "heart/container_half_blinking"),
            Identifier.withDefaultNamespace("hud/heart/container_hardcore"), Identifier.withDefaultNamespace("hud/heart/container_hardcore_blinking"),
            Identifier.fromNamespaceAndPath("colorfulhearts", "heart/container_hardcore_half"), Identifier.fromNamespaceAndPath("colorfulhearts", "heart/container_hardcore_half_blinking")
    );

    public static List<HeartDrawing> COLORED_HEALTH_HEARTS;
    public static List<HeartDrawing> COLORED_ABSORPTION_HEARTS;
    public static Map<Identifier, OverlayHeart> OVERLAY_HEARTS = new HashMap<>();

    public static Optional<OverlayHeart> getOverlayHeartForPlayer(Player player) {
        return OVERLAY_HEARTS.values().stream().filter(heart -> heart.shouldDraw(player)).findFirst();
    }
}
