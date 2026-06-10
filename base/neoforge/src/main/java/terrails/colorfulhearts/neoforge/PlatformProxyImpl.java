package terrails.colorfulhearts.neoforge;

import net.neoforged.fml.ModLoader;
import net.neoforged.neoforge.client.event.PlayerHeartTypeEvent;
import net.neoforged.neoforge.common.NeoForge;

import terrails.colorfulhearts.CColorfulHearts;
import terrails.colorfulhearts.PlatformProxy;
import terrails.colorfulhearts.api.event.HeartRegistry;
import terrails.colorfulhearts.api.event.HeartRenderEvent;
import terrails.colorfulhearts.api.heart.Hearts;
import terrails.colorfulhearts.api.heart.drawing.Heart;
import terrails.colorfulhearts.api.heart.drawing.OverlayHeart;
import terrails.colorfulhearts.api.neoforge.event.NeoHeartRegistryEvent;
import terrails.colorfulhearts.api.neoforge.event.NeoHeartRenderEvent;
import terrails.colorfulhearts.api.neoforge.event.NeoHeartSingleRenderEvent;
import terrails.colorfulhearts.api.neoforge.event.NeoHeartUpdateEvent;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.entity.player.Player;

import static terrails.colorfulhearts.CColorfulHearts.LOGGER;

public class PlatformProxyImpl implements PlatformProxy {

    @Override
    public String getLoader() {
        return "neoforge";
    }

    @Override
    public void applyConfig() {
        ColorfulHearts.CONFIG_SPEC.save();
        LOGGER.debug("Successfully saved changes to {} config file.", CColorfulHearts.MOD_ID + ".toml");
    }

    @Override
    public boolean forcedHardcoreHearts() {
        return false;
    }

    @Override
    public HeartRenderEvent.Pre preRenderEvent(GuiGraphicsExtractor GuiGraphicsExtractor, Player player, int x, int y, int maxHealth, int currentHealth, int displayHealth, int absorption, boolean blinking, boolean hardcore, OverlayHeart overlayHeart) {
        NeoHeartRenderEvent.Pre event = new NeoHeartRenderEvent.Pre(GuiGraphicsExtractor, player, x, y, maxHealth, currentHealth, displayHealth, absorption, blinking, hardcore, overlayHeart);
        NeoForge.EVENT_BUS.post(event);
        return event.getEvent();
    }

    @Override
    public OverlayHeart playerHeartTypeEvent(Player player, OverlayHeart overlayHeart) {
        Gui.HeartType heartType = overlayHeart == null ? Gui.HeartType.NORMAL : Hearts.getHeartTypeFromOverlayHeart(overlayHeart);
        return Hearts.getOverlayHeartFromHeartType(NeoForge.EVENT_BUS.post(new PlayerHeartTypeEvent(player, heartType)).getType()).orElse(overlayHeart);
    }

    @Override
    public void postRenderEvent(GuiGraphicsExtractor GuiGraphicsExtractor, Player player, int x, int y, int maxHealth, int currentHealth, int displayHealth, int absorption, boolean blinking, boolean hardcore, OverlayHeart overlayHeart) {
        NeoForge.EVENT_BUS.post(new NeoHeartRenderEvent.Post(GuiGraphicsExtractor, player, x, y, maxHealth, currentHealth, displayHealth, absorption, blinking, hardcore, overlayHeart));
    }

    @Override
    public void heartRegistryEvent(HeartRegistry registry) {
        ModLoader.postEvent(new NeoHeartRegistryEvent(registry));
    }

    @Override
    public void singleRenderEvent(Heart heart, GuiGraphicsExtractor GuiGraphicsExtractor, int index, int x, int y, boolean hardcore, boolean blinking, boolean blinkingHeart) {
        NeoForge.EVENT_BUS.post(new NeoHeartSingleRenderEvent(heart, GuiGraphicsExtractor, index, x, y, hardcore, blinking, blinkingHeart));
    }

    @Override
    public void heartUpdateEvent() {
        NeoForge.EVENT_BUS.post(new NeoHeartUpdateEvent());
    }
}
