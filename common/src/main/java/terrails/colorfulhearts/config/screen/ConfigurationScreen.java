package terrails.colorfulhearts.config.screen;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import terrails.colorfulhearts.config.screen.widgets.LabelLine;

public class ConfigurationScreen extends Screen {

    private final Screen lastScreen;

    public ConfigurationScreen(Screen lastScreen) {
        super(Component.translatable("colorfulhearts.screen.configuration.title"));
        this.lastScreen = lastScreen;
    }

    @Override
    protected void init() {
        assert this.minecraft != null;
        final Font font = this.minecraft.font;

        final int marginY = 32;
        final int marginX = 40;

        final int center = this.minecraft.getWindow().getGuiScaledWidth() / 2;
        final int leftButtonX = center - Button.DEFAULT_WIDTH - 3;
        final int rightButtonX = center + 3;

        //
        // Colors label & buttons
        int y = marginY + font.lineHeight * 2;
        this.addRenderableOnly(new LabelLine(font, marginX, y, this.width - marginX * 2,
                Component.translatable("colorfulhearts.screen.configuration.colors.category"))
        );

        y += font.lineHeight * 2;
        this.addRenderableWidget(
                Button.builder(Component.translatable("colorfulhearts.options.button.health_colors"),
                                (btn) -> this.minecraft.setScreen(new ColorSelectionScreen(this, true)))
                        .pos(leftButtonX, y).size(Button.DEFAULT_WIDTH, Button.DEFAULT_HEIGHT)
                        .build()
        );

        this.addRenderableWidget(
                Button.builder(Component.translatable("colorfulhearts.options.button.absorption_colors"),
                                (btn) -> this.minecraft.setScreen(new ColorSelectionScreen(this, false)))
                        .pos(rightButtonX, y).size(Button.DEFAULT_WIDTH, Button.DEFAULT_HEIGHT)
                        .build()
        );

        //
        // Bottom button
        this.addRenderableWidget(Button
                .builder(CommonComponents.GUI_DONE, button -> this.onClose())
                .bounds(this.width / 2 - 100, this.height - 27, 200, 20)
                .build());
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, 20, 16777215);
    }

    @Override
    public void onClose() {
        assert this.minecraft != null;
        this.minecraft.setScreen(this.lastScreen);
    }
}
