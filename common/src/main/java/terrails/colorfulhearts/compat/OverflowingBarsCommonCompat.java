package terrails.colorfulhearts.compat;

import terrails.colorfulhearts.api.event.HeartRenderEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.Supplier;

import static terrails.colorfulhearts.CColorfulHearts.LOGGER;

public class OverflowingBarsCommonCompat {

    protected Supplier<Boolean> allowCount;
    protected Method drawBarRowCount;
    private boolean firstRun = true;

    public OverflowingBarsCommonCompat() {
        try {
            Class<?> clazz = Class.forName("fuzs.overflowingbars.client.gui.RowCountRenderer");
            drawBarRowCount = clazz.getMethod("drawBarRowCount", GuiGraphics.class, int.class, int.class, int.class, boolean.class, int.class, Font.class);
        } catch (ClassNotFoundException e) {
            LOGGER.error("Could not initialize OverflowingBarsCompat as class RowCountRenderer has been moved. PLEASE REPORT TO COLORFUL HEARTS GITHUB!!!", e);
        } catch (NoSuchMethodException e) {
            LOGGER.error("Could not initialize OverflowingBarsCompat as field RowCountRenderer#drawBarRowCount has been moved. PLEASE REPORT TO COLORFUL HEARTS GITHUB!!!", e);
        }
    }

    public void render(HeartRenderEvent.Post event) {
        if (firstRun) {
            initializeConfigReflection();
            firstRun = false;
        }
        if (allowCount == null) return;
        Player player = Minecraft.getInstance().player;

        // After 10.000 iteration, it takes about 0.04-0.05 ms on average to run this reflection based approach.
        // That is double compared to direct calls that take about 0.02-0.03 ms to run this same code but with reflection calls replaced.
        // That should be fine in order to avoid having to add 2 extra libraries to the compile environment as long as we log anything that could break and point the blame to ourselves
        if (player != null && allowCount.get()) {
            int health = Mth.ceil(player.getHealth());
            drawBarRowCount(event.getGuiGraphics(), event.getX() - 2, event.getY(), health, 20);
            int maxAbsorption = (20 - Mth.ceil(Math.min(20, health) / 2.0F)) * 2;
            drawBarRowCount(event.getGuiGraphics(), event.getX() - 2, event.getY() - 10, Mth.ceil(player.getAbsorptionAmount()), maxAbsorption);
        }
    }

    private void drawBarRowCount(GuiGraphics guiGraphics, int posX, int posY, int barValue, int maxRowCount) {
        try {
            drawBarRowCount.invoke(null, guiGraphics, posX, posY, barValue, true, maxRowCount, Minecraft.getInstance().font);
        } catch (IllegalAccessException e) {
            LOGGER.error("Failed to access method RowCountRenderer#drawBarRowCount. Heart row counter will not behave as expected. PLEASE REPORT TO COLORFUL HEARTS GITHUB!!!", e);
        } catch (InvocationTargetException e) {
            LOGGER.error("Failed to invoke method RowCountRenderer#drawBarRowCount. Heart row counter will not behave as expected. PLEASE REPORT TO COLORFUL HEARTS GITHUB!!!", e);
        }
    }

