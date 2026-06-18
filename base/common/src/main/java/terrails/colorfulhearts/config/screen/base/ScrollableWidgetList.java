package terrails.colorfulhearts.config.screen.base;

import com.google.common.collect.ImmutableList;
import com.mojang.logging.annotations.MethodsReturnNonnullByDefault;
import org.jetbrains.annotations.NotNull;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;

import java.util.Arrays;
import java.util.List;

public class ScrollableWidgetList extends ContainerObjectSelectionList<ScrollableWidgetList.Entry> {

    public ScrollableWidgetList(Minecraft minecraft, int width, int height, int y, int entryHeight) {
        super(minecraft, width, height, y, entryHeight);
    }

    @Override
    public int addEntry(@NotNull Entry entry) {
        return super.addEntry(entry);
    }

    @Override
    public int getRowTop(int index) {
        return super.getRowTop(index);
    }

    @Override
    protected int scrollBarX() {
        return this.width - 7;
    }

    @Override
    public int getRowWidth() {
        return this.width - 7;
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
        public void renderContent(GuiGraphics guiGraphics, int mouseX, int mouseY, boolean isHovering, float partialTick) {
            this.children.forEach(widget -> {
                widget.setY(getContentY());
                widget.render(guiGraphics, mouseX, mouseY, partialTick);
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
