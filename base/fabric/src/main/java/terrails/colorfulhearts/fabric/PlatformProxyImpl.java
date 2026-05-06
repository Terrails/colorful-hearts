package terrails.colorfulhearts.fabric;

import net.fabricmc.loader.api.FabricLoader;

import terrails.colorfulhearts.CColorfulHearts;
import terrails.colorfulhearts.PlatformProxy;
import terrails.colorfulhearts.api.event.HeartRegistry;
import terrails.colorfulhearts.api.event.HeartRenderEvent;
import terrails.colorfulhearts.api.event.HeartSingleRenderEvent;
import terrails.colorfulhearts.api.fabric.ColorfulHeartsApi;
import terrails.colorfulhearts.api.fabric.event.FabHeartEvents;
import terrails.colorfulhearts.api.heart.drawing.Heart;
import terrails.colorfulhearts.api.heart.drawing.OverlayHeart;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;

public class PlatformProxyImpl implements PlatformProxy {

    @Override
    public String getLoader() {
        return "fabric";
    }

    @Override
    public void applyConfig() {
        ColorfulHearts.CONFIG.save();
    }

    @Override
    public boolean forcedHardcoreHearts() {
        if (FabricLoader.getInstance().getObjectShare().get("colorfulhearts:force_hardcore_hearts") instanceof Boolean forced) {
            return forced;
        } else return false;
    }

    @Override
    public HeartRenderEvent.Pre preRenderEvent(GuiGraphics guiGraphics, Player player, int x, int y, int maxHealth, int currentHealth, int displayHealth, int absorption, boolean blinking, boolean hardcore, OverlayHeart overlayHeart) {
        HeartRenderEvent.Pre event = new HeartRenderEvent.Pre(guiGraphics, player, x, y, maxHealth, currentHealth, displayHealth, absorption, blinking, hardcore, overlayHeart);
        FabHeartEvents.PRE_RENDER.invoker().accept(event);
        return event;
    }

    @Override
    public OverlayHeart playerHeartTypeEvent(Player player, OverlayHeart overlayHeart) {
        return overlayHeart; // do nothing as it's a NeoForge specific event
    }

    @Override
    public void postRenderEvent(GuiGraphics guiGraphics, Player player, int x, int y, int maxHealth, int currentHealth, int displayHealth, int absorption, boolean blinking, boolean hardcore, OverlayHeart overlayHeart) {
        HeartRenderEvent.Post event = new HeartRenderEvent.Post(guiGraphics, player, x, y, maxHealth, currentHealth, displayHealth, absorption, blinking, hardcore, overlayHeart);
        FabHeartEvents.POST_RENDER.invoker().accept(event);
    }

    @Override
    public void singleRenderEvent(Heart heart, GuiGraphics guiGraphics, int index, int x, int y, boolean hardcore, boolean blinking, boolean blinkingHeart) {
        HeartSingleRenderEvent event = new HeartSingleRenderEvent(heart, guiGraphics, index, x, y, hardcore, blinking, blinkingHeart);
        FabHeartEvents.SINGLE_RENDER.invoker().accept(event);
    }

    @Override
    public void heartRegistryEvent(HeartRegistry registry) {
        FabricLoader.getInstance().getEntrypointContainers("colorfulhearts", ColorfulHeartsApi.class).forEach(entryPoint -> {
            String modId = entryPoint.getProvider().getMetadata().getId();
            try {
                CColorfulHearts.LOGGER.info("Loading ColorfulHeartsApi implementation of mod {}", modId);
                ColorfulHeartsApi api = entryPoint.getEntrypoint();
                api.registerHearts(registry);
                CColorfulHearts.LOGGER.debug("Loaded ColorfulHeartsApi implementation of mod {}", modId);
            } catch (Throwable e) {
                CColorfulHearts.LOGGER.error("Could not load ColorfulHeartsApi implementation of mod {}", modId, e);
            }
        });

        FabHeartEvents.HEART_REGISTRY.invoker().accept(registry);
    }

    @Override
    public void heartUpdateEvent() {
        FabHeartEvents.UPDATE.invoker().run();
    }
}
