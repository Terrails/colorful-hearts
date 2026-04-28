//package terrails.colorfulhearts.compat;
//
//import net.minecraft.client.gui.GuiGraphicsExtractor;
//import terrails.colorfulhearts.api.event.HeartRenderEvent;
//import net.minecraft.client.Minecraft;
//import net.minecraft.util.Mth;
//import net.minecraft.world.entity.player.Player;
//
//public abstract class OverflowingBarsCommonCompat {
//
//    public void render(HeartRenderEvent.Post event) {
//        Player player = Minecraft.getInstance().player;
//
//        // After 10.000 iteration, it takes about 0.04-0.05 ms on average to run this reflection based approach.
//        // That is double compared to direct calls that take about 0.02-0.03 ms to run this same code but with reflection calls replaced.
//        // That should be fine in order to avoid having to add 2 extra libraries to the compile environment as long as we log anything that could break and point the blame to ourselves
//        if (player != null && this.allowCount()) {
//            int health = Mth.ceil(player.getHealth());
//            drawBarRowCount(event.getGuiGraphics(), event.getX() - 2, event.getY(), health, 20);
//            int maxAbsorption = (20 - Mth.ceil(Math.min(20, health) / 2.0F)) * 2;
//            drawBarRowCount(event.getGuiGraphics(), event.getX() - 2, event.getY() - 10, Mth.ceil(player.getAbsorptionAmount()), maxAbsorption);
//        }
//    }
//
//    protected abstract void drawBarRowCount(GuiGraphicsExtractor guiGraphics, int posX, int posY, int barValue, int maxRowCount);
//
//    protected abstract boolean allowCount();
//}
