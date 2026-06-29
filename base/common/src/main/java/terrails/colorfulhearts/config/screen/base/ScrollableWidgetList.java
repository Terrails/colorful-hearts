package terrails.colorfulhearts.config.screen.base;

import com.google.common.collect.ImmutableList;
import org.jspecify.annotations.NonNull;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
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
    public int addEntry(@NonNull Entry entry) {
        return super.addEntry(entry);
    }

    @Override
    protected int scrollBarX() {
        return this.width - scrollbarWidth();
    }

    @Override
    public int getRowWidth() {
        return this.width - scrollbarWidth();
    }

    public static class Entry extends ContainerObjectSelectionList.Entry<Entry> {

        final List<AbstractWidget> children;

        public Entry(List<AbstractWidget> widgets) {
            this.children = ImmutableList.copyOf(widgets);
        }

        public Entry(AbstractWidget... widgets) {
            this.children = ImmutableList.copyOf(Arrays.stream(widgets).toList());
        }

        @Override
        public void extractContent(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, boolean hovered, float a) {
            this.children.forEach(widget -> {
                widget.setY(getContentY());
                widget.extractRenderState(graphics, mouseX, mouseY, a);
            });
        }

        @Override
        public @NonNull List<? extends GuiEventListener> children() {
            return this.children;
        }

        @Override
        public @NonNull List<? extends NarratableEntry> narratables() {
            return this.children;
        }
    }
}
