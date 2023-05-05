package terrails.colorfulhearts.config.screen.widgets;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import terrails.colorfulhearts.heart.HeartPiece;
import terrails.colorfulhearts.heart.HeartType;

import java.util.function.Consumer;
import java.util.regex.Pattern;

public class HeartColorEditBox extends EditBox {

    private static final Pattern HEX_FORMAT = Pattern.compile("^#[0-9a-fA-F]{0,6}$"), HEX_MATCH = Pattern.compile("^#[0-9a-fA-F]{6}$");

    private boolean validHex;

    private final boolean health;
    private final HeartType type;

    public HeartColorEditBox(Font font, int x, int y, int width, int height, Component component, HeartType type, boolean health) {
        this(font, x, y, width, height, null, component, type, health);
    }

    public HeartColorEditBox(Font font, int x, int y, int width, int height, @Nullable EditBox editBox, Component component, HeartType type, boolean health) {
        super(font, x, y, width, height, editBox, component);
        this.type = type;
        this.health = health;
        this.setResponder((str) -> {});
        this.setFilter((str) -> HEX_FORMAT.matcher(str).matches());
        this.setMaxLength(7);
        if (editBox != null) {
            this.setValue(editBox.getValue());
        }
    }

    public boolean isValidHex() {
        return this.validHex;
    }

    public HeartPiece getHeartPiece() {
        return HeartPiece.custom(this.getValue(), !this.health);
    }

    @Override
    public void setResponder(@NotNull Consumer<String> responder) {
        super.setResponder((str) -> {
            validHex = HEX_MATCH.matcher(str).matches();
            responder.accept(str);
        });
    }

    @Override
    public void renderWidget(@NotNull PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        super.renderWidget(poseStack, mouseX, mouseY, partialTick);

        if (!this.isVisible()) {
            return;
        }

        if (!this.isValidHex()) {
            boolean isBordered = this.getInnerWidth() < this.width; // blame source for making #isBordered private
            if (isBordered) {
                // draw over the border in red if the hex text is invalid
                int borderColor = this.isFocused() ? 0xFFD6231A : 0xFF590707;
                GuiComponent.fill(poseStack, this.getX() - 1, this.getY() - 1, this.getX() + this.width + 1, this.getY(), borderColor);
                GuiComponent.fill(poseStack, this.getX() - 1, this.getY() + this.height, this.getX() + this.width + 1,  this.getY() + this.height + 1, borderColor);
                GuiComponent.fill(poseStack, this.getX() - 1, this.getY(), this.getX(), this.getY() + this.height, borderColor);
                GuiComponent.fill(poseStack, this.getX() + this.width, this.getY(), this.getX() + this.width + 1, this.getY() + this.height, borderColor);
            }
        } else {
            // draw heart in the remaining empty space inside the box
            HeartPiece heart = this.getHeartPiece();
            heart.draw(poseStack, this.getX() + this.width - 11, this.getY() + this.height / 2 - 4, false, false, this.type);
        }
    }
}