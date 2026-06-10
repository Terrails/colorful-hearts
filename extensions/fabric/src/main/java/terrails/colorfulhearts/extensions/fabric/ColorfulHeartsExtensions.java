package terrails.colorfulhearts.extensions.fabric;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import static terrails.colorfulhearts.extensions.CColorfulHeartsExtensions.LOGGER;

public class ColorfulHeartsExtensions implements ClientModInitializer {

    private static final Map<String, String> COMPAT = Map.of(
            //            "overflowingbars", "OverflowingBarsCompat"
    );

    @Override
    public void onInitializeClient() {
        setupCompat();
    }

    private void setupCompat() {
        final String basePackage = "terrails.colorfulhearts.extensions.fabric.compat";

        for (Map.Entry<String, String> entry : COMPAT.entrySet()) {
            String id = entry.getKey();
            if (FabricLoader.getInstance().isModLoaded(id)) {
                String className = basePackage + "." + entry.getValue();
                LOGGER.info("Loading compat for mod {}", id);
                try {
                    Class<?> compatClass = Class.forName(className);
                    compatClass.getDeclaredConstructor().newInstance();
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
