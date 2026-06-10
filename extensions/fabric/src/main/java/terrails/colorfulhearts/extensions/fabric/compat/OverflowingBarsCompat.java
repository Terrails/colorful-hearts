//package terrails.colorfulhearts.extensions.fabric.compat;
//
//import fuzs.overflowingbars.OverflowingBars;
//import fuzs.overflowingbars.client.gui.RowCountRenderer;
//import fuzs.overflowingbars.config.ClientConfig;
//import fuzs.puzzleslib.api.client.core.v1.ClientAbstractions;
//import fuzs.puzzleslib.api.client.event.v1.gui.RenderGuiLayerEvents;
//import fuzs.puzzleslib.api.event.v1.core.EventResult;
//
//import terrails.colorfulhearts.api.fabric.event.FabHeartEvents;
//import terrails.colorfulhearts.extensions.compat.OverflowingBarsCommonCompat;
//import net.minecraft.client.Minecraft;
//import net.minecraft.client.gui.GuiGraphics;
//import net.minecraft.util.Mth;
//import net.minecraft.world.entity.ai.attributes.Attributes;
//import net.minecraft.world.entity.player.Player;
//
//public class OverflowingBarsCompat extends OverflowingBarsCommonCompat {
//
//    public OverflowingBarsCompat() {
//        super();
//        FabHeartEvents.POST_RENDER.register(this::render);
//        RenderGuiLayerEvents.before(RenderGuiLayerEvents.PLAYER_HEALTH).register((gui, guiGraphics, deltaTracker) -> {
//            if (gui.minecraft.getCameraEntity() instanceof Player player) {
//                int health = Mth.ceil(player.getHealth());
//                float maxHealth = Math.max((float) player.getAttributeValue(Attributes.MAX_HEALTH), (float) Math.max(gui.displayHealth, health));
//                int absorption = Mth.ceil(player.getAbsorptionAmount());
//                int healthRows = Mth.ceil((maxHealth + (float) absorption) / 2.0F / 10.0F);
//                int healthRowShift = Math.max(10 - (healthRows - 2), 3);
//
//                // Have to remove this offset as PuzzlesLib adds it
//                int removeOffset = 10 + (healthRows - 1) * healthRowShift;
//
//                // handle half heart requiring absorption to move one row up
//                if (maxHealth == 19) maxHealth = 20;
//                boolean hasAbsorptionRow = (absorption + Math.min(20, maxHealth)) > 20;
//
//                // Actual offset we want
//                int actualOffset = hasAbsorptionRow ? 20 : 10;
//
//                // Remove offset created by PuzzlesLib and add our own
//                ClientAbstractions.INSTANCE.addGuiLeftHeight(Minecraft.getInstance().gui, actualOffset - removeOffset);
//            }
//            return EventResult.PASS;
//        });
//    }
//
//    @Override
//    protected void drawBarRowCount(GuiGraphics guiGraphics, int posX, int posY, int barValue, int maxRowCount) {
//        RowCountRenderer.drawBarRowCount(guiGraphics, posX, posY, barValue, true, maxRowCount, Minecraft.getInstance().font);
//    }
//
//    @Override
//    protected boolean allowCount() {
//        return OverflowingBars.CONFIG.get(ClientConfig.class).health.allowCount;
//    }
//}
