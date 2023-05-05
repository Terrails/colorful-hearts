package terrails.colorfulhearts.config.screen.base;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public class ScrollableWidgetList extends ContainerObjectSelectionList<ScrollableWidgetList.Entry> {

    public ScrollableWidgetList(Minecraft minecraft, int width, int height, int y0, int y1, int entryHeight) {
        super(minecraft, width, height, y0, y1, entryHeight);
    }

    public void removeEntries() {
        this.clearEntries();
    }

    @Nullable
    @Override
    public Entry remove(int index) {
        return super.remove(index);
    }

    @Override
    public int addEntry(@NotNull Entry entry) {
        return super.addEntry(entry);
    }

    @Override
    protected int getRowTop(int index) {
        return super.getRowTop(index);
    }

    @Override
    protected int getScrollbarPosition() {
        return this.width - 7;
    }

    @Override
    public int getRowWidth() {
        return this.width - 14;
    }

    @MethodsReturnNonnullByDefault
    public static class Entry extends ContainerObjectSelectionList.Entry<Entry> {

        final List<AbstractWidget> children;

        public Entry(List<AbstractWidget> widgets) {
            this.children = ImmutableList.copyOf(widgets);
        }

        public Entry(AbstractWidget... widgets) {
            this.children = ImmutableList.copyOf(Arrays.stream(widgets).toList());
        }

        @Override
        public void render(@NotNull PoseStack poseStack, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean isMouseOver, float partialTick) {
            this.children.forEach(widget -> {
                widget.setY(top);
                widget.render(poseStack, mouseX, mouseY, partialTick);
            });
        }

        @Override
        public List<? extends GuiEventListener> children() {
            return this.children;
        }

        @Override
        public List<? extends NarratableEntry> narratables() {
            return this.children;
        }
    }
}
