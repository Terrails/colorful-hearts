package terrails.colorfulhearts.fabric;

import com.electronwill.nightconfig.core.ConfigSpec;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.file.FileNotFoundAction;
import com.electronwill.nightconfig.core.io.ParsingException;
import com.electronwill.nightconfig.core.io.WritingMode;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ObjectShare;
import terrails.colorfulhearts.CColorfulHearts;
import terrails.colorfulhearts.config.ConfigOption;
import terrails.colorfulhearts.config.Configuration;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import static terrails.colorfulhearts.CColorfulHearts.LOGGER;

public class ColorfulHearts implements ClientModInitializer {

    public static CommentedFileConfig FILE_CONFIG;

    @Override
    public void onInitializeClient() {
        setupConfig();
        setupObjectShare();
    }

    private static void setupConfig() {
        final Object[] configObjects = { Configuration.HEALTH, Configuration.ABSORPTION };
        final String fileName = CColorfulHearts.MOD_ID + ".toml";

        final Path configDir = FabricLoader.getInstance().getConfigDir();
        final Path configPath = configDir.resolve(fileName);

        final ConfigSpec spec = new ConfigSpec();
        for (Object object : configObjects) {
            for (Field field : object.getClass().getDeclaredFields()) {
                try {
                    field.setAccessible(true);

                    if (field.get(object) instanceof ConfigOption<?> option) {

                        if (option.getDefault() instanceof List<?> list) {
                            spec.defineList(option.getPath(), list, option.getOptionValidator());
                        } else {
                            spec.define(option.getPath(), option.getDefault(), option.getOptionValidator());
                        }
                    }

                } catch (Exception e) {
                    LOGGER.error("Could not process {} in {}", field.getName(), configObjects, e);
                }
            }
        }

        while (true) {
            LOGGER.debug("Initializing {} config file", fileName);
            final CommentedFileConfig config = CommentedFileConfig.builder(configPath)
                    .sync()
                    .autoreload()
                    .onFileNotFound(FileNotFoundAction.CREATE_EMPTY)
                    .writingMode(WritingMode.REPLACE)
                    .build();

            try {
                LOGGER.info("Loading {} config file", fileName);
                config.load();

                // correct multiple times as there is some odd issue where categories are created empty.
                // running correct once again on the empty categories fixes the file.
                while (!spec.isCorrect(config)) {
                    int corrections = spec.correct(config, (action, path, incorrectValue, correctedValue) -> {
                        String pathString = String.join(".", path);
                        switch (action) {
                            case ADD -> LOGGER.info("Missing entry {} = {} added to {}", pathString, correctedValue, fileName);
                            case REMOVE -> LOGGER.info("Invalid entry {} removed from {}", pathString, fileName);
                            case REPLACE -> LOGGER.info("Invalid entry {}: value {} replaced by {} in {}", path, incorrectValue, correctedValue, fileName);
                        }
                    });
                    LOGGER.info("{} correction(s) applied to {} config file", corrections, fileName);
                    config.save();
                }

                // Apply comments from ConfigOption objects, also adds a new line with default values
                for (Object object : configObjects) {
                    for (Field field : object.getClass().getDeclaredFields()) {
                        try {
                            field.setAccessible(true);

                            // Double check that ConfigOption path is in ConfigSpec
                            if (field.get(object) instanceof ConfigOption<?> option && spec.isDefined(option.getPath())) {

                                String defaultValueStr;
                                if (option.getDefault() instanceof List<?> list) {
                                    String listStr = list.stream().map(Object::toString).collect(Collectors.joining(", "));
                                    defaultValueStr = "\nDefault: [" + listStr + "]";
                                } else {
                                    defaultValueStr = "\nDefault: " + option.getDefault().toString();
                                }

                                config.setComment(option.getPath(), option.getComment() + defaultValueStr);
                                option.initialize(() -> config.get(option.getPath()), (val) -> config.set(option.getPath(), val));
                            }
                        } catch (Exception e) {
                            LOGGER.error("Could not process {} in {}", field.getName(), configObjects, e);
                        }
                    }
                }

                // Leaving it open in order to be able to get updated values and save again
                config.save();
                FILE_CONFIG = config;

                LOGGER.info("Successfully loaded {} config file", fileName);
                break;
            } catch (ParsingException e) {
                config.close();
                LOGGER.error("Failed to load '{}' due to a parsing error.", fileName, e);

                String deformedFile = (CColorfulHearts.MOD_ID + "-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH.mm.ss")) + ".toml");
                try {
                    Files.move(configPath, configDir.resolve(deformedFile));
                    LOGGER.error("Deformed config file renamed to '{}'", deformedFile);
                } catch (IOException ee) {
                    LOGGER.error("Moving deformed config file failed...", ee);
                }
            }
        }
    }

    private static void setupObjectShare() {
        final ObjectShare objectShare = FabricLoader.getInstance().getObjectShare();

        // Absorption
        objectShare.putIfAbsent("colorfulhearts:absorption_over_health", Configuration.ABSORPTION.renderOverHealth.get());

        // Allows other mods to force use of hardcore heart textures
        // Default vanilla behaviour (hardcore world) if false
        objectShare.putIfAbsent("colorfulhearts:force_hardcore_hearts", false);
    }

}
