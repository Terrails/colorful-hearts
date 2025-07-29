package terrails.colorfulhearts.neoforge.compat;

import net.neoforged.neoforge.common.NeoForge;

import terrails.colorfulhearts.api.neoforge.event.NeoHeartRenderEvent;
import terrails.colorfulhearts.compat.OverflowingBarsCommonCompat;

public class OverflowingBarsCompat extends OverflowingBarsCommonCompat {

    public OverflowingBarsCompat() {
        super();
        if (drawBarRowCount != null) {
            NeoForge.EVENT_BUS.addListener(this::onPostRender);
        }
    }

    public void onPostRender(NeoHeartRenderEvent.Post event) {
        this.render(event.getEvent());
    }
}
