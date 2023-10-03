package terrails.colorfulhearts.config;

import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static terrails.colorfulhearts.CColorfulHearts.LOGGER;

public class ConfigOption<T> implements Supplier<T> {

    // Common night-config data
    private final String path;
    private final String comment;
    private final T defaultValue;
    private final Predicate<Object> optionValidator;

    // Mod-Loader dependant values
    private Supplier<T> valueSupplier;
    private Consumer<T> valueSetter;

    public ConfigOption(String path, String comment, T defaultValue, @NotNull Predicate<Object> optionValidator) {
        this.path = path;
        this.comment = comment;
        this.defaultValue = defaultValue;
        this.optionValidator = optionValidator;
    }

    public ConfigOption(String path, String comment, T defaultValue) {
        this(path, comment, defaultValue, o -> o != null && defaultValue.getClass().isAssignableFrom(o.getClass()));
    }

    public void initialize(Supplier<T> valueSupplier, Consumer<T> valueSetter) {
        if (this.isInitialized()) {
            LOGGER.error("ConfigOption already initialized...");
            return;
        }

        this.valueSupplier = valueSupplier;
        this.valueSetter = valueSetter;
    }

    private boolean isInitialized() {
        return this.valueSupplier != null && this.valueSetter != null;
    }

    public String getPath() {
        return this.path;
    }

    public String getComment() {
        return this.comment;
    }

    public Predicate<Object> getOptionValidator() {
        return this.optionValidator;
    }

    public T getDefault() {
        return this.defaultValue;
    }

    @Override
    public T get() {
        if (!this.isInitialized()) {
            LOGGER.error("ConfigOption {} has not yet been initialized. Returning default value...", this.path);
            return this.defaultValue;
        }
        T value = this.valueSupplier.get();
        // For some odd reason the value will be null if its fetched when config is being saved after using the config screen
        if (value == null) {
            return this.defaultValue;
        } else return value;
    }

    public void set(T value) {
        if (!this.isInitialized()) {
            LOGGER.error("ConfigOption {} has not yet been initialized. Doing nothing...", this.path);
            return;
        }
        this.valueSetter.accept(value);
    }

}