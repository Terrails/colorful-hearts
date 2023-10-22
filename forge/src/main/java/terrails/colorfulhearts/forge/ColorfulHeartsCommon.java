package terrails.colorfulhearts.forge;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLLoader;
import terrails.colorfulhearts.CColorfulHearts;

@Mod(CColorfulHearts.MOD_ID)
public class ColorfulHeartsCommon {

    public ColorfulHeartsCommon() {
        if (FMLLoader.getDist() == Dist.CLIENT) {
            ColorfulHearts.initialize();
        }
    }
}
