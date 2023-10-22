package terrails.colorfulhearts.forge;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.file.FileNotFoundAction;
import com.electronwill.nightconfig.core.io.WritingMode;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import terrails.colorfulhearts.CColorfulHearts;
import terrails.colorfulhearts.config.ConfigOption;
import terrails.colorfulhearts.config.Configuration;
import terrails.colorfulhearts.config.screen.ConfigurationScreen;

import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

import static terrails.colorfulhearts.CColorfulHearts.LOGGER;

public class ColorfulHearts {

    public static ForgeConfigSpec CONFIG_SPEC;

    private static final String CONFIG_FILE = CColorfulHearts.MOD_ID + ".toml";

    static void initialize() {
        final ColorfulHearts instance = new ColorfulHearts();

        instance.setupConfig();
        final ModLoadingContext context = ModLoadingContext.get();
        context.registerConfig(ModConfig.Type.CLIENT, CONFIG_SPEC, CONFIG_FILE);
        context.registerExtensionPoint(
                ConfigScreenHandler.ConfigScreenFactory.class,
                () -> new ConfigScreenHandler.ConfigScreenFactory((mc, lastScreen) -> new ConfigurationScreen(lastScreen))
        );

        final IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(instance::setup);

        MinecraftForge.EVENT_BUS.register(new RenderEventHandler());
    }

    private void setup(final FMLClientSetupEvent event) {
        CColorfulHearts.setupCommon();
    }

    private void setupConfig() {
        final Object[] configObjects = { Configuration.HEALTH, Configuration.ABSORPTION };

        final ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        for (Object object : configObjects) {
            for (Field field : object.getClass().getDeclaredFields()) {
                try {
                    field.setAccessible(true);

                    if (field.get(object) instanceof ConfigOption<?> option) {

                        if (option.getDefault() instanceof List<?> list) {
                            String listStr = list.stream().map(Object::toString).collect(Collectors.joining(", "));
                            builder.comment(option.getComment() + "\nDefault: [" + listStr + "]").defineList(option.getPath(), list, option.getOptionValidator());
                        } else {
                            builder.comment(option.getComment() + "\nDefault: " + option.getDefault().toString()).define(option.getPath(), option.getDefault(), option.getOptionValidator());
                        }
                    }

                } catch (Exception e) {
                    LOGGER.error("Could not process {} in {}", field.getName(), configObjects);
                    e.printStackTrace();
                }
            }
        }
        CONFIG_SPEC = builder.build();

        final Path path = FMLPaths.CONFIGDIR.get().resolve(CONFIG_FILE);
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
        CONFIG_SPEC.setConfig(config);

        // Initialize values from ConfigOption objects
        for (Object object : configObjects) {
            for (Field field : object.getClass().getDeclaredFields()) {
                try {
                    field.setAccessible(true);

                    if (field.get(object) instanceof ConfigOption<?> option) {
                        option.initialize(() -> config.get(option.getPath()), (val) -> config.set(option.getPath(), val));
                    }

                } catch (Exception e) {
                    LOGGER.error("Could not process {} in {}", field.getName(), configObjects);
                    e.printStackTrace();
                }
            }
        }
    }
}
