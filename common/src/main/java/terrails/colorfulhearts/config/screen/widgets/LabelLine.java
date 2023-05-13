package terrails.colorfulhearts.config.screen.widgets;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.network.chat.Component;

public class LabelLine implements Widget {

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
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        int textSpacing = (int) (this.font.width(this.labelText) * 0.7);
        int lineWidth = (this.width - textSpacing * 2) / 2;
        int centerX = this.x + lineWidth + textSpacing;

        GuiComponent.drawCenteredString(poseStack, this.font, this.labelText, centerX, this.y, 0xFFFFFF);

        int lineY = this.y + font.lineHeight / 2;
        GuiComponent.fill(poseStack, this.x, lineY, this.x + lineWidth, lineY + 1, 0x7FFFFFFF);
        GuiComponent.fill(poseStack, centerX + textSpacing, lineY, centerX + textSpacing + lineWidth, lineY + 1, 0x7FFFFFFF);
    }
}
