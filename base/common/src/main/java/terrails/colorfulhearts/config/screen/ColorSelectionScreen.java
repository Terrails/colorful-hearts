package terrails.colorfulhearts.config.screen;

import terrails.colorfulhearts.CColorfulHearts;
import terrails.colorfulhearts.config.ConfigOption;
import terrails.colorfulhearts.config.ConfigUtils;
import terrails.colorfulhearts.config.Configuration;
import terrails.colorfulhearts.config.SimpleConfigOption;
import terrails.colorfulhearts.config.screen.base.ScrollableWidgetList;
import terrails.colorfulhearts.config.screen.widgets.HeartColorEditBox;
import terrails.colorfulhearts.render.HeartRenderer;
import terrails.colorfulhearts.render.TabHeartRenderer;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

public class ColorSelectionScreen extends Screen {

    private final Screen lastScreen;

    private List<HeartColorEditBox> editBoxes;
    private List<Button> heartTypeButtons;
    private Button saveButton;

    private boolean vanillaHeart;
    private boolean hasChanged;
    private boolean colorsChanged, vanillaChanged;

    private ScrollableWidgetList colorSelectionList;
    private HeartType heartType;

    public ColorSelectionScreen(Screen lastScreen, boolean health) {
        super(Component.translatable(health ? "colorfulhearts.screen.health.title" : "colorfulhearts.screen.absorption.title"));
        this.lastScreen = lastScreen;
        this.heartType = health ? HeartType.HEALTH : HeartType.ABSORBING;
        this.updateHeartType(this.heartType, false);
    }

    @Override
    public void tick() {
        if (this.hasChanged) {
            this.heartTypeButtons.forEach(btn -> btn.active = false);
            this.saveButton.active = this.canApplyChanges();
        } else if (this.saveButton.active) {
            this.rebuildWidgets();
        }
    }

