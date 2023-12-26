package terrails.colorfulhearts.config;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static terrails.colorfulhearts.CColorfulHearts.LOGGER;

public class ConfigOption<C, O> implements Supplier<O> {

    // night-config data
    private final String path;
    private final String comment;
    private final C defaultValue;
    private final Predicate<Object> optionValidator;

    // config getters and setters
    private Supplier<C> valueGetter;
    private Consumer<C> valueSetter;

    // custom type serialization and deserialization
    private final Function<O, C> valueSerializer;
    private final Function<C, O> valueDeserializer;

    // updated on every config reload
    protected O cachedValue;

    public ConfigOption(String path, String comment, C defaultValue, Function<O, C> serializer, Function<C, O> deserializer, Predicate<Object> optionValidator) {
        this.path = path;
        this.defaultValue = defaultValue;
        this.optionValidator = optionValidator;
        this.valueSerializer = serializer;
        this.valueDeserializer = deserializer;

        if (!comment.isEmpty()) {
            comment += "\n";
        }

        if (defaultValue instanceof List<?> list) {
            if (list.isEmpty()) {
                comment += "Default: []";
            } else {
                Stream<String> stream = list.stream().map(Object::toString);
                if (list.get(0) instanceof String) {
                    comment += "Default: [" + stream.map(s -> '"' + s + '"').collect(Collectors.joining(", ")) + "]";
                } else {
                    comment += "Default: [" + stream.collect(Collectors.joining(", ")) + "]";
                }
            }
        } else if (defaultValue instanceof String) {
            comment += "Default: \"" + defaultValue + '"';
        } else {
            comment += "Default: " + defaultValue;
        }

        this.comment = comment;
    }

    public ConfigOption(String path, String comment, C defaultValue, Function<O, C> serializer, Function<C, O> deserializer) {
        this(path, comment, defaultValue, serializer, deserializer, o -> o != null && defaultValue.getClass().isAssignableFrom(o.getClass()));
    }

    public void initialize(Supplier<C> valueGetter, Consumer<C> valueSetter) {
        if (this.isInitialized()) {
            LOGGER.error("ConfigOption {} is already initialized.", this.path);
            throw new RuntimeException("ConfigOption " + this.path + " is already initialized");
        }

        this.valueGetter = valueGetter;
        this.valueSetter = valueSetter;
    }

    public void reload() {
        this.cachedValue = this.valueDeserializer.apply(this.getRaw());
    }

    private boolean isInitialized() {
        return this.valueGetter != null && this.valueSetter != null;
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

    public O getDefault() {
        return this.valueDeserializer.apply(this.defaultValue);
    }

    public C getRawDefault() {
        return this.defaultValue;
    }

    @Override
    public O get() {
        if (!this.isInitialized()) {
            LOGGER.error("ConfigOption {} has not yet been initialized.", this.path);
            throw new RuntimeException("ConfigOption " + this.path + " has not yet been initialized");
        }

        return this.cachedValue;
    }

    public C getRaw() {
        if (!this.isInitialized()) {
            LOGGER.error("ConfigOption {} has not yet been initialized.", this.path);
            throw new RuntimeException("ConfigOption " + this.path + " has not yet been initialized");
        }

        return this.valueGetter.get();
    }

    public void set(O value) {
        if (!this.isInitialized()) {
            LOGGER.error("ConfigOption {} has not yet been initialized.", this.path);
            throw new RuntimeException("ConfigOption " + this.path + " has not yet been initialized");
        }

        this.valueSetter.accept(this.valueSerializer.apply(value));
        this.reload();
    }

    public void setRaw(C value) {
        if (!this.isInitialized()) {
            LOGGER.error("ConfigOption {} has not yet been initialized.", this.path);
            throw new RuntimeException("ConfigOption " + this.path + " has not yet been initialized");
        }

        this.valueSetter.accept(value);
        this.reload();
    }
}
