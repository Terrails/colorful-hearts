package terrails.colorfulhearts.forge;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.file.FileNotFoundAction;
import com.electronwill.nightconfig.core.io.ParsingException;
import com.electronwill.nightconfig.core.io.WritingMode;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.loading.FMLPaths;
import terrails.colorfulhearts.CColorfulHearts;
import terrails.colorfulhearts.config.ConfigOption;
import terrails.colorfulhearts.config.Configuration;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static terrails.colorfulhearts.CColorfulHearts.LOGGER;

public class ForgeConfig {

    private static final List<ConfigOption<?, ?>> options = new ArrayList<>();

    public static ForgeConfigSpec setup(String fileName) {
        ForgeConfigSpec.Builder specBuilder = new ForgeConfigSpec.Builder();
        for (Object instance : new Object[]{Configuration.HEALTH, Configuration.ABSORPTION}) {
            for (Field field : instance.getClass().getDeclaredFields()) {
                try {
                    if (field.get(instance) instanceof ConfigOption<?, ?> option) {
                        options.add(option);

                        if (option.getRawDefault() instanceof List<?> list) {
                            specBuilder.comment(option.getComment()).defineList(option.getPath(), list, option.getOptionValidator());
                        } else {
                            specBuilder.comment(option.getComment()).define(option.getPath(), option.getRawDefault(), option.getOptionValidator());
                        }
                    } else {
                        LOGGER.debug("Skipping {} field in {} as it is not a ConfigOption", field.getName(), instance.getClass().getName());
                    }
                } catch (IllegalAccessException e) {
                    LOGGER.error("Could not process {} field in {}", field.getName(), instance.getClass().getName(), e);
                }
            }
        }

        ForgeConfigSpec spec = specBuilder.build();

        Path filePath = FMLPaths.CONFIGDIR.get().resolve(fileName);
        CommentedFileConfig config = CommentedFileConfig.builder(filePath)
                .onFileNotFound(FileNotFoundAction.CREATE_EMPTY)
                .writingMode(WritingMode.REPLACE)
                .autoreload()
                .sync()
                .build();

        while (true) {
            try {
                LOGGER.info("Loading {} config file", config.getFile().getName());
                config.load();
                break;
            } catch (ParsingException e) {
                LOGGER.error("Failed to load {} due to a parsing error", config.getFile().getName(), e);
                String deformedFile = CColorfulHearts.MOD_ID + "-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH.mm.ss")) + "-deformed.toml";
                try {
                    Files.move(config.getNioPath(), FMLPaths.CONFIGDIR.get().resolve(deformedFile));
                    LOGGER.error("Deformed config file renamed to {}", deformedFile);
                } catch (IOException ee) {
                    LOGGER.error("Moving deformed config file failed", ee);
                    throw new RuntimeException("Moving deformed config file failed: " + e);
                }
            }
        }
        spec.setConfig(config);

        return spec;
    }

    public static void load(final ModConfigEvent.Loading event) {
        final CommentedConfig config = event.getConfig().getConfigData();
        for (ConfigOption<?, ?> option : options) {
            option.initialize(() -> config.get(option.getPath()), v -> config.set(option.getPath(), v));
            option.reload();
        }
        LOGGER.info("Loading {} config file", event.getConfig().getFileName());
    }

    public static void reload(final ModConfigEvent.Reloading event) {
        LOGGER.info("Reloading {} config file", event.getConfig().getFileName());
        options.forEach(ConfigOption::reload);
    }
}
