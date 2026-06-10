package terrails.colorfulhearts.extensions.neoforge;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;

import terrails.colorfulhearts.extensions.CColorfulHeartsExtensions;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import static terrails.colorfulhearts.extensions.CColorfulHeartsExtensions.LOGGER;

@Mod(value = CColorfulHeartsExtensions.MOD_ID, dist = Dist.CLIENT)
public class ColorfulHeartsExtensions {

    private static final Map<String, String> COMPAT = Map.of(
            "appleskin", "AppleSkinCompat",
            "undergarden", "UndergardenCompat"
            //            "overflowingbars", "OverflowingBarsCompat"
    );

    public ColorfulHeartsExtensions(final IEventBus bus) {
        this.setupCompat(bus);
    }

    private void setupCompat(final IEventBus bus) {
        final String basePackage = "terrails.colorfulhearts.extensions.neoforge.compat";

        for (Map.Entry<String, String> entry : COMPAT.entrySet()) {
            String id = entry.getKey();
            if (ModList.get().isLoaded(id)) {
                String className = basePackage + "." + entry.getValue();
                LOGGER.info("Loading compat for mod {}", id);
                try {
                    Class<?> compatClass = Class.forName(className);
                    try {
                        compatClass.getDeclaredConstructor(IEventBus.class).newInstance(bus);
                    } catch (NoSuchMethodException ignored) {
                        compatClass.getDeclaredConstructor().newInstance();
                    }
                } catch (ClassNotFoundException e) {
                    LOGGER.error("Failed to load compat as {} does not exist", className, e);
                } catch (NoSuchMethodException e) {
                    LOGGER.error("Failed to load compat as {} does not have a valid constructor", className, e);
                } catch (IllegalAccessException e) {
                    LOGGER.error("Failed to load compat as {} does not have a valid public constructor", className, e);
                } catch (InstantiationException e) {
                    LOGGER.error("Failed to load compat as {} is an abstract class", className, e);
                } catch (InvocationTargetException e) {
                    LOGGER.error("Failed to load compat {} as an unknown error was thrown", className, e);
                }
            } else {
                LOGGER.debug("Skipped loading compat for missing mod {}", id);
            }
        }
    }
}
