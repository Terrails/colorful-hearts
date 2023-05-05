package terrails.colorfulhearts.fabric;

import net.fabricmc.loader.api.FabricLoader;
import terrails.colorfulhearts.CColorfulHearts;

import static terrails.colorfulhearts.CColorfulHearts.LOGGER;

public class LoaderExpectPlatformImpl {

    public static void applyConfig() {
        ColorfulHearts.FILE_CONFIG.save();
        LOGGER.debug("Successfully saved changes to {} config file.", CColorfulHearts.MOD_ID + ".toml");
    }

    public static boolean forcedHardcoreHearts() {
        if (FabricLoader.getInstance().getObjectShare().get("colorfulhearts:force_hardcore_hearts") instanceof Boolean forced) {
            return forced;
        } else return false;
    }

    public static boolean inDevEnvironment() {
        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }
}
