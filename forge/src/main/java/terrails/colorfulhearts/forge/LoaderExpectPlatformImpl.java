package terrails.colorfulhearts.forge;

import net.minecraftforge.fml.loading.FMLLoader;
import terrails.colorfulhearts.CColorfulHearts;

import static terrails.colorfulhearts.CColorfulHearts.LOGGER;

public class LoaderExpectPlatformImpl {

    public static void applyConfig() {
        ColorfulHearts.CONFIG_SPEC.save();
        LOGGER.debug("Successfully saved changes to {} config file.", CColorfulHearts.MOD_ID + ".toml");
    }

    public static boolean forcedHardcoreHearts() {
        return false;
    }

    public static boolean inDevEnvironment() { return !FMLLoader.isProduction(); }
}
