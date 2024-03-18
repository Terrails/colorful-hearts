package terrails.colorfulhearts.neoforge;

import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.javafmlmod.FMLJavaModLoadingContext;
import net.neoforged.neoforge.client.ConfigScreenHandler;
import net.neoforged.neoforge.client.event.RegisterSpriteSourceTypesEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.NeoForge;
import terrails.colorfulhearts.CColorfulHearts;
import terrails.colorfulhearts.config.screen.ConfigurationScreen;
import terrails.colorfulhearts.neoforge.api.event.ForgeHeartChangeEvent;
import terrails.colorfulhearts.render.HeartRenderer;
import terrails.colorfulhearts.render.TabHeartRenderer;
import terrails.colorfulhearts.render.atlas.sources.ColoredHearts;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import static terrails.colorfulhearts.CColorfulHearts.LOGGER;

public class ColorfulHearts {

    public static ModConfigSpec CONFIG_SPEC;

    private static final Map<String, String> COMPAT = Map.of(
            "appleskin", "AppleSkinForgeCompat"
    );

    public ColorfulHearts() {
        final String fileName = CColorfulHearts.MOD_ID + ".toml";
        CONFIG_SPEC = ForgeConfig.setup(fileName);

        final ModLoadingContext context = ModLoadingContext.get();
        context.registerConfig(ModConfig.Type.CLIENT, CONFIG_SPEC, fileName);
        context.registerExtensionPoint(
                ConfigScreenHandler.ConfigScreenFactory.class,
                () -> new ConfigScreenHandler.ConfigScreenFactory((mc, lastScreen) -> new ConfigurationScreen(lastScreen))
        );

        final IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(ForgeConfig::load);
        bus.addListener(ForgeConfig::reload);
        bus.addListener(this::setup);
        bus.addListener(this::registerSprites);

        NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST, RenderEventHandler.INSTANCE::renderHearts);
        NeoForge.EVENT_BUS.addListener(this::heartChanged);
    }

    private void setup(final FMLClientSetupEvent event) {
        this.setupCompat();
    }

    private void registerSprites(final RegisterSpriteSourceTypesEvent event) {
        event.register(CColorfulHearts.SPRITE_NAME, ColoredHearts.CODEC);
    }

    private void heartChanged(ForgeHeartChangeEvent event) {
        // force an update to heart colors when config GUI is updated
        HeartRenderer.INSTANCE.lastHealthType = null;
        TabHeartRenderer.INSTANCE.lastHealth = 0;
    }

    private void setupCompat() {
        final String basePackage = "terrails.colorfulhearts.neoforge.compat";

        for (Map.Entry<String, String> entry : COMPAT.entrySet()) {
            String id = entry.getKey();
            if (ModList.get().isLoaded(id)) {
                String className = basePackage + "." + entry.getValue();
                LOGGER.info("Loading compat for mod {}.", id);
                try {
                    Class<?> compatClass = Class.forName(className);
                    compatClass.getDeclaredConstructor().newInstance();
                } catch (ClassNotFoundException e) {
                    LOGGER.error("Failed to load compat as {} does not exist", className, e);
                } catch (NoSuchMethodException e) {
                    LOGGER.error("Failed to load compat as {} does not have an empty constructor", className, e);
                } catch (IllegalAccessException e) {
                    LOGGER.error("Failed to load compat as {} does not have an empty public constructor", className, e);
                } catch (InstantiationException e) {
                    LOGGER.error("Failed to load compat as {} is an abstract class", className, e);
                } catch (InvocationTargetException e) {
                    LOGGER.error("Failed to load compat {} as an unknown error was thrown", className, e);
                }
            } else {
                LOGGER.debug("Skipped loading compat for mod {} as it is not present", id);
            }
        }
    }
}
