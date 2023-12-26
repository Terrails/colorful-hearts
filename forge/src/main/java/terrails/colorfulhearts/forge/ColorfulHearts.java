package terrails.colorfulhearts.forge;

import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import terrails.colorfulhearts.CColorfulHearts;
import terrails.colorfulhearts.config.screen.ConfigurationScreen;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import static terrails.colorfulhearts.CColorfulHearts.LOGGER;

public class ColorfulHearts {

    private static final Map<String, String> COMPAT = Map.of(
            "appleskin", "AppleSkinForgeCompat"
    );

    public static ForgeConfigSpec CONFIG_SPEC;

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

        MinecraftForge.EVENT_BUS.addListener(EventPriority.LOWEST, RenderEventHandler.INSTANCE::renderHearts);
    }

    private void setup(final FMLClientSetupEvent event) {
        this.setupCompat();
    }

    private void setupCompat() {
        final String basePackage = "terrails.colorfulhearts.forge.compat";

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