    public void initializeConfigReflection() {
        Object configHolderInstance;
        try {
            Class<?> clazz = Class.forName("fuzs.overflowingbars.OverflowingBars");
            Field field = clazz.getField("CONFIG");
            configHolderInstance = field.get(null); // static, we can pass null
        } catch (ClassNotFoundException e) {
            LOGGER.error("Could not initialize OverflowingBarsCompat as class BarOverlayRenderer has been moved. PLEASE REPORT TO COLORFUL HEARTS GITHUB!!!", e);
            return;
        } catch (NoSuchFieldException e) {
            LOGGER.error("Could not initialize OverflowingBarsCompat as field BarOverlayRenderer.CONFIG has been moved. PLEASE REPORT TO COLORFUL HEARTS GITHUB!!!", e);
            return;
        } catch (IllegalAccessException e) {
            LOGGER.error("Could not initialize OverflowingBarsCompat as field BarOverlayRenderer.CONFIG could not be accessed. PLEASE REPORT TO COLORFUL HEARTS GITHUB!!!", e);
            return;
        }

        Class<?> clientConfigClass;
        try {
            clientConfigClass = Class.forName("fuzs.overflowingbars.config.ClientConfig");
        } catch (ClassNotFoundException e) {
            LOGGER.error("Could not initialize OverflowingBarsCompat as class ClientConfig has been moved. PLEASE REPORT TO COLORFUL HEARTS GITHUB!!!", e);
            return;
        }

        Object configInstance;
        try {
            Class<?> clazz = Class.forName("fuzs.puzzleslib.api.config.v3.ConfigHolder");
            Method method = clazz.getMethod("get", Class.class);
            configInstance = method.invoke(configHolderInstance, clientConfigClass);
        } catch (ClassNotFoundException e) {
            LOGGER.error("Could not initialize OverflowingBarsCompat as class BarOverlayRenderer has been moved. PLEASE REPORT TO COLORFUL HEARTS GITHUB!!!", e);
            return;
        } catch (NoSuchMethodException e) {
            LOGGER.error("Could not initialize OverflowingBarsCompat as method BarOverlayRenderer#get has been moved. PLEASE REPORT TO COLORFUL HEARTS GITHUB!!!", e);
            return;
        } catch (IllegalAccessException e) {
            LOGGER.error("Could not initialize OverflowingBarsCompat as method BarOverlayRenderer#get could not be accessed. PLEASE REPORT TO COLORFUL HEARTS GITHUB!!!", e);
            return;
        } catch (InvocationTargetException e) {
            LOGGER.error("Could not initialize OverflowingBarsCompat as method BarOverlayRenderer#get could not be invoked. PLEASE REPORT TO COLORFUL HEARTS GITHUB!!!", e);
            return;
        }

        Object healthConfigInstance;
        try {
            Field field = clientConfigClass.getField("health");
            healthConfigInstance = field.get(configInstance);
        } catch (NoSuchFieldException e) {
            LOGGER.error("Could not initialize OverflowingBarsCompat as field ClientConfig.health has been moved. PLEASE REPORT TO COLORFUL HEARTS GITHUB!!!", e);
            return;
        } catch (IllegalAccessException e) {
            LOGGER.error("Could not initialize OverflowingBarsCompat as field ClientConfig.health could not be accessed. PLEASE REPORT TO COLORFUL HEARTS GITHUB!!!", e);
            return;
        }

        Class<?> iconRowConfigClass;
        try {
            iconRowConfigClass = Class.forName("fuzs.overflowingbars.config.ClientConfig$IconRowConfig");
        } catch (ClassNotFoundException e) {
            LOGGER.error("Could not initialize OverflowingBarsCompat as class ClientConfig$IconRowConfig has been moved. PLEASE REPORT TO COLORFUL HEARTS GITHUB!!!", e);
            return;
        }

        try {
            Field field = iconRowConfigClass.getField("allowLayers");
            if (field.getBoolean(healthConfigInstance)) {
                LOGGER.warn("Overflowing Bars allowLayers config option is enabled and could possibly conflict with Colorful Hearts by forcing its own heart rendering");
            }
        } catch (NoSuchFieldException e) {
            LOGGER.error("Could not initialize OverflowingBarsCompat as field ClientConfig$IconRowConfig.allowLayers has been moved. PLEASE REPORT TO COLORFUL HEARTS GITHUB!!!", e);
            return;
        } catch (IllegalAccessException | IllegalArgumentException e) {
            LOGGER.error("Failed to fetch ClientConfig$IconRowConfig.allowLayers config value. Heart row counter will be disabled. PLEASE REPORT TO COLORFUL HEARTS GITHUB!!!", e);
            return;
        }

        try {
            Field field = iconRowConfigClass.getField("allowCount");
            allowCount = () -> {
                try {
                    return field.getBoolean(healthConfigInstance);
                } catch (IllegalAccessException | IllegalArgumentException e) {
                    LOGGER.error("Failed to fetch ClientConfig$IconRowConfig.allowCount config value. Heart row counter will be disabled. PLEASE REPORT TO COLORFUL HEARTS GITHUB!!!", e);
                    return false;
                }
            };
        } catch (NoSuchFieldException e) {
            LOGGER.error("Could not initialize OverflowingBarsCompat as field ClientConfig$IconRowConfig.allowCount has been moved. PLEASE REPORT TO COLORFUL HEARTS GITHUB!!!", e);
        }
    }
}
