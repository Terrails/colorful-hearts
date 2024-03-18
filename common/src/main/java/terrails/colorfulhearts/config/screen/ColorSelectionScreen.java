package terrails.colorfulhearts.config.screen;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import terrails.colorfulhearts.LoaderExpectPlatform;
import terrails.colorfulhearts.config.SimpleConfigOption;
import terrails.colorfulhearts.config.Configuration;
import terrails.colorfulhearts.config.screen.base.ScrollableWidgetList;
import terrails.colorfulhearts.config.screen.widgets.HeartColorEditBox;
import terrails.colorfulhearts.heart.CHeartType;
import terrails.colorfulhearts.render.HeartRenderer;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class ColorSelectionScreen extends Screen {

    private final Screen lastScreen;

    private List<HeartColorEditBox> editBoxes;
    private List<Button> heartTypeButtons;
    private Button saveButton;

    private boolean vanillaHeart;
    private boolean hasChanged;
    private boolean colorsChanged, vanillaChanged;

    private ScrollableWidgetList colorSelectionList;
    private CHeartType heartType;

    public ColorSelectionScreen(Screen lastScreen, boolean health) {
        super(Component.translatable(health ? "colorfulhearts.screen.health.title" : "colorfulhearts.screen.absorption.title"));
        this.lastScreen = lastScreen;
        this.heartType = health ? CHeartType.HEALTH : CHeartType.ABSORBING;
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
        double prevScrollAmount = this.colorSelectionList == null ? 0 : this.colorSelectionList.getScrollAmount();

        // create scrollable list widget and add it to this screen's children
        this.colorSelectionList = this.addRenderableWidget(new ScrollableWidgetList(Minecraft.getInstance(), width, height - marginY * 2, marginY, Button.DEFAULT_HEIGHT + 6));

        // set background type
        assert this.minecraft != null;
        this.colorSelectionList.setRenderBackground(this.minecraft.level == null);

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
                (btn) -> this.updateHeartType(this.heartType.isHealth() ? CHeartType.HEALTH : CHeartType.ABSORBING, true)
        ).pos(startX, y).size(BUTTON_WIDTH, BUTTON_HEIGHT).build());
        normalHearts.active = !(this.heartType == CHeartType.HEALTH || this.heartType == CHeartType.ABSORBING);

        int x = startX + BUTTON_WIDTH + BUTTON_SPACING;
        final Button poisonedHearts = this.addRenderableWidget(Button.builder(
                Component.translatable("colorfulhearts.options.button.hearttype.poisoned"),
                (btn) -> this.updateHeartType(this.heartType.isHealth() ? CHeartType.HEALTH_POISONED : CHeartType.ABSORBING_POISONED, true)
        ).pos(x, y).size(BUTTON_WIDTH, BUTTON_HEIGHT).build());
        poisonedHearts.active = !(this.heartType == CHeartType.HEALTH_POISONED || this.heartType == CHeartType.ABSORBING_POISONED);

        x += BUTTON_WIDTH + BUTTON_SPACING;
        final Button witheredHearts = this.addRenderableWidget(Button.builder(
                Component.translatable("colorfulhearts.options.button.hearttype.withered"),
                (btn) -> this.updateHeartType(this.heartType.isHealth() ? CHeartType.HEALTH_WITHERED : CHeartType.ABSORBING_WITHERED, true)
        ).pos(x, y).size(BUTTON_WIDTH, BUTTON_HEIGHT).build());
        witheredHearts.active = !(this.heartType == CHeartType.HEALTH_WITHERED || this.heartType == CHeartType.ABSORBING_WITHERED);

        x += BUTTON_WIDTH + BUTTON_SPACING;
        final Button frozenHearts = this.addRenderableWidget(Button.builder(
                Component.translatable("colorfulhearts.options.button.hearttype.frozen"),
                (btn) -> this.updateHeartType(this.heartType.isHealth() ? CHeartType.HEALTH_FROZEN : CHeartType.ABSORBING_FROZEN, true)
        ).pos(x, y).size(BUTTON_WIDTH, BUTTON_HEIGHT).build());
        frozenHearts.active = !(this.heartType == CHeartType.HEALTH_FROZEN || this.heartType == CHeartType.ABSORBING_FROZEN);

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
            LoaderExpectPlatform.heartChangeEvent();
        } else if (this.vanillaChanged) {
            LoaderExpectPlatform.heartChangeEvent();
        }
    }

    public void updateHeartType(CHeartType type, boolean rebuildWidgets) {
        this.heartType = type;
        this.editBoxes = null;

        Integer[] colors = type.getColors();
        assert colors != null;
        this.vanillaHeart = this.hasVanillaVariant() && colors[0] == null;

        if (rebuildWidgets) {
            this.rebuildWidgets();
        }
    }

    public boolean hasVanillaVariant() {
        // all health hearts have a vanilla variant, absorption only normal variant
        return this.heartType.isHealth() || (this.heartType == CHeartType.ABSORBING);
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
            final Integer[] colors = this.heartType.getColors();
            assert colors != null;

            this.editBoxes = new LinkedList<>();
            for (Integer color : colors) {
                if (color == null) continue;
                final HeartColorEditBox box = new HeartColorEditBox(this.font, 0, 0, 0, 0, this.heartType);
                box.setValue("#" + HexFormat.of().toHexDigits(color, 6));
                this.editBoxes.add(box);
            }
        }

        final int elementCount = this.editBoxes.size();
        for (int i = 0; i < elementCount; i++) {

            // if not normal variant and if there's vanilla and 1 color OR 2 colors
            boolean stop = this.heartType.isHealth() && this.heartType != CHeartType.HEALTH && ((i > 0 && this.vanillaHeart) || i > 1);
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
                boolean canAddMoreHearts = !this.heartType.isEffect() || !this.hasEnoughColors();

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
                (this.heartType.isHealth() && (this.heartType == CHeartType.HEALTH || this.vanillaHeart || count == 2)) // health
                || (!this.heartType.isHealth() && (this.heartType == CHeartType.ABSORBING || count == 2)); // absorbing
    }

    public boolean canApplyChanges() {
        if (this.editBoxes.stream().anyMatch(HeartColorEditBox::isInvalid)) return false;
        return this.hasChanged && this.hasEnoughColors();
    }

    private boolean haveValuesChanged() {
        if (this.editBoxes.stream().anyMatch(HeartColorEditBox::isInvalid)) return true;

        final Integer[] currentColors = this.heartType.getColors();
        assert currentColors != null;

        boolean vanillaOptionChanged = this.vanillaHeart != (currentColors[0] == null);
        if (vanillaOptionChanged) return true;

        final Integer[] newColors;
        if (this.vanillaHeart) {
            List<Integer> list = this.editBoxes.stream().map(HeartColorEditBox::getColor).collect(Collectors.toList());
            list.add(0, null);
            newColors = list.toArray(Integer[]::new);
        } else {
            newColors = this.editBoxes.stream().map(HeartColorEditBox::getColor).toArray(Integer[]::new);
        }

        if (currentColors.length != newColors.length) return true;

        for (int i = 0; i < currentColors.length; i++) {
            Integer currentColor = currentColors[i];
            Integer newColor = newColors[i];
            if (!Objects.equals(currentColor, newColor)) {
                return true;
            }
        }

        return false;
    }

    private SimpleConfigOption<List<String>> getConfigColors() {
        return switch (this.heartType) {
            case HEALTH -> Configuration.HEALTH.colors;
            case HEALTH_POISONED -> Configuration.HEALTH.poisonedColors;
            case HEALTH_WITHERED -> Configuration.HEALTH.witheredColors;
            case HEALTH_FROZEN -> Configuration.HEALTH.frozenColors;
            case ABSORBING -> Configuration.ABSORPTION.colors;
            case ABSORBING_POISONED -> Configuration.ABSORPTION.poisonedColors;
            case ABSORBING_WITHERED -> Configuration.ABSORPTION.witheredColors;
            case ABSORBING_FROZEN -> Configuration.ABSORPTION.frozenColors;
            default -> null;
        };
    }

    private SimpleConfigOption<Boolean> getConfigVanilla() {
        return switch (this.heartType) {
            case HEALTH -> Configuration.HEALTH.vanillaHearts;
            case ABSORBING -> Configuration.ABSORPTION.vanillaHearts;
            default -> null;
        };
    }

    private void saveConfig() {
        SimpleConfigOption<List<String>> configColors = this.getConfigColors();
        SimpleConfigOption<Boolean> configVanilla = this.getConfigVanilla();
        assert configColors != null && configVanilla != null;

        // save only valid color fields
        if (this.hasVanillaVariant() && this.vanillaHeart != configVanilla.get()) {
            configVanilla.set(this.vanillaHeart);
            this.vanillaChanged = true;
        }

        List<String> previousValues = configColors.get().stream().map(String::toUpperCase).toList();
        final List<String> colors = this.editBoxes.stream().map(HeartColorEditBox::getColor).map(i -> "#" + HexFormat.of().toHexDigits(i, 6).toUpperCase()).toList();
        if (!previousValues.equals(colors)) {
            configColors.set(colors);
            this.colorsChanged = true;
        }
        LoaderExpectPlatform.applyConfig();
    }
}