package terrails.colorfulhearts;

import dev.architectury.injectables.annotations.ExpectPlatform;

public class LoaderExpectPlatform {

    /**
     * Applies changes to night-config's FileConfig
     * Technically not needed if autosave were to be enabled on both loaders
     */
    @ExpectPlatform
    public static void applyConfig() { throw new AssertionError(); }

    /**
     * A way for other mods to force usage of hardcore hearts
     * Currently only possible with Fabric via ObjectShare
     * @return if hardcore textures should be used even if a world is not hardcore
     */
    @ExpectPlatform
    public static boolean forcedHardcoreHearts() { return false; }

    @ExpectPlatform
    public static boolean inDevEnvironment() { return false; }

}
