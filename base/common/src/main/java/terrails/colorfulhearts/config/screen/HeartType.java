package terrails.colorfulhearts.config.screen;

import net.minecraft.resources.ResourceLocation;
import terrails.colorfulhearts.CColorfulHearts;
import terrails.colorfulhearts.config.ConfigOption;
import terrails.colorfulhearts.config.Configuration;

import java.util.List;
import java.util.function.Supplier;

public enum HeartType {
    HEALTH(true, Configuration.HEALTH.colors, Configuration.HEALTH.vanillaHearts),
    HEALTH_POISONED(true, Configuration.HEALTH.poisonedColors, () -> Configuration.HEALTH.poisonedColors.get().size() == 1),
    HEALTH_WITHERED(true, Configuration.HEALTH.witheredColors, () -> Configuration.HEALTH.witheredColors.get().size() == 1),
    HEALTH_FROZEN(true, Configuration.HEALTH.frozenColors, () -> Configuration.HEALTH.frozenColors.get().size() == 1),
    ABSORBING(false, Configuration.ABSORPTION.colors, Configuration.ABSORPTION.vanillaHearts),
    ABSORBING_POISONED(false, Configuration.ABSORPTION.poisonedColors, () -> false),
    ABSORBING_WITHERED(false, Configuration.ABSORPTION.witheredColors, () -> false),
    ABSORBING_FROZEN(false, Configuration.ABSORPTION.frozenColors, () -> false);

    private final boolean isHealth;
    private final ConfigOption<List<String>, List<Integer>> configColors;
    private final Supplier<Boolean> vanillaVariantPresent;

    HeartType(boolean isHealth, ConfigOption<List<String>, List<Integer>> configOption, Supplier<Boolean> vanillaVariantPresent) {
        this.isHealth = isHealth;
        this.configColors = configOption;
        this.vanillaVariantPresent = vanillaVariantPresent;
    }

    public boolean isHealthType() {
        return this.isHealth;
    }

    public boolean isEffectType() {
        return this != HEALTH && this != ABSORBING;
    }

    public boolean isVanillaVariantPresent() {
        return this.vanillaVariantPresent.get();
    }

    public ConfigOption<List<String>, List<Integer>> getConfigOption() {
        return this.configColors;
    }

    private String getEffectName() {
        if (this.isEffectType()) {
            switch (this) {
                case HEALTH_POISONED, ABSORBING_POISONED -> {
                    return "/poisoned";
                }
                case HEALTH_WITHERED, ABSORBING_WITHERED -> {
                    return "/withered";
                }
                case HEALTH_FROZEN, ABSORBING_FROZEN -> {
                    return "/frozen";
                }
            }
        }
        return "";
    }

    public List<Integer> getColors() {
        return this.configColors.get();
    }

    public ResourceLocation getBaseSprite(boolean hardcore, boolean highlight, boolean half) {
        return CColorfulHearts.location("heart/" + (this.isHealthType() ? "health" : "absorbing") + this.getEffectName() + "/" + (hardcore ? "hardcore_" : "") + (half ? "half" : "full") + (highlight ? "_blinking" : ""));
    }

    public ResourceLocation getSprite(boolean hardcore, boolean highlight, boolean half, int color) {
        return this.getBaseSprite(hardcore, highlight, half).withSuffix("_" + color);
    }
}
