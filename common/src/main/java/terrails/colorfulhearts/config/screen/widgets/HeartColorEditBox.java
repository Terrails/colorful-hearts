package terrails.colorfulhearts.config.screen.widgets;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import terrails.colorfulhearts.heart.CHeartType;

import java.util.function.Consumer;
import java.util.regex.Pattern;

public class HeartColorEditBox extends EditBox {

    private static final Pattern HEX_FORMAT = Pattern.compile("^#[0-9a-fA-F]{0,6}$"), HEX_MATCH = Pattern.compile("^#[0-9a-fA-F]{6}$");

    private boolean invalidRGBHex;

    private final CHeartType heartType;
    private ResourceLocation spriteLocation;

    private final Consumer<String> defaultResponder;

    public HeartColorEditBox(Font font, int x, int y, int width, int height, CHeartType heartType) {
        this(font, x, y, width, height, null, heartType);
    }

    public HeartColorEditBox(Font font, int x, int y, int width, int height, @Nullable EditBox editBox, CHeartType heartType) {
        super(font, x, y, width, height, editBox, Component.empty());
        this.setResponder((str) -> {});
        this.setFilter((str) -> HEX_FORMAT.matcher(str).matches());
        this.setMaxLength(7);
        this.heartType = heartType;
        this.defaultResponder = (str) -> {
            this.invalidRGBHex = !HEX_MATCH.matcher(str).matches();
            if (!this.invalidRGBHex) {
                ResourceLocation spriteLocation = this.heartType.getSprite(false, false, false, this.getColor(), false);
                TextureAtlasSprite sprite = Minecraft.getInstance().getGuiSprites().getSprite(spriteLocation);
                if (!sprite.contents().name().equals(MissingTextureAtlasSprite.getLocation())) {
                    this.spriteLocation = spriteLocation;
                }
            }
        };
        this.defaultResponder.accept(this.getValue());
    }

    @Override
    public void setResponder(Consumer<String> responder) {
        super.setResponder((str) -> {
            this.defaultResponder.accept(str);
            responder.accept(str);
        });
    }

    public boolean isInvalid() {
        return this.invalidRGBHex;
    }

    public int getColor() {
        return Integer.decode(this.getValue()) & 0xFFFFFF;
    }

    @Override
    public void renderWidget(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);

        if (!this.isVisible()) {
            return;
        }

        if (this.isInvalid()) {
            if (this.isBordered()) {
                // draw over the border in red if the text is invalid
                int borderColor = this.isFocused() ? 0xFFD6231A : 0xFF590707;
                guiGraphics.fill(this.getX(), this.getY(), this.getX() + this.width, this.getY() + 1, borderColor);
                guiGraphics.fill(this.getX(), this.getY() + this.height - 1, this.getX() + this.width,  this.getY() + this.height, borderColor);
                guiGraphics.fill(this.getX(), this.getY() + 1, this.getX() + 1, this.getY() + this.height - 1, borderColor);
                guiGraphics.fill(this.getX() + this.width - 1, this.getY() + 1, this.getX() + this.width, this.getY() + this.height - 1, borderColor);
            }
        } else {
            // draw a colored rectangle in the remaining empty space inside the box
            int x = this.getX() + this.width - 11;
            int y = this.getY() + this.height / 2 - 5;
            if (this.spriteLocation != null) {
                guiGraphics.blitSprite(this.spriteLocation, x, y, 9, 9);
            } else {
                guiGraphics.fill(x, y, x + 9, y + 9, this.getColor() | 0xFF000000);
                guiGraphics.renderOutline(x, y, 9, 9, 0xFFDDDDDD);
            }
        }
    }
}