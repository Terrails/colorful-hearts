package terrails.colorfulhearts.fabric;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ObjectShare;
import terrails.colorfulhearts.fabric.api.event.FabHeartChangeEvent;
import terrails.colorfulhearts.render.HeartRenderer;
import terrails.colorfulhearts.render.TabHeartRenderer;

public class ColorfulHearts implements ClientModInitializer {

    public static FabConfig CONFIG;

    @Override
    public void onInitializeClient() {
        CONFIG = new FabConfig();
        this.setupObjectShare();
        FabHeartChangeEvent.EVENT.register(() -> {
            // force an update to heart colors when config GUI is updated
            HeartRenderer.INSTANCE.lastHealthType = null;
            TabHeartRenderer.INSTANCE.lastHealth = 0;
        });
    }

    private void setupObjectShare() {
        final ObjectShare objectShare = FabricLoader.getInstance().getObjectShare();

        // Absorption
        // keep this for now in case some mods depended on it
        objectShare.putIfAbsent("colorfulhearts:absorption_over_health", false);

        // Allows other mods to force use of hardcore heart textures
        // Default vanilla behaviour (hardcore world) if false
        objectShare.putIfAbsent("colorfulhearts:force_hardcore_hearts", false);
    }

}
