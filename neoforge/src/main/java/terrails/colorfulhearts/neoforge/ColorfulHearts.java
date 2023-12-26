package terrails.colorfulhearts.neoforge;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.file.FileNotFoundAction;
import com.electronwill.nightconfig.core.io.WritingMode;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.javafmlmod.FMLJavaModLoadingContext;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.client.ConfigScreenHandler;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.NeoForge;
import terrails.colorfulhearts.CColorfulHearts;
import terrails.colorfulhearts.config.ConfigOption;
import terrails.colorfulhearts.config.Configuration;
import terrails.colorfulhearts.config.screen.ConfigurationScreen;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static terrails.colorfulhearts.CColorfulHearts.LOGGER;

class ColorfulHearts {

    public static ModConfigSpec CONFIG_SPEC;

    private static final Map<String, String> COMPAT = Map.of(
            "appleskin", "AppleSkinForgeCompat"
    );

    public ColorfulHearts() {
        final String configFile = CColorfulHearts.MOD_ID + ".toml";
        CONFIG_SPEC = this.setupConfig(configFile);

        final ModLoadingContext context = ModLoadingContext.get();
        context.registerConfig(ModConfig.Type.CLIENT, CONFIG_SPEC, configFile);
        context.registerExtensionPoint(
                ConfigScreenHandler.ConfigScreenFactory.class,
                () -> new ConfigScreenHandler.ConfigScreenFactory((mc, lastScreen) -> new ConfigurationScreen(lastScreen))
        );

        final IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(this::setup);

        NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST, RenderEventHandler.INSTANCE::renderHearts);
    }

    private void setup(final FMLClientSetupEvent event) {
        this.setupCompat();
    }

    private ModConfigSpec setupConfig(String configFile) {
        final Object[] configs = { Configuration.HEALTH, Configuration.ABSORPTION };

        // initialize ConfigSpec
        final ModConfigSpec.Builder specBuilder = new ModConfigSpec.Builder();
        for (Object object : configs) {
            for (Field field : object.getClass().getDeclaredFields()) {
                try {
                    field.setAccessible(true);

                    if (field.get(object) instanceof ConfigOption<?> option) {

                        if (option.getDefault() instanceof List<?> list) {
                            String listStr = list.stream().map(Object::toString).collect(Collectors.joining(", "));
                            specBuilder.comment(option.getComment() + "\nDefault: [" + listStr + "]").defineList(option.getPath(), list, option.getOptionValidator());
                        } else {
                            specBuilder.comment(option.getComment() + "\nDefault: " + option.getDefault().toString()).define(option.getPath(), option.getDefault(), option.getOptionValidator());
                        }
                    }

                } catch (Exception e) {
                    LOGGER.error("Could not process {} in {}", field.getName(), configs, e);
                }
            }
        }
        final ModConfigSpec spec = specBuilder.build();

        final Path path = FMLPaths.CONFIGDIR.get().resolve(configFile);
        LOGGER.debug("Loading config file {}", path);

        final CommentedFileConfig config = CommentedFileConfig.builder(path)
                .sync()
                .autoreload()
                .onFileNotFound(FileNotFoundAction.CREATE_EMPTY)
                .writingMode(WritingMode.REPLACE)
                .build();

        LOGGER.debug("Built TOML config {}", path);
        config.load();
        LOGGER.debug("Loaded TOML config {}", path);
        spec.setConfig(config);

        // Initialize values from ConfigOption objects
        for (Object object : configs) {
            for (Field field : object.getClass().getDeclaredFields()) {
                try {
                    field.setAccessible(true);

                    if (field.get(object) instanceof ConfigOption<?> option) {
                        option.initialize(() -> config.get(option.getPath()), (val) -> config.set(option.getPath(), val));
                    }

                } catch (Exception e) {
                    LOGGER.error("Could not process {} in {}", field.getName(), configs, e);
                }
            }
        }
        return spec;
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
