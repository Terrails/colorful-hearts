package terrails.colorfulhearts;

import net.minecraft.resources.ResourceLocation;

public class Utils {

    public static ResourceLocation location(String path) {
        return new ResourceLocation(CColorfulHearts.MOD_ID, path);
    }
}
