package terrails.colorfulhearts.render.atlas.sources;

import com.google.common.base.Suppliers;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.atlas.SpriteResourceLoader;
import net.minecraft.client.renderer.texture.atlas.SpriteSource;
import net.minecraft.client.renderer.texture.atlas.SpriteSourceType;
import net.minecraft.client.renderer.texture.atlas.sources.LazyLoadedImage;
import net.minecraft.client.resources.metadata.animation.FrameSize;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceMetadata;
import org.jetbrains.annotations.NotNull;
import terrails.colorfulhearts.CColorfulHearts;
import terrails.colorfulhearts.Utils;
import terrails.colorfulhearts.heart.CHeartType;
import terrails.colorfulhearts.render.ImageUtils;

import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.IntUnaryOperator;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class ColoredHearts implements SpriteSource {

    public static SpriteSourceType COLORED_HEARTS;

    private static final Codec<CHeartType> HEART_TYPE = Codec.STRING.comapFlatMap(
            s -> {
                try {
                    CHeartType type = CHeartType.valueOf(s);
                    if (type == CHeartType.CONTAINER) {
                        return DataResult.error(() -> "Container is not a heart type");
                    } else {
                        return DataResult.success(type);
                    }
                } catch (IllegalArgumentException e) {
                    return DataResult.error(() -> "Unknown heart type " + s + " " + e.getMessage());
                }
            },
            Enum::name
    );

    public static final Codec<ColoredHearts> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    ColoredHearts.HEART_TYPE.fieldOf("heart").forGetter(codec -> codec.heartType)
            ).apply(instance, ColoredHearts::new)
    );

    private final CHeartType heartType;

    public ColoredHearts(CHeartType heartType) {
        this.heartType = heartType;
    }

    @Override
    public void run(ResourceManager resourceManager, Output output) {
        Integer[] configColors = this.heartType.getColors();
        assert configColors != null;

        final Map<Integer, IntUnaryOperator> map = Arrays.stream(configColors)
                .filter(Objects::nonNull)
                .map(rgb -> {
                    int r = (rgb >> 16) & 0xFF;
                    int g = (rgb >> 8) & 0xFF;
                    int b = rgb & 0xFF;
                    return Map.entry(rgb, ImageUtils.getColorOverlayOperator(r, g, b));
                })
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        this.processType(resourceManager, output, this.heartType, map, false, false, false);
        this.processType(resourceManager, output, this.heartType, map, false, false, true);
        this.processType(resourceManager, output, this.heartType, map, false, true, false);
        this.processType(resourceManager, output, this.heartType, map, false, true, true);

        this.processType(resourceManager, output, this.heartType, map, true, false, false);
        this.processType(resourceManager, output, this.heartType, map, true, false, true);
        this.processType(resourceManager, output, this.heartType, map, true, true, false);
        this.processType(resourceManager, output, this.heartType, map, true, true, true);
    }

    private void processType(
            ResourceManager resourceManager, Output output,
            CHeartType heartType, Map<Integer, IntUnaryOperator> hexARGBs,
            boolean hardcore, boolean blinking, boolean half
    ) {
        ResourceLocation baseLocation = Utils.location(heartType.getSprite(hardcore, blinking, half, null, false).getPath());
        Supplier<Consumer<NativeImage>> blendApplier = Suppliers.memoize(() -> this.getBlendApplier(resourceManager, baseLocation, hexARGBs.size()));

        ResourceLocation textureLocation = TEXTURE_ID_CONVERTER.idToFile(baseLocation);
        Optional<Resource> optional = resourceManager.getResource(textureLocation);
        if (optional.isEmpty()) {
            CColorfulHearts.LOGGER.warn("Missing texture: {}", textureLocation);
            return;
        }

        LazyLoadedImage lazyImage = new LazyLoadedImage(textureLocation, optional.get(), hexARGBs.size());
        for (Map.Entry<Integer, IntUnaryOperator> entry : hexARGBs.entrySet()) {
            ResourceLocation spriteLocation = heartType.getSprite(hardcore, blinking, half, entry.getKey(), false);
            output.add(spriteLocation, new ColoredHeartsSupplier(lazyImage, entry.getValue(), blendApplier, spriteLocation));
        }
    }

    /**
     * Finds all available blend type files and prepares a consumer that applies them to a given image
     * Although the default resource pack uses only screen blend type, I have added the possibility to do
     * normal, multiply, overlay and the already mentioned screen blend type in case that resource pack creators need more customization.
     * <p>
     * From what I know the best way to override the default screen blend files is to just make them empty/fully transparent files in the resource pack
     * All files must have their blend name appended to the usual name for the given base heart icon, so overlay for full.png would be full_overlay.png
     */
    private Consumer<NativeImage> getBlendApplier(ResourceManager resourceManager, ResourceLocation spriteLocation, int loadCount) {
        final ResourceLocation normalLocation = TEXTURE_ID_CONVERTER.idToFile(spriteLocation.withSuffix("_normal"));
        final ResourceLocation multiplyLocation = TEXTURE_ID_CONVERTER.idToFile(spriteLocation.withSuffix("_multiply"));
        final ResourceLocation screenLocation = TEXTURE_ID_CONVERTER.idToFile(spriteLocation.withSuffix("_screen"));
        final ResourceLocation overlayLocation = TEXTURE_ID_CONVERTER.idToFile(spriteLocation.withSuffix("_overlay"));

        final Optional<LazyLoadedImage> normalOptional = resourceManager.getResource(normalLocation).map(r -> new LazyLoadedImage(normalLocation, r, loadCount));
        final Optional<LazyLoadedImage> multiplyOptional = resourceManager.getResource(multiplyLocation).map(r -> new LazyLoadedImage(multiplyLocation, r, loadCount));
        final Optional<LazyLoadedImage> screenOptional = resourceManager.getResource(screenLocation).map(r -> new LazyLoadedImage(screenLocation, r, loadCount));
        final Optional<LazyLoadedImage> overlayOptional = resourceManager.getResource(overlayLocation).map(r -> new LazyLoadedImage(overlayLocation, r, loadCount));

        return (image) -> {
            normalOptional.ifPresentOrElse(normal -> {
                try {
                    ImageUtils.blendNormal(image, normal.get());
                } catch (IOException e) {
                    // print as error since the file exists but could not be loaded
                    CColorfulHearts.LOGGER.error(e);
                } finally {
                    normal.release();
                }
            }, () -> CColorfulHearts.LOGGER.debug("Texture not found: {}", normalLocation));
            multiplyOptional.ifPresentOrElse(multiply -> {
                try {
                    ImageUtils.blendMultiply(image, multiply.get());
                } catch (IOException e) {
                    // print as error since the file exists but could not be loaded
                    CColorfulHearts.LOGGER.error(e);
                } finally {
                    multiply.release();
                }
            }, () -> CColorfulHearts.LOGGER.debug("Texture not found: {}", multiplyLocation));
            screenOptional.ifPresentOrElse(screen -> {
                try {
                    NativeImage screenImg = screen.get();
                    ImageUtils.blendScreen(image, screenImg);
                } catch (IOException e) {
                    // print as error since the file exists but could not be loaded
                    CColorfulHearts.LOGGER.error(e);
                } finally {
                    screen.release();
                }
            }, () -> CColorfulHearts.LOGGER.debug("Texture not found: {}", screenLocation));
            overlayOptional.ifPresentOrElse(overlay -> {
                try {
                    ImageUtils.blendOverlay(image, overlay.get());
                } catch (IOException e) {
                    // print as error since the file exists but could not be loaded
                    CColorfulHearts.LOGGER.error(e);
                } finally {
                    overlay.release();
                }
            }, () -> CColorfulHearts.LOGGER.debug("Texture not found: {}", overlayLocation));
        };
    }

    @Override
    public @NotNull SpriteSourceType type() {
        return ColoredHearts.COLORED_HEARTS;
    }

    private record ColoredHeartsSupplier(
            LazyLoadedImage image, IntUnaryOperator colorOperator, Supplier<Consumer<NativeImage>> blend, ResourceLocation spriteLocation
    ) implements SpriteSupplier {

        @Override
        public SpriteContents apply(SpriteResourceLoader spriteResourceLoader) {
            try {
                NativeImage image = this.image.get().mappedCopy(this.colorOperator);
                this.blend.get().accept(image);
                return new SpriteContents(this.spriteLocation, new FrameSize(image.getWidth(), image.getHeight()), image, ResourceMetadata.EMPTY);
            } catch (IllegalArgumentException | IOException e) {
                CColorfulHearts.LOGGER.error("Unable to apply color to {}", this.spriteLocation, e);
            } finally {
                this.image.release();
            }

            return null;
        }

        @Override
        public void discard() {
            this.image.release();
        }
    }
}
