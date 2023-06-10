package terrails.colorfulhearts.config.screen.widgets;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.network.chat.Component;

public class LabelLine implements Renderable {

    private final Font font;
    private final int x, y, width;

    private final Component labelText;

    public LabelLine(Font font, int x, int y, int width, Component labelText) {
        this.font = font;
        this.x = x;
        this.y = y;
        this.width = width;
        this.labelText = labelText;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        int textSpacing = (int) (this.font.width(this.labelText) * 0.7);
        int lineWidth = (this.width - textSpacing * 2) / 2;
        int centerX = this.x + lineWidth + textSpacing;

        guiGraphics.drawCenteredString(this.font, this.labelText, centerX, this.y, 0xFFFFFF);

        int lineY = this.y + font.lineHeight / 2;
        guiGraphics.fill(this.x, lineY, this.x + lineWidth, lineY + 1, 0x7FFFFFFF);
        guiGraphics.fill(centerX + textSpacing, lineY, centerX + textSpacing + lineWidth, lineY + 1, 0x7FFFFFFF);
    }
}
