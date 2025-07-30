package terrails.colorfulhearts.neoforge.compat;

import fuzs.overflowingbars.OverflowingBars;
import fuzs.overflowingbars.client.gui.RowCountRenderer;
import fuzs.overflowingbars.config.ClientConfig;
import net.neoforged.neoforge.common.NeoForge;

import terrails.colorfulhearts.api.neoforge.event.NeoHeartRenderEvent;
import terrails.colorfulhearts.compat.OverflowingBarsCommonCompat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

public class OverflowingBarsCompat extends OverflowingBarsCommonCompat {

    public OverflowingBarsCompat() {
        super();
        NeoForge.EVENT_BUS.addListener(this::onPostRender);
    }

    public void onPostRender(NeoHeartRenderEvent.Post event) {
        this.render(event.getEvent());
    }

    @Override
    protected void drawBarRowCount(GuiGraphics guiGraphics, int posX, int posY, int barValue, int maxRowCount) {
        RowCountRenderer.drawBarRowCount(guiGraphics, posX, posY, barValue, true, maxRowCount, Minecraft.getInstance().font);
    }

    @Override
    protected boolean allowCount() {
        return OverflowingBars.CONFIG.get(ClientConfig.class).health.allowCount;
    }
}
