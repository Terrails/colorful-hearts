package terrails.colorfulhearts.config;

import java.util.function.Predicate;

public class SimpleConfigOption<T> extends ConfigOption<T, T> {

    public SimpleConfigOption(String path, String comment, T defaultValue, Predicate<Object> optionValidator) {
        super(path, comment, defaultValue, null, null, optionValidator);
    }

    public SimpleConfigOption(String path, String comment, T defaultValue) {
        this(path, comment, defaultValue, o -> o != null && defaultValue.getClass().isAssignableFrom(o.getClass()));
    }

    @Override
    public void reload() {
        this.cachedValue = this.getRaw();
    }

    @Override
    public T getDefault() {
        return this.getRawDefault();
    }

    @Override
    public T get() {
        return this.cachedValue;
    }

    @Override
    public void set(T value) {
        this.setRaw(value);
    }
}