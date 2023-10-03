package terrails.colorfulhearts.heart;

import net.minecraft.client.gui.Gui;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import terrails.colorfulhearts.CColorfulHearts;
import terrails.colorfulhearts.Utils;
import terrails.colorfulhearts.config.Configuration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum CHeartType {
    CONTAINER(
            null, null,
            Utils.location("heart/container_half"), Utils.location("heart/container_half_blinking"),
            null, null,
            Utils.location("heart/container_hardcore_half"), Utils.location("heart/container_hardcore_half_blinking")
    ),
    HEALTH(
            Utils.location("heart/health/full"), Utils.location("heart/health/full_blinking"),
            Utils.location("heart/health/half"), Utils.location("heart/health/half_blinking"),
            Utils.location("heart/health/hardcore_full"), Utils.location("heart/health/hardcore_full_blinking"),
            Utils.location("heart/health/hardcore_half"), Utils.location("heart/health/hardcore_half_blinking")
    ),
    HEALTH_POISONED(
            Utils.location("heart/health/poisoned/full"), Utils.location("heart/health/poisoned/full_blinking"),
            Utils.location("heart/health/poisoned/half"), Utils.location("heart/health/poisoned/half_blinking"),
            Utils.location("heart/health/poisoned/hardcore_full"), Utils.location("heart/health/poisoned/hardcore_full_blinking"),
            Utils.location("heart/health/poisoned/hardcore_half"), Utils.location("heart/health/poisoned/hardcore_half_blinking")
    ),
    HEALTH_WITHERED(
            Utils.location("heart/health/withered/full"), Utils.location("heart/health/withered/full_blinking"),
            Utils.location("heart/health/withered/half"), Utils.location("heart/health/withered/half_blinking"),
            Utils.location("heart/health/withered/hardcore_full"), Utils.location("heart/health/withered/hardcore_full_blinking"),
            Utils.location("heart/health/withered/hardcore_half"), Utils.location("heart/health/withered/hardcore_half_blinking")
    ),
    HEALTH_FROZEN(
            Utils.location("heart/health/frozen/full"), Utils.location("heart/health/frozen/full_blinking"),
            Utils.location("heart/health/frozen/half"), Utils.location("heart/health/frozen/half_blinking"),
            Utils.location("heart/health/frozen/hardcore_full"), Utils.location("heart/health/frozen/hardcore_full_blinking"),
            Utils.location("heart/health/frozen/hardcore_half"), Utils.location("heart/health/frozen/hardcore_half_blinking")
    ),
    ABSORBING(
            Utils.location("heart/absorbing/full"), Utils.location("heart/absorbing/full_blinking"),
            Utils.location("heart/absorbing/half"), Utils.location("heart/absorbing/half_blinking"),
            Utils.location("heart/absorbing/hardcore_full"), Utils.location("heart/absorbing/hardcore_full_blinking"),
            Utils.location("heart/absorbing/hardcore_half"), Utils.location("heart/absorbing/hardcore_half_blinking")
    ),
    ABSORBING_POISONED(
            Utils.location("heart/absorbing/poisoned/full"), Utils.location("heart/absorbing/poisoned/full_blinking"),
            Utils.location("heart/absorbing/poisoned/half"), Utils.location("heart/absorbing/poisoned/half_blinking"),
            Utils.location("heart/absorbing/poisoned/hardcore_full"), Utils.location("heart/absorbing/poisoned/hardcore_full_blinking"),
            Utils.location("heart/absorbing/poisoned/hardcore_half"), Utils.location("heart/absorbing/poisoned/hardcore_half_blinking")
    ),
    ABSORBING_WITHERED(
            Utils.location("heart/absorbing/withered/full"), Utils.location("heart/absorbing/withered/full_blinking"),
            Utils.location("heart/absorbing/withered/half"), Utils.location("heart/absorbing/withered/half_blinking"),
            Utils.location("heart/absorbing/withered/hardcore_full"), Utils.location("heart/absorbing/withered/hardcore_full_blinking"),
            Utils.location("heart/absorbing/withered/hardcore_half"), Utils.location("heart/absorbing/withered/hardcore_half_blinking")
    ),
    ABSORBING_FROZEN(
            Utils.location("heart/absorbing/frozen/full"), Utils.location("heart/absorbing/frozen/full_blinking"),
            Utils.location("heart/absorbing/frozen/half"), Utils.location("heart/absorbing/frozen/half_blinking"),
            Utils.location("heart/absorbing/frozen/hardcore_full"), Utils.location("heart/absorbing/frozen/hardcore_full_blinking"),
            Utils.location("heart/absorbing/frozen/hardcore_half"), Utils.location("heart/absorbing/frozen/hardcore_half_blinking")
    );

    private final ResourceLocation full, fullBlinking;
    private final ResourceLocation half, halfBlinking;
    private final ResourceLocation hardcoreFull, hardcoreFullBlinking;
    private final ResourceLocation hardcoreHalf, hardcoreHalfBlinking;

    CHeartType(
            ResourceLocation full, ResourceLocation fullBlinking,
            ResourceLocation half, ResourceLocation halfBlinking,
            ResourceLocation hardcoreFull, ResourceLocation hardcoreFullBlinking,
            ResourceLocation hardcoreHalf, ResourceLocation hardcoreHalfBlinking
    ) {
        this.full = full;
        this.fullBlinking = fullBlinking;
        this.half = half;
        this.halfBlinking = halfBlinking;
        this.hardcoreFull = hardcoreFull;
        this.hardcoreFullBlinking = hardcoreFullBlinking;
        this.hardcoreHalf = hardcoreHalf;
        this.hardcoreHalfBlinking = hardcoreHalfBlinking;
    }

    private final Map<Integer, ResourceLocation> COLOR_CACHE = new HashMap<>();

    public ResourceLocation getSprite(boolean hardcore, boolean blinking, boolean half, Integer rgbColor, boolean vanilla) {
        if (rgbColor != null && this == CONTAINER) {
            CColorfulHearts.LOGGER.error("Container cannot have color: {}", rgbColor);
            throw new IllegalArgumentException();
        }

        if (vanilla && rgbColor == null && (!half || this != CONTAINER)) {
            return switch (this) {
                case CONTAINER -> Gui.HeartType.CONTAINER.getSprite(hardcore, false, blinking);
                case HEALTH -> Gui.HeartType.NORMAL.getSprite(hardcore, half, blinking);
                case HEALTH_POISONED -> Gui.HeartType.POISIONED.getSprite(hardcore, half, blinking);
                case HEALTH_WITHERED -> Gui.HeartType.WITHERED.getSprite(hardcore, half, blinking);
                case HEALTH_FROZEN -> Gui.HeartType.FROZEN.getSprite(hardcore, half, blinking);
                case ABSORBING, ABSORBING_POISONED, ABSORBING_WITHERED, ABSORBING_FROZEN -> Gui.HeartType.ABSORBING.getSprite(hardcore, half, blinking);
            };
        }

        ResourceLocation texture;
        if (half) {
            if (hardcore) {
                texture = blinking ? this.hardcoreHalfBlinking : this.hardcoreHalf;
            } else {
                texture = blinking ? this.halfBlinking : this.half;
            }
        } else {
            if (hardcore) {
                texture = blinking ? this.hardcoreFullBlinking : this.hardcoreFull;
            } else {
                texture = blinking ? this.fullBlinking : this.full;
            }
        }

        if (rgbColor != null) {
            // since the color is just a rgb integer, there are enough bits left to use as flags for cache key
            int cacheKey = rgbColor;
            if (hardcore) cacheKey |= 1 << 24;
            if (blinking) cacheKey |= 1 << 25;
            if (half) cacheKey |= 1 << 26;

            ResourceLocation cached = COLOR_CACHE.get(cacheKey);
            if (cached == null) {
                cached = texture.withSuffix("_" + rgbColor);
                COLOR_CACHE.put(cacheKey, cached);
            }
            return cached;
        }

        return texture;
    }

    public boolean isHealth() {
        return switch (this) {
            case HEALTH, HEALTH_POISONED, HEALTH_WITHERED, HEALTH_FROZEN -> true;
            default -> false;
        };
    }

    public boolean isEffect() {
        return switch (this) {
            case HEALTH, ABSORBING -> false;
            default -> true;
        };
    }

    public Integer[] getColors() {
        List<String> configColors;
        boolean vanillaHearts = false;
        switch (this) {
            case HEALTH -> {
                configColors = Configuration.HEALTH.colors.get();
                vanillaHearts = Configuration.HEALTH.vanillaHearts.get();
            }
            case HEALTH_POISONED -> {
                configColors = Configuration.HEALTH.poisonedColors.get();
                if (configColors.size() < 2) vanillaHearts = true;
            }
            case HEALTH_WITHERED -> {
                configColors = Configuration.HEALTH.witheredColors.get();
                if (configColors.size() < 2) vanillaHearts = true;
            }
            case HEALTH_FROZEN -> {
                configColors = Configuration.HEALTH.frozenColors.get();
                if (configColors.size() < 2) vanillaHearts = true;
            }
            case ABSORBING -> {
                configColors = Configuration.ABSORPTION.colors.get();
                vanillaHearts = Configuration.ABSORPTION.vanillaHearts.get();
            }
            case ABSORBING_POISONED -> configColors = Configuration.ABSORPTION.poisonedColors.get();
            case ABSORBING_WITHERED -> configColors = Configuration.ABSORPTION.witheredColors.get();
            case ABSORBING_FROZEN -> configColors = Configuration.ABSORPTION.frozenColors.get();
            default -> {
                return null;
            }
        }

        Stream<Integer> stream = configColors.stream().map(s -> Integer.decode(s) & 0xFFFFFF);

        if (vanillaHearts) {
            List<Integer> colors = stream.collect(Collectors.toList());
            colors.add(0, null);
            return colors.toArray(Integer[]::new);
        }

        return stream.toArray(Integer[]::new);
    }

    public static CHeartType forPlayer(Player player, boolean health) {
        if (player.hasEffect(MobEffects.POISON)) {
            return health ? HEALTH_POISONED : ABSORBING_POISONED;
        } else if (player.hasEffect(MobEffects.WITHER)) {
            return health ? HEALTH_WITHERED : ABSORBING_WITHERED;
        } else if (player.isFullyFrozen()) {
            return health ? HEALTH_FROZEN : ABSORBING_FROZEN;
        } else return health ? HEALTH : ABSORBING;
    }
}
