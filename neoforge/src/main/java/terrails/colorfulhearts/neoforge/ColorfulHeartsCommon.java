package terrails.colorfulhearts.neoforge;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLLoader;
import terrails.colorfulhearts.CColorfulHearts;

@Mod(CColorfulHearts.MOD_ID)
public class ColorfulHeartsCommon {

    public ColorfulHeartsCommon(final IEventBus bus) {
        if (FMLLoader.getDist().isClient()) {
            new ColorfulHearts(bus);
        }
    }
}
