package terrails.colorfulhearts.config.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import terrails.colorfulhearts.LoaderExpectPlatform;
import terrails.colorfulhearts.config.ConfigOption;
import terrails.colorfulhearts.config.Configuration;
import terrails.colorfulhearts.config.screen.widgets.HeartColorEditBox;
import terrails.colorfulhearts.config.screen.base.ScrollableWidgetList;
import terrails.colorfulhearts.heart.HeartPiece;
import terrails.colorfulhearts.heart.HeartType;
import terrails.colorfulhearts.render.HeartRenderer;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class ColorSelectionScreen extends Screen {

    private final Screen lastScreen;
    private final boolean health;

    private boolean vanillaHeart, hasChanged;

    private List<HeartColorEditBox> editBoxes;

    private List<Button> heartTypeButtons;
    private Button saveButton;

    private ScrollableWidgetList colorSelectionList;
    private HeartType heartType;

    public ColorSelectionScreen(Screen lastScreen, boolean health) {
        super(Component.translatable(health ? "colorfulhearts.screen.health.title" : "colorfulhearts.screen.absorption.title"));
        this.lastScreen = lastScreen;
        this.health = health;
        this.updateHeartType(HeartType.NORMAL);
    }

    @Override
    protected void init() {
        super.init();

        // some basic info
        int width = Minecraft.getInstance().getWindow().getGuiScaledWidth();
        int height = Minecraft.getInstance().getWindow().getGuiScaledHeight();
        int marginY = 32;

        // get previous scroll amount or none if screen was just created
        double prevScrollAmount = this.colorSelectionList == null ? 0 : this.colorSelectionList.getScrollAmount();

        // create scrollable list widget and add it to this screen's children
        this.colorSelectionList = this.addRenderableWidget(new ScrollableWidgetList(Minecraft.getInstance(), width, height, marginY, height - marginY, Button.DEFAULT_HEIGHT + 6));

        // add elements to the scrollable widget
        this.addColorElements();

        // prevent view from being moved to top after widgets get rebuilt
        this.colorSelectionList.setScrollAmount(prevScrollAmount);

        // add buttons for switching between heart types
        int BUTTON_SPACING = 10;
        int BUTTON_WIDTH = 80;
        int startX = (width - BUTTON_WIDTH * 4 - BUTTON_SPACING * 3) / 2;
        int y = (marginY - Button.DEFAULT_HEIGHT) / 2;
        this.heartTypeButtons = new ArrayList<>();

        final Button normalHearts = Button.builder(
                Component.translatable("colorfulhearts.options.button.hearttype.normal"),
                (btn) -> this.updateHeartType(HeartType.NORMAL)).pos(startX, y).size(BUTTON_WIDTH, Button.DEFAULT_HEIGHT).build();
        normalHearts.active = !(this.heartType == HeartType.NORMAL);
        this.addRenderableWidget(normalHearts);
        this.heartTypeButtons.add(normalHearts);

        int x = startX + BUTTON_WIDTH + BUTTON_SPACING;
        final Button poisonedHearts = Button.builder(
                Component.translatable("colorfulhearts.options.button.hearttype.poisoned"),
                (btn) -> this.updateHeartType(HeartType.POISONED)).pos(x, y).size(BUTTON_WIDTH, Button.DEFAULT_HEIGHT).build();
        poisonedHearts.active = !(this.heartType == HeartType.POISONED);
        this.addRenderableWidget(poisonedHearts);
        this.heartTypeButtons.add(poisonedHearts);

        x += BUTTON_WIDTH + BUTTON_SPACING;
        final Button witheredHearts = Button.builder(
                Component.translatable("colorfulhearts.options.button.hearttype.withered"),
                (btn) -> this.updateHeartType(HeartType.WITHERED)).pos(x, y).size(BUTTON_WIDTH, Button.DEFAULT_HEIGHT).build();
        witheredHearts.active = !(this.heartType == HeartType.WITHERED);
        this.addRenderableWidget(witheredHearts);
        this.heartTypeButtons.add(witheredHearts);

        x += BUTTON_WIDTH + BUTTON_SPACING;
        final Button frozenHearts = Button.builder(
                Component.translatable("colorfulhearts.options.button.hearttype.frozen"),
                (btn) -> this.updateHeartType(HeartType.FROZEN)).pos(x, y).size(BUTTON_WIDTH, Button.DEFAULT_HEIGHT).build();
        frozenHearts.active = !(this.heartType == HeartType.FROZEN);
        this.addRenderableWidget(frozenHearts);
        this.heartTypeButtons.add(frozenHearts);

        // add save & cancel buttons
        BUTTON_WIDTH = Button.SMALL_WIDTH;
        BUTTON_SPACING = 30;
        startX = (width - BUTTON_WIDTH * 2 - BUTTON_SPACING) / 2;
        y = height - (Button.DEFAULT_HEIGHT / 2) - (marginY / 2);

        final Button saveButton = Button.builder(
                Component.translatable("colorfulhearts.options.button.confirmsave"),
                (btn) -> {
                    this.saveConfig();
                    // forces a heart update in renderer
                    HeartRenderer.INSTANCE.lastHeartType = null;
                    this.onClose();
                }).pos(startX, y).size(BUTTON_WIDTH, Button.DEFAULT_HEIGHT).build();
        saveButton.active = false;
        this.addRenderableWidget(saveButton);
        this.saveButton = saveButton;

        x = startX + BUTTON_WIDTH + BUTTON_SPACING;
        final Button cancelButton = Button.builder(
                Component.translatable("colorfulhearts.options.button.cancel"),
                (btn) -> this.onClose()).pos(x, y).size(BUTTON_WIDTH, Button.DEFAULT_HEIGHT).build();
        this.addRenderableWidget(cancelButton);

        if (this.hasChanged) {
            this.heartTypeButtons.forEach(btn -> btn.active = false);
        }

        if (this.canSave()) {
            this.saveButton.active = true;
        }
    }

    public void updateHeartType(HeartType type) {
        this.heartType = type;
        this.vanillaHeart = this.hasVanillaHeartVariant() && this.heartColorsContainVanilla();
        this.editBoxes = null;
        this.rebuildWidgets();
    }

    public boolean canSave() {
        boolean invalidColorsPresent = this.editBoxes.stream().anyMatch(box -> !box.isValidHex());
        // if there's not enough valid colors, do not allow saving
        // at least 1 color for:
        //  - health
        //  - NORMAL heart variants
        // exactly 2 colors for:
        //  - absorption effect variants (no vanilla textures)
        long colorCount = this.editBoxes.stream().filter(HeartColorEditBox::isValidHex).count();
        boolean hasEnoughColors = colorCount > 0 &&
                ((this.health && (this.heartType == HeartType.NORMAL || this.vanillaHeart || colorCount == 2)) // health
                        ||
                        (!this.health && (this.heartType == HeartType.NORMAL || colorCount == 2)));

        return this.hasChanged && !invalidColorsPresent && hasEnoughColors;
    }

    public boolean hasVanillaHeartVariant() {
        // all health hearts have a vanilla variant, absorption only has a NORMAL vanilla variant
        return this.health || (this.heartType == HeartType.NORMAL);
    }

    private boolean haveValuesChanged() {
        final List<HeartPiece> pieces = this.heartColorsWithoutVanilla();
        boolean isSizeEqual = pieces.size() == this.editBoxes.size();
        boolean hasVanillaOptionChanged = !this.hasVanillaHeartVariant() // has no vanilla option
                || (this.heartType == HeartType.NORMAL && this.vanillaHeart == this.configVanillaHeart().get()) // normal vanilla option
                || (this.heartType != HeartType.NORMAL && this.vanillaHeart == this.heartColorsContainVanilla()); // effect vanilla variants

        if (isSizeEqual && hasVanillaOptionChanged) {
            for (int i = 0; i < pieces.size(); i++) {
                HeartColorEditBox box = this.editBoxes.get(i);
                HeartPiece piece = pieces.get(i);
                if (!box.isValidHex() || !piece.getHexColor().equalsIgnoreCase(box.getValue())) {
                    return true;
                }
            }
        } else {
            return true;
        }
        return false;
    }

    private List<HeartPiece> heartColorsWithoutVanilla() {
        List<HeartPiece> pieces = new ArrayList<>(HeartPiece.getHeartPiecesForType(this.heartType, !this.health));
        if (health) {
            pieces.remove(HeartPiece.VANILLA_HEALTH);
        } else {
            pieces.remove(HeartPiece.VANILLA_ABSORPTION);
        }
        return pieces;
    }

    private boolean heartColorsContainVanilla() {
        List<HeartPiece> pieces = new ArrayList<>(HeartPiece.getHeartPiecesForType(this.heartType, !this.health));
        if (health) {
            return pieces.contains(HeartPiece.VANILLA_HEALTH);
        } else {
            return pieces.contains(HeartPiece.VANILLA_ABSORPTION);
        }
    }

    private ConfigOption<Boolean> configVanillaHeart() {
        if (health) {
            return Configuration.HEALTH.vanillaHearts;
        } else {
            return Configuration.ABSORPTION.vanillaHearts;
        }
    }

    private void saveConfig() {
        // save only valid color fields
        final List<HeartPiece> heartPieces = this.editBoxes.stream().filter(HeartColorEditBox::isValidHex).map(HeartColorEditBox::getHeartPiece).collect(Collectors.toList());
        if (this.hasVanillaHeartVariant() && this.vanillaHeart) {
            if (this.health) {
                heartPieces.add(0, HeartPiece.VANILLA_HEALTH);
            } else {
                heartPieces.add(0, HeartPiece.VANILLA_ABSORPTION);
            }
        }

        if (health) {
            switch (heartType) {
                case NORMAL -> {
                    Configuration.HEALTH.colors.set(HeartPiece.getColorList(heartPieces));
                    Configuration.HEALTH.vanillaHearts.set(this.vanillaHeart);
                }
                case POISONED -> Configuration.HEALTH.poisonedColors.set(HeartPiece.getColorList(heartPieces));
                case WITHERED -> Configuration.HEALTH.witheredColors.set(HeartPiece.getColorList(heartPieces));
                case FROZEN -> Configuration.HEALTH.frozenColors.set(HeartPiece.getColorList(heartPieces));
            }
        } else {
            switch (heartType) {
                case NORMAL -> {
                    Configuration.ABSORPTION.colors.set(HeartPiece.getColorList(heartPieces));
                    Configuration.ABSORPTION.vanillaHearts.set(this.vanillaHeart);
                }
                case POISONED -> Configuration.ABSORPTION.poisonedColors.set(HeartPiece.getColorList(heartPieces));
                case WITHERED -> Configuration.ABSORPTION.witheredColors.set(HeartPiece.getColorList(heartPieces));
                case FROZEN -> Configuration.ABSORPTION.frozenColors.set(HeartPiece.getColorList(heartPieces));
            }
        }
        LoaderExpectPlatform.applyConfig();
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
        if (this.hasVanillaHeartVariant()) {
            OFFSET = 1;

            final Supplier<Component> componentSupplier = () -> {
                if (this.vanillaHeart) {
                    return Component.translatable("colorfulhearts.options.button.vanillaheart.true");
                } else {
                    return Component.translatable("colorfulhearts.options.button.vanillaheart.false");
                }
            };
            final Button vanillaHeart = Button.builder(componentSupplier.get(),
                    (btn) -> {
                        this.vanillaHeart = !this.vanillaHeart;
                        this.hasChanged = this.haveValuesChanged();
                        btn.setMessage(componentSupplier.get());
                        this.rebuildWidgets();
                    }).pos(startX, 0).size(BUTTON_DIMS + BUTTON_SPACING + EDIT_BOX_WIDTH, Button.DEFAULT_HEIGHT).build();
            widgets.add(vanillaHeart);
        }

        if (this.editBoxes == null) {
            this.editBoxes = new ArrayList<>();
            final List<HeartPiece> pieces = this.heartColorsWithoutVanilla();
            for (HeartPiece piece : pieces) {
                final HeartColorEditBox box = new HeartColorEditBox(this.font, 0, 0, 0, 0, Component.empty(), this.heartType, this.health);
                box.setValue(piece.getHexColor());
                this.editBoxes.add(box);
            }
        }

        final int elementCount = this.editBoxes.size();
        for (int i = 0; i < elementCount; i++) {

            // if not NORMAL and if there's vanilla and 1 heart being rendered OR 2 hearts being rendered
            boolean stopRendering = this.health && this.heartType != HeartType.NORMAL && ((i > 0 && this.vanillaHeart) || i > 1);
            if (stopRendering) {
                this.colorSelectionList.addEntry(new ScrollableWidgetList.Entry(widgets));
                break;
            }
            final int index = i;

            int column = (i + OFFSET) % ELEMENTS_PER_ROW;
            int x = startX + column * (EDIT_BOX_WIDTH + BUTTON_DIMS + ELEMENT_SPACING + BUTTON_SPACING);

            final Button button = Button.builder(Component.literal("-").withStyle(ChatFormatting.RED),
                    (btn) -> {
                        this.editBoxes.remove(index);
                        this.hasChanged = this.haveValuesChanged();
                        this.rebuildWidgets();
                    }).pos(x, 0).size(BUTTON_DIMS, BUTTON_DIMS).build();

            HeartColorEditBox box = this.editBoxes.get(index);
            box = new HeartColorEditBox(this.font, x + BUTTON_DIMS + BUTTON_SPACING, 0, EDIT_BOX_WIDTH - 2, Button.DEFAULT_HEIGHT, box, Component.empty(), this.heartType, this.health);
            box.setResponder((str) -> this.hasChanged = this.haveValuesChanged());
            this.editBoxes.set(index, box);

            widgets.add(button);
            widgets.add(box);

            boolean isFinished = (index + 1) == elementCount;
            boolean isRowDone = (column + 1) == ELEMENTS_PER_ROW;

            if (isRowDone) {
                this.colorSelectionList.addEntry(new ScrollableWidgetList.Entry(widgets));
                widgets.clear();
            }

            if (isFinished) {
                // NORMAL = unlimited colors, EFFECT = 2 colors
                // do not need EFFECT = vanilla & 1 color as it is handled outside this loop, since no colors are present
                boolean canHaveMoreHearts = this.heartType == HeartType.NORMAL || (elementCount < 2 && !this.vanillaHeart);

                if (canHaveMoreHearts) {
                    column = (column + 1) % ELEMENTS_PER_ROW;
                    x = startX + (column * EDIT_BOX_WIDTH) + (column * BUTTON_DIMS) + (column * ELEMENT_SPACING) + (column * BUTTON_SPACING);
                    final Button addButton = Button.builder(Component.literal("+").withStyle(ChatFormatting.GREEN),
                            (btn) -> {
                                this.editBoxes.add(new HeartColorEditBox(this.font,
                                        0, 0,
                                        EDIT_BOX_WIDTH - 22, Button.DEFAULT_HEIGHT,
                                        Component.empty(),
                                        this.heartType,
                                        this.health));
                                this.hasChanged = this.haveValuesChanged();
                                this.rebuildWidgets();
                            }).pos(x, 0).size(BUTTON_DIMS, BUTTON_DIMS).build();
                    widgets.add(addButton);
                }
                this.colorSelectionList.addEntry(new ScrollableWidgetList.Entry(widgets));
            }
        }

        if (this.editBoxes.size() == 0) {
            int x = startX + EDIT_BOX_WIDTH + BUTTON_DIMS + ELEMENT_SPACING + BUTTON_SPACING;
            final Button addButton = Button.builder(Component.literal("+").withStyle(ChatFormatting.GREEN),
                    (btn) -> {
                        this.editBoxes.add(new HeartColorEditBox(this.font,
                                0, 0,
                                EDIT_BOX_WIDTH - 22, Button.DEFAULT_HEIGHT,
                                Component.empty(),
                                this.heartType,
                                this.health));
                        // not required as the widgets are rebuilt anyway
                        //this.hasChanged = this.haveValuesChanged();
                        this.rebuildWidgets();
                    }).pos(x, 0).size(BUTTON_DIMS, BUTTON_DIMS).build();
            widgets.add(addButton);
            this.colorSelectionList.addEntry(new ScrollableWidgetList.Entry(widgets));
        }
    }

    @Override
    public void render(@NotNull PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        if (this.hasChanged) {
            this.heartTypeButtons.forEach(btn -> btn.active = false);
            this.saveButton.active = this.canSave();
        } else if (this.saveButton.active) {
            this.rebuildWidgets();
        }
        super.render(poseStack, mouseX, mouseY, partialTick);
    }

    @Override
    public void onClose() {
        assert this.minecraft != null;
        this.minecraft.setScreen(this.lastScreen);
    }
}