package terrails.colorfulhearts.extensions;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.resources.ResourceLocation;

public class CColorfulHeartsExtensions {

    public static final String MOD_ID = "colorfulhearts_extensions";
    public static final String MOD_NAME = "Colorful Hearts Extensions";
    public static final Logger LOGGER = LogManager.getLogger(MOD_NAME);

    public static ResourceLocation location(String path) {
        return ResourceLocation.fromNamespaceAndPath(CColorfulHeartsExtensions.MOD_ID, path);
    }
}
