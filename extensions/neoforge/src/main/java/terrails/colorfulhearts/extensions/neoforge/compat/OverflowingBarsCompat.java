package terrails.colorfulhearts.extensions.neoforge.compat;

import fuzs.overflowingbars.common.OverflowingBars;
import fuzs.overflowingbars.common.client.gui.RowCountRenderer;
import fuzs.overflowingbars.common.config.ClientConfig;
import net.neoforged.neoforge.common.NeoForge;

import terrails.colorfulhearts.api.neoforge.event.NeoHeartRenderEvent;
import terrails.colorfulhearts.extensions.compat.OverflowingBarsCommonCompat;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public class OverflowingBarsCompat extends OverflowingBarsCommonCompat {

    public OverflowingBarsCompat() {
        super();
        NeoForge.EVENT_BUS.addListener(this::onPostRender);
    }

    public void onPostRender(NeoHeartRenderEvent.Post event) {
        this.render(event.getEvent());
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
