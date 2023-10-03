package terrails.colorfulhearts;

import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CColorfulHearts {

    public static final String MOD_ID = "colorfulhearts";
    public static final String MOD_NAME = "Colorful Hearts";
    public static final Logger LOGGER = LogManager.getLogger(MOD_NAME);

    public static final ResourceLocation HEALTH_ICONS_LOCATION = new ResourceLocation(MOD_ID, "textures/health.png");
    public static final ResourceLocation ABSORPTION_ICONS_LOCATION = new ResourceLocation(MOD_ID, "textures/absorption.png");
    public static final ResourceLocation HALF_HEART_ICONS_LOCATION = new ResourceLocation(MOD_ID, "textures/half_heart.png");
    public static final ResourceLocation GUI_ICONS_LOCATION = new ResourceLocation("textures/gui/icons.png");
}
