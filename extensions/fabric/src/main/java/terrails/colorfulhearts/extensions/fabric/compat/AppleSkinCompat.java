package terrails.colorfulhearts.extensions.fabric.compat;

import squeek.appleskin.ModConfig;
import squeek.appleskin.api.AppleSkinApi;
import squeek.appleskin.client.HUDOverlayHandler;
import squeek.appleskin.helpers.ConsumableFood;
import squeek.appleskin.helpers.FoodHelper;

import terrails.colorfulhearts.api.fabric.event.FabHeartEvents;
import terrails.colorfulhearts.api.heart.drawing.OverlayHeart;
import terrails.colorfulhearts.extensions.compat.AppleSkinCommonCompat;
import terrails.colorfulhearts.extensions.fabric.mixin.appleskin.HUDOverlayHandlerAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;

public class AppleSkinCompat extends AppleSkinCommonCompat implements AppleSkinApi {

    @Override
    public void registerEvents() {
        // register own custom renderer and use modifiedHealth that AppleSkin's event provided
        FabHeartEvents.POST_RENDER.register(event -> {
            Minecraft client = Minecraft.getInstance();
            Player player = event.getPlayer();

            if (!shouldDrawOverlay(event.getOverlayHeart().orElse(null), player)) {
                return;
            }

            /* copied from HUDOverlayHandler */
            FoodHelper.QueriedFoodResult result = ((HUDOverlayHandlerAccessor) HUDOverlayHandler.INSTANCE).getHeldFood().result(client.gui.getGuiTicks(), player);
            if (result == null) {
                HUDOverlayHandler.INSTANCE.resetFlash();
                return;
            }

            float foodHealthIncrement = FoodHelper.getEstimatedHealthIncrement(player, new ConsumableFood(result.modifiedFoodComponent, result.consumableComponent));
            float currentHealth = player.getHealth();
            float modifiedHealth = Math.min(currentHealth + foodHealthIncrement, player.getMaxHealth());

            if (modifiedHealth <= currentHealth) {
                return;
            }

            int absorbing = Mth.ceil(player.getAbsorptionAmount());

            // this value never reaches 1.0, so the health colors will always be somewhat mixed
            // I'll leave this behaviour as is at it makes the differentiation easier
            float alpha = ((HUDOverlayHandlerAccessor) HUDOverlayHandler.INSTANCE).getFlashAlpha();

            drawHealthOverlay(event.getGuiGraphics(), event.getX(), event.getY(), absorbing, Mth.ceil(currentHealth), Mth.ceil(modifiedHealth), alpha, event.isHardcore());
        });

        FabHeartEvents.UPDATE.register(() -> this.lastHealth = 0);
    }

    public boolean shouldDrawOverlay(OverlayHeart overlayHeart, Player player) {
        if (overlayHeart != null) {
            return false; // AppleSkin usually checks the effect, but we'll do it this way
        }

        /* copied from HUDOverlayHandler */
        if (!ModConfig.INSTANCE.showFoodHealthHudOverlay) {
            return false;
        }

        // in the `PEACEFUL` mode, health will restore faster
        if (player.level().getDifficulty() == Difficulty.PEACEFUL)
            return false;

        FoodData stats = player.getFoodData();

        // when player has any changes health amount by any case can't show estimated health
        // because player will confused how much of restored/damaged healths
        if (stats.getFoodLevel() >= 18)
            return false;

        if (player.hasEffect(MobEffects.REGENERATION))
            return false;

        return true;
    }
}
