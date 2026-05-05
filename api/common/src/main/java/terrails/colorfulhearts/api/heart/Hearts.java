package terrails.colorfulhearts.api.heart;

import terrails.colorfulhearts.api.heart.drawing.HeartDrawing;
import terrails.colorfulhearts.api.heart.drawing.OverlayHeart;
import terrails.colorfulhearts.api.heart.drawing.SpriteHeartDrawing;
import net.minecraft.client.gui.Gui;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class Hearts {

    public static final HeartDrawing CONTAINER = SpriteHeartDrawing.build(ResourceLocation.fromNamespaceAndPath("colorfulhearts", "container")).finish(
            ResourceLocation.withDefaultNamespace("hud/heart/container"), ResourceLocation.withDefaultNamespace("hud/heart/container_blinking"),
            ResourceLocation.fromNamespaceAndPath("colorfulhearts", "heart/container_half"), ResourceLocation.fromNamespaceAndPath("colorfulhearts", "heart/container_half_blinking"),
            ResourceLocation.withDefaultNamespace("hud/heart/container_hardcore"), ResourceLocation.withDefaultNamespace("hud/heart/container_hardcore_blinking"),
            ResourceLocation.fromNamespaceAndPath("colorfulhearts", "heart/container_hardcore_half"), ResourceLocation.fromNamespaceAndPath("colorfulhearts", "heart/container_hardcore_half_blinking")
    );

    public static List<HeartDrawing> COLORED_HEALTH_HEARTS;
    public static List<HeartDrawing> COLORED_ABSORPTION_HEARTS;
    public static Map<ResourceLocation, OverlayHeart> OVERLAY_HEARTS = new HashMap<>();

    public static final ResourceLocation POISON_OVERLAY_HEART_ID = ResourceLocation.withDefaultNamespace("poison");
    public static final ResourceLocation WITHER_OVERLAY_HEART_ID = ResourceLocation.withDefaultNamespace("wither");
    public static final ResourceLocation FROZEN_OVERLAY_HEART_ID = ResourceLocation.withDefaultNamespace("frozen");

    public static Optional<OverlayHeart> getOverlayHeartForPlayer(Player player) {
        return OVERLAY_HEARTS.values().stream().filter(heart -> heart.shouldDraw(player)).findFirst();
    }

    public static Optional<OverlayHeart> getOverlayHeartFromHeartType(Gui.HeartType heartType) {
        return switch (heartType) {
            case POISIONED -> Optional.ofNullable(OVERLAY_HEARTS.get(POISON_OVERLAY_HEART_ID));
            case WITHERED -> Optional.ofNullable(OVERLAY_HEARTS.get(WITHER_OVERLAY_HEART_ID));
            case FROZEN -> Optional.ofNullable(OVERLAY_HEARTS.get(FROZEN_OVERLAY_HEART_ID));
            default -> Optional.empty();
        };
    }

    public static Gui.HeartType getHeartTypeFromOverlayHeart(OverlayHeart heart) {
        ResourceLocation id = heart.getId();
        if (id.equals(POISON_OVERLAY_HEART_ID)) {
            return Gui.HeartType.POISIONED;
        } else if (id.equals(WITHER_OVERLAY_HEART_ID)) {
            return Gui.HeartType.WITHERED;
        } else if (id.equals(FROZEN_OVERLAY_HEART_ID)) {
            return Gui.HeartType.FROZEN;
        } else {
            return Gui.HeartType.NORMAL;
        }
    }
}
