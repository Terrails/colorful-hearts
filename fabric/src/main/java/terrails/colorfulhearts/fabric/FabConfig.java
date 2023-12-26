package terrails.colorfulhearts.fabric;

import com.electronwill.nightconfig.core.ConfigSpec;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.file.FileNotFoundAction;
import com.electronwill.nightconfig.core.file.FileWatcher;
import com.electronwill.nightconfig.core.io.ParsingException;
import com.electronwill.nightconfig.core.io.WritingException;
import com.electronwill.nightconfig.core.io.WritingMode;
import com.electronwill.nightconfig.core.utils.CommentedConfigWrapper;
import com.electronwill.nightconfig.toml.TomlFormat;
import net.fabricmc.loader.api.FabricLoader;
import terrails.colorfulhearts.CColorfulHearts;
import terrails.colorfulhearts.config.ConfigOption;
import terrails.colorfulhearts.config.Configuration;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static terrails.colorfulhearts.CColorfulHearts.LOGGER;

public class FabConfig extends CommentedConfigWrapper<CommentedFileConfig> implements CommentedFileConfig {

    private final Runnable NO_OP = () -> {}, CONFIG_RELOAD = this::reload;

    private final List<ConfigOption<?, ?>> options = new ArrayList<>();
    private final ConfigSpec spec = new ConfigSpec();
    private final FileWatcher watcher = FileWatcher.defaultInstance();

    private boolean firstLoad = true;

    public FabConfig() {
        super(CommentedFileConfig
                      .builder(FabricLoader.getInstance().getConfigDir().resolve(CColorfulHearts.MOD_ID + ".toml"), TomlFormat.instance())
                      .onFileNotFound(FileNotFoundAction.CREATE_EMPTY)
                      .writingMode(WritingMode.REPLACE)
                      .sync()
                      .build()
        );

        for (Object instance : new Object[]{Configuration.HEALTH, Configuration.ABSORPTION}) {
            for (Field field : instance.getClass().getDeclaredFields()) {
                try {
                    if (field.get(instance) instanceof ConfigOption<?, ?> option) {
                        this.options.add(option);

                        if (option.getRawDefault() instanceof List<?> list) {
                            this.spec.defineList(option.getPath(), list, option.getOptionValidator());
                        } else {
                            this.spec.define(option.getPath(), option.getRawDefault(), option.getOptionValidator());
                        }
                    } else {
                        LOGGER.debug("Skipping {} field in {} as it is not a ConfigOption", field.getName(), instance.getClass().getName());
                    }
                } catch (IllegalAccessException e) {
                    LOGGER.error("Could not process {} field in {}", field.getName(), instance.getClass().getName(), e);
                }
            }
        }

        this.load();
    }

    @Override
    public File getFile() {
        return this.config.getFile();
    }

    @Override
    public Path getNioPath() {
        return this.config.getNioPath();
    }

    @Override
    public void save() {
        LOGGER.debug("Saving {} config file.", getFile().getName());
        this.config.save();
    }

    @Override
    public void load() {
        while (true) {
            try {
                if (this.firstLoad) LOGGER.info("Loading {} config file", getFile().getName());
                this.config.load();
                break;
            } catch (ParsingException e) {
                LOGGER.error("Failed to load {} due to a parsing error", getFile().getName(), e);
                this.disableWatcher();
                String deformedFile = CColorfulHearts.MOD_ID + "-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH.mm.ss")) + "-deformed.toml";
                try {
                    Files.move(getNioPath(), FabricLoader.getInstance().getConfigDir().resolve(deformedFile));
                    LOGGER.error("Deformed config file renamed to {}", deformedFile);
                } catch (IOException ee) {
                    LOGGER.error("Moving deformed config file failed", ee);
                    throw new RuntimeException("Moving deformed config file failed: " + e);
                }
                this.createFile();
                this.enableWatcher();
            }
        }

        boolean modified = false;
        if (!this.spec.isCorrect(this.config)) {
            int corrections = this.spec.correct(this.config, (action, pathList, incorrectValue, correctedValue) -> {
                String path = String.join(".", pathList);
                switch (action) {
                    case ADD -> LOGGER.info("Missing entry {} = {} added to {}", path, correctedValue, getFile().getName());
                    case REMOVE -> LOGGER.info("Invalid entry {} removed from {}", path, getFile().getName());
                    case REPLACE -> LOGGER.info("Invalid entry {}: value {} replaced by {} in {}", path, incorrectValue, correctedValue, getFile().getName());
                }
            });

            if (corrections > 0) {
                LOGGER.info("{} correction(s) applied to {} config file", corrections, getFile().getName());
                modified = true;
            }
        }

        for (ConfigOption<?, ?> option : this.options) {
            if (!this.spec.isDefined(option.getPath())) continue;

            String oldComment = this.getComment(option.getPath());
            String newComment = option.getComment();

            if (oldComment != null && newComment != null) {
                oldComment = oldComment.replaceAll("\r\n", "\n");
                newComment = newComment.replaceAll("\r\n", "\n");
            }

            if ((oldComment == null && newComment != null) || (oldComment != null && !oldComment.equals(newComment))) {
                this.setComment(option.getPath(), newComment);
                modified = true;
            }

        }

        if (modified) {
            LOGGER.info("Saving corrected {} config file", getFile().getName());
            this.save();
        }

        if (this.firstLoad) {
            for (ConfigOption<?, ?> option : this.options) {
                option.initialize(() -> this.get(option.getPath()), v -> this.set(option.getPath(), v));
                option.reload();
            }
            this.enableWatcher();
            this.firstLoad = false;
        } else {
            this.options.forEach(ConfigOption::reload);
        }
    }

    public void reload() {
        LOGGER.info("Reloading {} config file", getFile().getName());
        this.load();
    }

    @Override
    public void close() {
        this.watcher.removeWatch(getFile());
        this.config.close();
    }

    private void createFile() {
        if (Files.notExists(getNioPath())) {
            try {
                FileNotFoundAction.CREATE_EMPTY.run(getNioPath(), TomlFormat.instance());
            } catch (IOException e) {
                this.config.close();
                throw new WritingException("An exception occurred while executing the FileNotFoundAction for file " + getNioPath(), e);
            }
        }
    }

    private void disableWatcher() {
        try {
            this.watcher.setWatch(getNioPath(), NO_OP);
        } catch (IOException e) {
            this.config.close();
            throw new RuntimeException("Unable to disable a file watcher", e);
        }
    }

    private void enableWatcher() {
        try {
            this.watcher.setWatch(getNioPath(), CONFIG_RELOAD);
        } catch (IOException e) {
            this.config.close();
            throw new RuntimeException("Unable to add a file watcher", e);
        }
    }
}