    @Override
    protected void init() {
        super.init();
        this.colorsChanged = false;

        // some basic info
        int width = Minecraft.getInstance().getWindow().getGuiScaledWidth();
        int height = Minecraft.getInstance().getWindow().getGuiScaledHeight();
        int marginY = 32;

        // get previous scroll amount or none if screen was just created
        double prevScrollAmount = this.colorSelectionList == null ? 0 : this.colorSelectionList.scrollAmount();

        // create scrollable list widget and add it to this screen's children
        this.colorSelectionList = this.addRenderableWidget(new ScrollableWidgetList(Minecraft.getInstance(), width, height - marginY * 2, marginY, Button.DEFAULT_HEIGHT + 6));

        // add elements to the scrollable widget
        this.addColorElements();

        // prevent view from being moved to top after widgets get rebuilt
        this.colorSelectionList.setScrollAmount(prevScrollAmount);

        // add buttons for switching between heart types
        int BUTTON_SPACING = 10;
        int BUTTON_WIDTH = 80;
        int BUTTON_HEIGHT = Button.DEFAULT_HEIGHT;
        int startX = (width - BUTTON_WIDTH * 4 - BUTTON_SPACING * 3) / 2;
        int y = (marginY - Button.DEFAULT_HEIGHT) / 2;

        final Button normalHearts = this.addRenderableWidget(Button.builder(
                Component.translatable("colorfulhearts.options.button.hearttype.normal"),
                (btn) -> this.updateHeartType(this.heartType.isHealthType() ? HeartType.HEALTH : HeartType.ABSORBING, true)
        ).pos(startX, y).size(BUTTON_WIDTH, BUTTON_HEIGHT).build());
        normalHearts.active = !(this.heartType == HeartType.HEALTH || this.heartType == HeartType.ABSORBING);

        int x = startX + BUTTON_WIDTH + BUTTON_SPACING;
        final Button poisonedHearts = this.addRenderableWidget(Button.builder(
                Component.translatable("colorfulhearts.options.button.hearttype.poisoned"),
                (btn) -> this.updateHeartType(this.heartType.isHealthType() ? HeartType.HEALTH_POISONED : HeartType.ABSORBING_POISONED, true)
        ).pos(x, y).size(BUTTON_WIDTH, BUTTON_HEIGHT).build());
        poisonedHearts.active = !(this.heartType == HeartType.HEALTH_POISONED || this.heartType == HeartType.ABSORBING_POISONED);

        x += BUTTON_WIDTH + BUTTON_SPACING;
        final Button witheredHearts = this.addRenderableWidget(Button.builder(
                Component.translatable("colorfulhearts.options.button.hearttype.withered"),
                (btn) -> this.updateHeartType(this.heartType.isHealthType() ? HeartType.HEALTH_WITHERED : HeartType.ABSORBING_WITHERED, true)
        ).pos(x, y).size(BUTTON_WIDTH, BUTTON_HEIGHT).build());
        witheredHearts.active = !(this.heartType == HeartType.HEALTH_WITHERED || this.heartType == HeartType.ABSORBING_WITHERED);

        x += BUTTON_WIDTH + BUTTON_SPACING;
        final Button frozenHearts = this.addRenderableWidget(Button.builder(
                Component.translatable("colorfulhearts.options.button.hearttype.frozen"),
                (btn) -> this.updateHeartType(this.heartType.isHealthType() ? HeartType.HEALTH_FROZEN : HeartType.ABSORBING_FROZEN, true)
        ).pos(x, y).size(BUTTON_WIDTH, BUTTON_HEIGHT).build());
        frozenHearts.active = !(this.heartType == HeartType.HEALTH_FROZEN || this.heartType == HeartType.ABSORBING_FROZEN);

        this.heartTypeButtons = List.of(normalHearts, poisonedHearts, witheredHearts, frozenHearts);

        // add save & cancel buttons
        BUTTON_WIDTH = Button.SMALL_WIDTH;
        BUTTON_SPACING = 30;
        startX = (width - BUTTON_WIDTH * 2 - BUTTON_SPACING) / 2;
        y = height - (BUTTON_HEIGHT / 2) - (marginY / 2);

        this.saveButton = this.addRenderableWidget(Button.builder(
                Component.translatable("colorfulhearts.options.button.confirmsave"),
                (btn) -> {
                    this.saveConfig();
                    // send a value to ConfigurationScreen to mark for resourcepack restart
                    this.onClose();
                }
        ).pos(startX, y).size(BUTTON_WIDTH, BUTTON_HEIGHT).build());
        this.saveButton.active = false;

        x = startX + BUTTON_WIDTH + BUTTON_SPACING;
        this.addRenderableWidget(Button.builder(
                Component.translatable("colorfulhearts.options.button.cancel"),
                (btn) -> this.onClose()
        ).pos(x, y).size(BUTTON_WIDTH, BUTTON_HEIGHT).build());

        if (this.hasChanged) {
            this.heartTypeButtons.forEach(btn -> btn.active = false);
        }

        if (this.canApplyChanges()) {
            this.saveButton.active = true;
        }
    }

    @Override
    public void onClose() {
        assert this.minecraft != null;
        this.minecraft.setScreen(this.lastScreen);
        if (this.colorsChanged) {
            // recreates texture atlas
            this.minecraft.reloadResourcePacks();
            ConfigUtils.loadColoredHearts();
            ConfigUtils.loadStatusEffectHearts();
            HeartRenderer.INSTANCE.lastHealth = 0;
            TabHeartRenderer.INSTANCE.lastHealth = 0;
            CColorfulHearts.PROXY.heartUpdateEvent();
        } else if (this.vanillaChanged) {
            ConfigUtils.loadColoredHearts();
            ConfigUtils.loadStatusEffectHearts();
            HeartRenderer.INSTANCE.lastHealth = 0;
            TabHeartRenderer.INSTANCE.lastHealth = 0;
            CColorfulHearts.PROXY.heartUpdateEvent();
        }
    }

    public void updateHeartType(HeartType type, boolean rebuildWidgets) {
        this.heartType = type;
        this.editBoxes = null;
        this.vanillaHeart = this.hasVanillaVariant() && type.isVanillaVariantPresent();

        if (rebuildWidgets) {
            this.rebuildWidgets();
        }
    }

    public boolean hasVanillaVariant() {
        // all health hearts have a vanilla variant, absorption only normal variant
        return this.heartType.isHealthType() || (this.heartType == HeartType.ABSORBING);
    }

