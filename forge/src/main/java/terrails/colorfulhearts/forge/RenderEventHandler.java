package terrails.colorfulhearts.forge;

import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import terrails.colorfulhearts.config.Configuration;
import terrails.colorfulhearts.render.HeartRenderer;

public class RenderEventHandler {

    private final Minecraft client = Minecraft.getInstance();

    private long lastHealthTime, healthBlinkTime;
    private int displayHealth, lastHealth;

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void renderHearts(RenderGuiOverlayEvent.Pre event) {
        if (event.isCanceled()
                || client.options.hideGui
                || !event.getOverlay().id().equals(VanillaGuiOverlay.PLAYER_HEALTH.id())
                || !((ForgeGui) client.gui).shouldDrawSurvivalElements()
                || !(client.getCameraEntity() instanceof Player player)) {
            return;
        }

        client.getProfiler().push("health");

        int absorption = Mth.ceil(player.getAbsorptionAmount());
        int health = Mth.ceil(player.getHealth());

        long tickCount = this.client.gui.getGuiTicks();
        boolean highlight = this.healthBlinkTime > tickCount && (this.healthBlinkTime - tickCount) / 3L % 2L == 1L;

        if (health < this.lastHealth && player.invulnerableTime > 0) {
            this.lastHealthTime = Util.getMillis();
            this.healthBlinkTime = (tickCount + 20);
        } else if (health > this.lastHealth && player.invulnerableTime > 0) {
            this.lastHealthTime = Util.getMillis();
            this.healthBlinkTime = (tickCount + 10);
        }

        if (Util.getMillis() - this.lastHealthTime > 1000L) {
            this.displayHealth = health;
            this.lastHealthTime = Util.getMillis();
        }

        this.lastHealth = health;

        AttributeInstance attrMaxHealth = player.getAttribute(Attributes.MAX_HEALTH);
        assert attrMaxHealth != null;
        int healthMax = Mth.ceil(Math.max((float) attrMaxHealth.getValue(), Math.max(this.displayHealth, health)));

        final ForgeGui gui = (ForgeGui) client.gui;
        int width = event.getWindow().getGuiScaledWidth();
        int height = event.getWindow().getGuiScaledHeight();
        int left = width / 2 - 91;
        int top = height - gui.leftHeight;

        int offset = 10 + (absorption > 0 && !Configuration.ABSORPTION.renderOverHealth.get() ? 10 : 0);
        gui.leftHeight += offset;

        HeartRenderer.INSTANCE.renderPlayerHearts(event.getPoseStack(), player, left, top, healthMax, health, this.displayHealth, absorption, highlight);

        client.getProfiler().pop();

        event.setCanceled(true);
    }
}
