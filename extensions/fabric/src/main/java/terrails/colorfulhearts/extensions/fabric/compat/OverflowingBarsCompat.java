package terrails.colorfulhearts.extensions.fabric.compat;

import fuzs.overflowingbars.OverflowingBars;
import fuzs.overflowingbars.client.gui.RowCountRenderer;
import fuzs.overflowingbars.config.ClientConfig;

import terrails.colorfulhearts.api.fabric.event.FabHeartEvents;
import terrails.colorfulhearts.extensions.compat.OverflowingBarsCommonCompat;
import net.minecraft.client.gui.GuiGraphics;

public class OverflowingBarsCompat extends OverflowingBarsCommonCompat {

    public OverflowingBarsCompat() {
        super();
        FabHeartEvents.POST_RENDER.register(this::render);
    }

    @Override
    protected void drawBarRowCount(GuiGraphics guiGraphics, int posX, int posY, int barValue, int maxRowCount) {
        RowCountRenderer.drawBarRowCount(guiGraphics, posX, posY, barValue, true, maxRowCount);
    }

    @Override
    protected boolean allowCount() {
        return OverflowingBars.CONFIG.get(ClientConfig.class).health.allowCount;
    }
}
