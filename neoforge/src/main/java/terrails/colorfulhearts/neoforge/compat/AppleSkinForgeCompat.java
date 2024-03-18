package terrails.colorfulhearts.neoforge.compat;

import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.NeoForge;
import squeek.appleskin.ModConfig;
import squeek.appleskin.api.event.FoodValuesEvent;
import squeek.appleskin.api.event.HUDOverlayEvent;
import squeek.appleskin.api.food.FoodValues;
import squeek.appleskin.client.HUDOverlayHandler;
import squeek.appleskin.helpers.FoodHelper;
import terrails.colorfulhearts.compat.AppleSkinCompat;
import terrails.colorfulhearts.heart.CHeartType;
import terrails.colorfulhearts.neoforge.api.event.ForgeHeartChangeEvent;
import terrails.colorfulhearts.neoforge.api.event.ForgeHeartRenderEvent;
import terrails.colorfulhearts.neoforge.mixin.compat.appleskin.HUDOverlayHandlerAccessor;

public class AppleSkinForgeCompat extends AppleSkinCompat {

    public AppleSkinForgeCompat() {
        NeoForge.EVENT_BUS.addListener(this::onDefaultRender);
        NeoForge.EVENT_BUS.addListener(this::onPostRender);
        NeoForge.EVENT_BUS.addListener(this::heartChanged);
    }

    /**
     * event called when default overlay renderer gets called.
     * Issue with it is that it should never get called due to RenderGuiOverlayEvent.Post
     * never being called due to RenderGuiOverlayEvent.Pre being cancelled
     * Due to that, the value of modifiedHealth cannot be attained here and has
     * to be calculated manually.
     * In weird case the event gets called, it should be cancelled
     */
    private void onDefaultRender(HUDOverlayEvent.HealthRestored event) {
        event.setCanceled(true);
    }

    private void onPostRender(ForgeHeartRenderEvent.Post event) {
        Player player = client.player;
        assert player != null;

        if (!shouldDrawOverlay(event.getHealthType(), player)) {
            return;
        }

        /* copied from HUDOverlayHandler */

        // try to get the item stack in the player hand
        ItemStack heldItem = player.getMainHandItem();
        if (ModConfig.SHOW_FOOD_VALUES_OVERLAY_WHEN_OFFHAND.get() && !FoodHelper.canConsume(heldItem, player))
            heldItem = player.getOffhandItem();

        boolean shouldRenderHeldItemValues = !heldItem.isEmpty() && FoodHelper.canConsume(heldItem, player);
        if (!shouldRenderHeldItemValues) {
            HUDOverlayHandler.resetFlash();
            return;
        }
        int health = Mth.ceil(player.getHealth());

        FoodValues modifiedFoodValues = FoodHelper.getModifiedFoodValues(heldItem, player);
        FoodValuesEvent foodValuesEvent = new FoodValuesEvent(player, heldItem, FoodHelper.getDefaultFoodValues(heldItem, player), modifiedFoodValues);
        NeoForge.EVENT_BUS.post(foodValuesEvent);
        modifiedFoodValues = foodValuesEvent.modifiedFoodValues;

        float foodHealthIncrement = FoodHelper.getEstimatedHealthIncrement(heldItem, modifiedFoodValues, player);
        int modifiedHealth = Mth.ceil(Math.min(health + foodHealthIncrement, player.getMaxHealth()));

        if (modifiedHealth <= health) {
            return;
        }

        int absorbing = Mth.ceil(player.getAbsorptionAmount());

        // this value never reaches 1.0, so the health colors will always be somewhat mixed
        // I'll leave this behaviour as is at it makes the differentiation easier
        float alpha = HUDOverlayHandlerAccessor.getFlashAlpha();

        drawHealthOverlay(event.getGuiGraphics(), event.getX(), event.getY(), absorbing, health, modifiedHealth, alpha, event.isHardcore());
    }

    private void heartChanged(ForgeHeartChangeEvent event) {
        this.lastHealth = 0;
    }

    public boolean shouldDrawOverlay(CHeartType heartType, Player player) {
        if (heartType != CHeartType.HEALTH) {
            return false; // AppleSkin usually checks the effect, but we'll do it this way
        }

        /* copied from HUDOverlayHandler */
        if (!ModConfig.SHOW_FOOD_HEALTH_HUD_OVERLAY.get()) {
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
