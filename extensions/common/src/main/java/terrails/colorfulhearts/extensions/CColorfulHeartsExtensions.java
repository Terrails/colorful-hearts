package terrails.colorfulhearts.extensions;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.resources.Identifier;

public class CColorfulHeartsExtensions {

    public static final String MOD_ID = "colorfulhearts_extensions";
    public static final String MOD_NAME = "Colorful Hearts Extensions";
    public static final Logger LOGGER = LogManager.getLogger(MOD_NAME);

    public static Identifier location(String path) {
        return Identifier.fromNamespaceAndPath(CColorfulHeartsExtensions.MOD_ID, path);
    }
}