    private void addColorElements() {
        final int ELEMENTS_PER_ROW = 4;
        final int ELEMENT_SPACING = 6;
        final int EDIT_BOX_WIDTH = 60;
        final int BUTTON_DIMS = Button.DEFAULT_HEIGHT;
        final int BUTTON_SPACING = 3;

        int screenWidth = Minecraft.getInstance().getWindow().getGuiScaledWidth();
        int totalElementWidth = (ELEMENTS_PER_ROW * EDIT_BOX_WIDTH) + (ELEMENTS_PER_ROW * BUTTON_DIMS) + ((ELEMENTS_PER_ROW - 1) * ELEMENT_SPACING) + ((ELEMENTS_PER_ROW) * BUTTON_SPACING);
        int startX = (screenWidth - totalElementWidth) / 2;

        List<AbstractWidget> widgets = new ArrayList<>();

        int OFFSET = 0;
        if (this.hasVanillaVariant()) {
            OFFSET = 1;

            final Supplier<Component> componentSupplier = () -> Component.translatable(
                    this.vanillaHeart ? "colorfulhearts.options.button.vanillaheart.true" : "colorfulhearts.options.button.vanillaheart.false"
            );
            widgets.add(Button.builder(componentSupplier.get(), (btn) -> {
                this.vanillaHeart = !this.vanillaHeart;
                this.hasChanged = this.haveValuesChanged();
                btn.setMessage(componentSupplier.get());
                this.rebuildWidgets();
            }).pos(startX, 0).size(BUTTON_DIMS + BUTTON_SPACING + EDIT_BOX_WIDTH, Button.DEFAULT_HEIGHT).build());
        }

        if (this.editBoxes == null) {
            this.editBoxes = new LinkedList<>();
            for (String color : this.heartType.getConfigOption().getRaw()) {
                final HeartColorEditBox box = new HeartColorEditBox(this.font, 0, 0, 0, 0, this.heartType);
                box.setValue(color);
                this.editBoxes.add(box);
            }
        }

        final int elementCount = this.editBoxes.size();
        for (int i = 0; i < elementCount; i++) {

            // if not normal variant and if there's vanilla and 1 color OR 2 colors
            boolean stop = this.heartType.isHealthType() && this.heartType != HeartType.HEALTH && ((i > 0 && this.vanillaHeart) || i > 1);
            if (stop) {
                this.colorSelectionList.addEntry(new ScrollableWidgetList.Entry(widgets));
                break;
            }

            int column = (i + OFFSET) % ELEMENTS_PER_ROW;
            int x = startX + column * (EDIT_BOX_WIDTH + BUTTON_DIMS + ELEMENT_SPACING + BUTTON_SPACING);

            final int i_ = i;
            final Button button = Button.builder(
                    Component.literal("-").withStyle(ChatFormatting.RED),
                    (btn) -> {
                        this.editBoxes.remove(i_);
                        this.hasChanged = this.haveValuesChanged();
                        this.rebuildWidgets();
                    }
            ).pos(x, 0).size(BUTTON_DIMS, BUTTON_DIMS).build();

            HeartColorEditBox box = this.editBoxes.get(i);
            box = new HeartColorEditBox(this.font, x + BUTTON_DIMS + BUTTON_SPACING, 0, EDIT_BOX_WIDTH - 2, Button.DEFAULT_HEIGHT, box, this.heartType);
            box.setResponder((str) -> this.hasChanged = this.haveValuesChanged());
            this.editBoxes.set(i, box);

            widgets.add(button);
            widgets.add(box);

            // row finished
            if ((column + 1) == ELEMENTS_PER_ROW) {
                this.colorSelectionList.addEntry(new ScrollableWidgetList.Entry(widgets));
                widgets.clear();
            }

            // last element
            if ((i + 1) == elementCount) {
                boolean canAddMoreHearts = !this.heartType.isEffectType() || !this.hasEnoughColors();

                if (canAddMoreHearts) {
                    column = (column + 1) % ELEMENTS_PER_ROW;
                    x = startX + column * (EDIT_BOX_WIDTH + BUTTON_DIMS + ELEMENT_SPACING + BUTTON_SPACING);
                    final Button addButton = Button.builder(
                            Component.literal("+").withStyle(ChatFormatting.GREEN),
                            (btn) -> {
                                this.editBoxes.add(new HeartColorEditBox(this.font, 0, 0, EDIT_BOX_WIDTH - 22, Button.DEFAULT_HEIGHT, this.heartType));
                                this.hasChanged = this.haveValuesChanged();
                                this.rebuildWidgets();
                            }
                    ).pos(x, 0).size(BUTTON_DIMS, BUTTON_DIMS).build();
                    widgets.add(addButton);
                }
                this.colorSelectionList.addEntry(new ScrollableWidgetList.Entry(widgets));
            }
        }

        if (this.editBoxes.isEmpty()) {
            int x = startX + EDIT_BOX_WIDTH + BUTTON_DIMS + ELEMENT_SPACING + BUTTON_SPACING;
            final Button addButton = Button.builder(
                    Component.literal("+").withStyle(ChatFormatting.GREEN),
                    (btn) -> {
                        this.editBoxes.add(new HeartColorEditBox(this.font, 0, 0, EDIT_BOX_WIDTH - 22, Button.DEFAULT_HEIGHT, this.heartType));
                        this.rebuildWidgets();
                    }
            ).pos(x, 0).size(BUTTON_DIMS, BUTTON_DIMS).build();
            widgets.add(addButton);
            this.colorSelectionList.addEntry(new ScrollableWidgetList.Entry(widgets));
        }
    }

