package terrails.colorfulhearts.neoforge.render;

import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

import terrails.colorfulhearts.render.HeartRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Mth;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;

import java.util.Objects;

public class RenderEventHandler {

    public static final RenderEventHandler INSTANCE = new RenderEventHandler();

    public void renderHearts(RenderGuiLayerEvent.Pre event) {
        Minecraft client = Minecraft.getInstance();
        if (event.isCanceled()
                || client.options.hideGui
                || !event.getName().equals(VanillaGuiLayers.PLAYER_HEALTH)
                || !Objects.requireNonNull(client.gameMode).canHurtPlayer()
                || !(client.getCameraEntity() instanceof Player player)) {
            return;
        }

        Profiler.get().push("health");

        int absorption = Mth.ceil(player.getAbsorptionAmount());
        int health = Mth.ceil(player.getHealth());
        int maxHealth = Mth.ceil(Math.max((float) player.getAttributeValue(Attributes.MAX_HEALTH), Math.max(HeartRenderer.INSTANCE.displayHealth, health)));

        GuiGraphics guiGraphics = event.getGuiGraphics();
        int width = guiGraphics.guiWidth();
        int height = guiGraphics.guiHeight();
        int left = width / 2 - 91;
        int top = height - client.gui.leftHeight;

        // handle half heart requiring absorption to move one row up
        boolean hasAbsorptionRow = (absorption + Math.min(20, maxHealth == 19 ? 20 : maxHealth)) > 20;
        int offset = 10 + (hasAbsorptionRow ? 10 : 0);
        client.gui.leftHeight += offset;

        HeartRenderer.INSTANCE.renderPlayerHearts(guiGraphics, player, left, top);

        Profiler.get().pop();

        event.setCanceled(true);
    }
}
