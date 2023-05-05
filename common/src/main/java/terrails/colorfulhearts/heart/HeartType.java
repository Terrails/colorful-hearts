package terrails.colorfulhearts.heart;

import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;

public enum HeartType {
    NORMAL(4, 0, true),
    POISONED(8, 2, true),
    WITHERED(12, 4, true),
    FROZEN(18, 6, false);

    private final int indexVanilla;
    private final int index;
    private final boolean canBlink;

    HeartType(int indexVanilla, int index, boolean canBlink) {
        this.indexVanilla = indexVanilla;
        this.index = index;
        this.canBlink = canBlink;
    }

    public int getX(int x, boolean vanilla, boolean absorption, boolean blinking) {
        if (vanilla) {
            x += 16;
            if (absorption) {
                x += 144;
            } else {
                x += this.indexVanilla * 9;
                // Absorption should never "blink"
                if (this.canBlink && blinking) {
                    x += 18;
                }
            }
        } else {
            x += this.index * 9;
        }
        return x;
    }

    public static HeartType forPlayer(Player player) {
        if (player.hasEffect(MobEffects.POISON)) {
            return POISONED;
        } else if (player.hasEffect(MobEffects.WITHER)) {
            return WITHERED;
        } else if (player.isFullyFrozen()) {
            return FROZEN;
        } else return NORMAL;
    }
}
