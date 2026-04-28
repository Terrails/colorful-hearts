package terrails.colorfulhearts.fabric;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.SpriteSourceRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ObjectShare;
import terrails.colorfulhearts.CColorfulHearts;
import terrails.colorfulhearts.fabric.config.FabConfig;
import terrails.colorfulhearts.render.atlas.sources.ColoredHearts;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import static terrails.colorfulhearts.CColorfulHearts.LOGGER;

public class ColorfulHearts implements ClientModInitializer {

    public static FabConfig CONFIG;

    private static final Map<String, String> COMPAT = Map.of(
            "overflowingbars", "OverflowingBarsCompat"
    );

    @Override
    public void onInitializeClient() {
        CColorfulHearts.setup(new PlatformProxyImpl());
        CONFIG = new FabConfig();
        this.setupSpriteSource();
        this.setupObjectShare();
        this.setupCompat();
    }

    private void setupSpriteSource() {
        SpriteSourceRegistry.register(CColorfulHearts.location("colored_hearts"), ColoredHearts.CODEC);
    }

    private void setupObjectShare() {
        final ObjectShare objectShare = FabricLoader.getInstance().getObjectShare();

        // Absorption
        // keep this for now in case some mods depended on it
        objectShare.putIfAbsent("colorfulhearts:absorption_over_health", false);

        // Allows other mods to force use of hardcore heart textures
        // Default vanilla behaviour (hardcore world) if false
        objectShare.putIfAbsent("colorfulhearts:force_hardcore_hearts", false);
    }

    private void setupCompat() {
        final String basePackage = "terrails.colorfulhearts.fabric.compat";

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
