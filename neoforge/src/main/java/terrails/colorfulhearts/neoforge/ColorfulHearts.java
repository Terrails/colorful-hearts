package terrails.colorfulhearts.neoforge;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterSpriteSourceTypesEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.NeoForge;

import terrails.colorfulhearts.CColorfulHearts;
import terrails.colorfulhearts.config.ConfigOption;
import terrails.colorfulhearts.config.ConfigUtils;
import terrails.colorfulhearts.config.Configuration;
import terrails.colorfulhearts.config.screen.ConfigurationScreen;
import terrails.colorfulhearts.neoforge.render.RenderEventHandler;
import terrails.colorfulhearts.render.atlas.sources.ColoredHearts;
import net.minecraft.resources.ResourceLocation;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import static terrails.colorfulhearts.CColorfulHearts.LOGGER;

@Mod(value = CColorfulHearts.MOD_ID, dist = Dist.CLIENT)
public class ColorfulHearts {

    public static ModConfigSpec CONFIG_SPEC;
    private static final List<ConfigOption<?, ?>> CONFIG_OPTIONS = new ArrayList<>();

    public ColorfulHearts(final IEventBus bus, final ModContainer container) {
        CColorfulHearts.setup(new PlatformProxyImpl());
        CONFIG_SPEC = this.setupConfig();
        container.registerConfig(ModConfig.Type.CLIENT, CONFIG_SPEC, CColorfulHearts.MOD_ID + ".toml");
        container.registerExtensionPoint(IConfigScreenFactory.class, (Supplier<IConfigScreenFactory>) () -> (mc, lastScreen) -> new ConfigurationScreen(lastScreen));

        bus.addListener(this::setup);
        bus.addListener(this::registerSprite);
        bus.addListener(this::loadConfig);
        bus.addListener(this::reloadConfig);
    }

    private void setup(final FMLClientSetupEvent event) {
        NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST, RenderEventHandler.INSTANCE::renderHearts);
    }

    private void registerSprite(final RegisterSpriteSourceTypesEvent event) {
        event.register(ResourceLocation.fromNamespaceAndPath(CColorfulHearts.MOD_ID, "colored_hearts"), ColoredHearts.TYPE);
    }

    private void loadConfig(final ModConfigEvent.Loading event) {
        LOGGER.info("Loading {} config file", event.getConfig().getFileName());
        CONFIG_OPTIONS.forEach(ConfigOption::reload);
        ConfigUtils.loadColoredHearts();
        ConfigUtils.loadStatusEffectHearts();
        LOGGER.debug("Loaded {} config file", event.getConfig().getFileName());
    }

    private void reloadConfig(final ModConfigEvent.Reloading event) {
        LOGGER.info("Reloading {} config file", event.getConfig().getFileName());
        CONFIG_OPTIONS.forEach(ConfigOption::reload);
        ConfigUtils.loadColoredHearts();
        ConfigUtils.loadStatusEffectHearts();
        LOGGER.debug("Reloaded {} config file", event.getConfig().getFileName());
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private ModConfigSpec setupConfig() {
        ModConfigSpec.Builder specBuilder = new ModConfigSpec.Builder();
        for (Object instance : new Object[]{Configuration.HEALTH, Configuration.ABSORPTION}) {
            for (Field field : instance.getClass().getDeclaredFields()) {
                try {
                    if (field.get(instance) instanceof ConfigOption option) {
                        CONFIG_OPTIONS.add(option);

                        ModConfigSpec.ConfigValue value;
                        if (option.getRawDefault() instanceof List<?> list) {
                            value = specBuilder.comment(option.getComment()).defineList(option.getPath(), list, null, option.getOptionValidator());
                        } else {
                            value = specBuilder.comment(option.getComment()).define(option.getPath(), option.getRawDefault(), option.getOptionValidator());
                        }
                        option.initialize(value, value::set);
                    } else {
                        LOGGER.debug("Skipping {} field in {} as it is not a ConfigOption", field.getName(), instance.getClass().getName());
                    }
                } catch (IllegalAccessException e) {
                    LOGGER.error("Could not process {} field in {}", field.getName(), instance.getClass().getName(), e);
                }
            }
        }
        return specBuilder.build();
    }
}
