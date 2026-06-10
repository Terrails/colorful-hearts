package terrails.colorfulhearts.fabric;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.SpriteSourceRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudStatusBarHeightRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ObjectShare;

import terrails.colorfulhearts.CColorfulHearts;
import terrails.colorfulhearts.fabric.config.FabConfig;
import terrails.colorfulhearts.render.HeartRenderer;
import terrails.colorfulhearts.render.atlas.sources.ColoredHearts;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;

public class ColorfulHearts implements ClientModInitializer {

    public static FabConfig CONFIG;

    @Override
    public void onInitializeClient() {
        CColorfulHearts.setup(new PlatformProxyImpl());
        CONFIG = new FabConfig();
        this.setupSpriteSource();
        this.setupObjectShare();
        this.setupHeartRenderer();
    }

    private void setupSpriteSource() {
        SpriteSourceRegistry.register(CColorfulHearts.location("colored_hearts"), ColoredHearts.CODEC);
    }

    private void setupObjectShare() {
        final ObjectShare objectShare = FabricLoader.getInstance().getObjectShare();

        // Absorption
        // keep this for now in case some mods depended on it
        objectShare.putIfAbsent("colorfulhearts:absorption_over_health", false);

        // Allows other mods to force use of hardcore heart textures
        // Default vanilla behaviour (hardcore world) if false
        objectShare.putIfAbsent("colorfulhearts:force_hardcore_hearts", false);
    }

    private void setupHeartRenderer() {
        HudStatusBarHeightRegistry.addLeft(VanillaHudElements.HEALTH_BAR, (player) -> {
            int displayHealth = HeartRenderer.INSTANCE.displayHealth;
            int absorption = Mth.ceil(player.getAbsorptionAmount());
            int health = Mth.ceil(player.getHealth());
            int maxHealth = Mth.ceil(Math.max((float) player.getAttributeValue(Attributes.MAX_HEALTH), Math.max(displayHealth, health)));
            // handle half heart requiring absorption to move one row up
            if (maxHealth == 19) maxHealth = 20;

            boolean hasAbsorptionRow = (absorption + Math.min(20, maxHealth)) > 20;

            return hasAbsorptionRow ? 20 : 10;
        });
        HudElementRegistry.replaceElement(VanillaHudElements.HEALTH_BAR, (vanilla) -> (guiGraphics, deltaTracker) -> {
            var mc = Minecraft.getInstance();
            int width = mc.getWindow().getGuiScaledWidth() / 2 - 91;
            int height = mc.getWindow().getGuiScaledHeight() - HudStatusBarHeightRegistry.getHeight(VanillaHudElements.HEALTH_BAR);
            HeartRenderer.INSTANCE.renderPlayerHearts(guiGraphics, (Player) mc.getCameraEntity(), width, height);
        });
    }
}
