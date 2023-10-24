package terrails.colorfulhearts.fabric.mixin;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import terrails.colorfulhearts.render.HeartRenderer;

/**
 * Mixin for injecting custom heart renderer for Fabric and Quilt mod-loaders
 */
@Mixin(Gui.class)
public abstract class GuiMixin {

    @Shadow protected abstract Player getCameraPlayer();

    @Shadow private int displayHealth;

    /**
     * Disables the default heart renderer by setting for-loop index to -1 resulting in it never executing
     * Also injects own heart render
     */
    @ModifyVariable(method = "renderHearts", at = @At("STORE"), ordinal = 10)
    private int colorfulhearts_renderHearts(
            int defaultIndexVal, GuiGraphics guiGraphics, Player player,
            int x, int y, int height, int regenIndex, float maxHealth,
            int currentHealth, int displayHealth, int absorptionAmount, boolean blinking
    ) {
        HeartRenderer.INSTANCE.renderPlayerHearts(guiGraphics, player, x, y, Mth.ceil(maxHealth), currentHealth, displayHealth, absorptionAmount, blinking);
        return -1;
    }

    /**
     * Modifies the heart bar rows to 1 or 2, depending on if absorption is in the same row
     * Required for armor bar to render at correct height when there are multiple heart rows
     */
    @ModifyVariable(method = "renderPlayerHealth", at = @At("STORE"), ordinal = 7)
    private int colorfulhearts_renderPlayerHealth(int defaultValue) {
        int absorption = Mth.ceil(this.getCameraPlayer().getAbsorptionAmount());
        int health = Mth.ceil(this.getCameraPlayer().getHealth());
        int maxHealth = Mth.ceil(Math.max((float) this.getCameraPlayer().getAttributeValue(Attributes.MAX_HEALTH), Math.max(this.displayHealth, health)));
        // handle half heart requiring absorption to move one row up
        if (maxHealth == 19) maxHealth = 20;

        boolean hasAbsorptionRow = (absorption + Math.min(20, maxHealth)) > 20;

        return hasAbsorptionRow ? 2 : 1;
    }
}
