package terrails.colorfulhearts.extensions.compat;

import terrails.colorfulhearts.api.event.HeartRenderEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;

public abstract class OverflowingBarsCommonCompat {

    public void render(HeartRenderEvent.Post event) {
        Player player = Minecraft.getInstance().player;

        if (player != null && this.allowCount()) {
            int health = Mth.ceil(player.getHealth());
            drawBarRowCount(event.getGuiGraphics(), event.getX() - 2, event.getY(), health, 20);
            int maxAbsorption = (20 - Mth.ceil(Math.min(20, health) / 2.0F)) * 2;
            drawBarRowCount(event.getGuiGraphics(), event.getX() - 2, event.getY() - 10, Mth.ceil(player.getAbsorptionAmount()), maxAbsorption);
        }
    }

    protected abstract void drawBarRowCount(GuiGraphics guiGraphics, int posX, int posY, int barValue, int maxRowCount);

    protected abstract boolean allowCount();
}