    public boolean hasEnoughColors() {
        // at least 1 color for all health and normal absorbing variants
        // exactly 2 colors for absorbing effect variants
        int count = this.editBoxes.size();
        return count > 0 &&
                (this.heartType.isHealthType() && (this.heartType == HeartType.HEALTH || this.vanillaHeart || count == 2)) // health
                || (!this.heartType.isHealthType() && (this.heartType == HeartType.ABSORBING || count == 2)); // absorbing
    }

    public boolean canApplyChanges() {
        if (this.editBoxes.stream().anyMatch(HeartColorEditBox::isInvalid)) return false;
        return this.hasChanged && this.hasEnoughColors();
    }

    private boolean haveValuesChanged() {
        if (this.editBoxes.stream().anyMatch(HeartColorEditBox::isInvalid)) {
            return true;
        }

        if (this.vanillaHeart != this.heartType.isVanillaVariantPresent()) {
            return true;
        }

        final List<Integer> currentColors = this.heartType.getColors();
        final List<Integer> newColors = this.editBoxes.stream().map(HeartColorEditBox::getColor).toList();

        if (currentColors.size() != newColors.size()) {
            return true;
        }

        for (int i = 0; i < currentColors.size(); i++) {
            if (!Objects.equals(currentColors.get(i), newColors.get(i))) {
                return true;
            }
        }

        return false;
    }

    private SimpleConfigOption<Boolean> getConfigVanilla() {
        return switch (this.heartType) {
            case HEALTH -> Configuration.HEALTH.vanillaHearts;
            case ABSORBING -> Configuration.ABSORPTION.vanillaHearts;
            default -> null;
        };
    }

    private void saveConfig() {
        SimpleConfigOption<Boolean> configVanilla = this.getConfigVanilla();

        // save only valid color fields
        if (configVanilla != null && this.vanillaHeart != configVanilla.get()) {
            configVanilla.set(this.vanillaHeart);
            this.vanillaChanged = true;
        }

        ConfigOption<List<String>, List<Integer>> configColors = this.heartType.getConfigOption();

        List<Integer> currentColors = configColors.get();
        List<Integer> newColors = this.editBoxes.stream().map(HeartColorEditBox::getColor).toList();

        if (currentColors.size() != newColors.size()) {
            configColors.set(newColors);
            this.colorsChanged = true;
        } else for (int i = 0; i < currentColors.size(); i++) {
            if (!Objects.equals(currentColors.get(i), newColors.get(i))) {
                configColors.set(newColors);
                this.colorsChanged = true;
                break;
            }
        }

        CColorfulHearts.PROXY.applyConfig();
    }
}