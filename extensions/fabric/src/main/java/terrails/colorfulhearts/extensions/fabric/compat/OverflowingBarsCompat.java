package terrails.colorfulhearts.extensions.fabric.compat;

import fuzs.overflowingbars.common.OverflowingBars;
import fuzs.overflowingbars.common.client.gui.RowCountRenderer;
import fuzs.overflowingbars.common.config.ClientConfig;

import terrails.colorfulhearts.api.fabric.event.FabHeartEvents;
import terrails.colorfulhearts.extensions.compat.OverflowingBarsCommonCompat;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public class OverflowingBarsCompat extends OverflowingBarsCommonCompat {

    public OverflowingBarsCompat() {
        super();
        FabHeartEvents.POST_RENDER.register(this::render);
    }

    @Override
    protected void drawBarRowCount(GuiGraphicsExtractor guiGraphics, int posX, int posY, int barValue, int maxRowCount) {
        RowCountRenderer.drawBarRowCount(guiGraphics, posX, posY, barValue, true, maxRowCount);
    }

    @Override
    protected boolean allowCount() {
        return OverflowingBars.CONFIG.get(ClientConfig.class).health.allowCount;
    }
}
