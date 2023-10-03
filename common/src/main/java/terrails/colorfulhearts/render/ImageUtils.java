package terrails.colorfulhearts.render;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;

import java.util.function.IntUnaryOperator;

public class ImageUtils {

    /**
     * Blends two images using normal blend mode (A over B pre-multiplied alpha compositing method).
     *      f(a, b) = a + b * (1 - alpha_a) / 255 -> clamp(f(a, b), 0, 255)
     * As for alpha channel, the function takes the highest of two and uses it for the end color
     */
    public static void blendNormal(NativeImage base, NativeImage blend) {
        int width = Math.max(base.getWidth(), blend.getWidth());
        int height = Math.max(base.getHeight(), blend.getHeight());

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int pixelBase = base.isOutsideBounds(x, y) ? 0 : base.getPixelRGBA(x, y);
                int pixelBlend = blend.isOutsideBounds(x, y) ? 0 : blend.getPixelRGBA(x, y);

                int alphaBackground = FastColor.ABGR32.alpha(pixelBase);
                // no point in blending if source alpha is 0
                if (alphaBackground == 0) {
                    base.setPixelRGBA(x, y, pixelBlend);
                    continue;
                }
                int blueBackground = FastColor.ABGR32.blue(pixelBase);
                int greenBackground = FastColor.ABGR32.green(pixelBase);
                int redBackground = FastColor.ABGR32.red(pixelBase);

                int alphaForeground = FastColor.ABGR32.alpha(pixelBlend);
                // skip over pixel if nothing is going to be drawn over
                if (alphaForeground == 0) {
                    continue;
                }
                int blueForeground = FastColor.ABGR32.blue(pixelBlend);
                int greenForeground = FastColor.ABGR32.green(pixelBlend);
                int redForeground = FastColor.ABGR32.red(pixelBlend);

                int alphaInvert = 255 - alphaForeground;

                // take the max of two alpha
                int alpha = Math.max(alphaForeground, alphaBackground);
                int blue = Mth.clamp((blueForeground * alphaForeground + blueBackground * alphaInvert) / 255, 0, 255);
                int green = Mth.clamp((greenForeground * alphaForeground + greenBackground * alphaInvert) / 255, 0, 255);
                int red = Mth.clamp((redForeground * alphaForeground + redBackground * alphaInvert) / 255, 0, 255);

                int color = FastColor.ABGR32.color(alpha, blue, green, red);
                base.setPixelRGBA(x, y, color);
            }
        }
    }

    /**
     * Blends two images using multiply blend mode.
     *      f(a, b) = ab / 255
     * As for alpha channel, the function takes the highest of two and uses it for the end color
     */
    public static void blendMultiply(NativeImage base, NativeImage blend) {
        int width = Math.max(base.getWidth(), blend.getWidth());
        int height = Math.max(base.getHeight(), blend.getHeight());

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int pixelBase = base.isOutsideBounds(x, y) ? 0 : base.getPixelRGBA(x, y);
                int pixelBlend = blend.isOutsideBounds(x, y) ? 0 : blend.getPixelRGBA(x, y);

                int alphaBase = FastColor.ABGR32.alpha(pixelBase);
                if (alphaBase == 0) {
                    base.setPixelRGBA(x, y, pixelBlend);
                    continue;
                }

                int alphaBlend = FastColor.ABGR32.alpha(pixelBlend);
                if (alphaBlend == 0) {
                    continue;
                }

                int blueBase = FastColor.ABGR32.blue(pixelBase);
                int greenBase = FastColor.ABGR32.green(pixelBase);
                int redBase = FastColor.ABGR32.red(pixelBase);

                int blueBlend = FastColor.ABGR32.blue(pixelBlend);
                int greenBlend = FastColor.ABGR32.green(pixelBlend);
                int redBlend = FastColor.ABGR32.red(pixelBlend);

                // take the max of two alpha
                final int alpha = Math.max(alphaBase, alphaBlend);
                final int blue = blueBase * blueBlend / 255;
                final int green = greenBase * greenBlend / 255;
                final int red = redBase * redBlend / 255;

                int color = FastColor.ABGR32.color(alpha, blue, green, red);
                base.setPixelRGBA(x, y, color);
            }
        }
    }

    /**
     * Blends two images using screen blend mode.
     *      f(a, b) = 255 - (255 - a) * (255 - b) / 255 -> clamp(f(a,b), 0, 255)
     * As for alpha channel, the function takes the highest of two and uses it for the end color
     */
    public static void blendScreen(NativeImage base, NativeImage blend) {
        int width = Math.max(base.getWidth(), blend.getWidth());
        int height = Math.max(base.getHeight(), blend.getHeight());

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int pixelBase = base.isOutsideBounds(x, y) ? 0 : base.getPixelRGBA(x, y);
                int pixelBlend = blend.isOutsideBounds(x, y) ? 0 : blend.getPixelRGBA(x, y);

                int alphaBase = FastColor.ABGR32.alpha(pixelBase);
                if (alphaBase == 0) {
                    base.setPixelRGBA(x, y, pixelBlend);
                    continue;
                }

                int alphaBlend = FastColor.ABGR32.alpha(pixelBlend);
                if (alphaBlend == 0) {
                    continue;
                }

                int blueBase = FastColor.ABGR32.blue(pixelBase);
                int greenBase = FastColor.ABGR32.green(pixelBase);
                int redBase = FastColor.ABGR32.red(pixelBase);

                int blueBlend = FastColor.ABGR32.blue(pixelBlend);
                int greenBlend = FastColor.ABGR32.green(pixelBlend);
                int redBlend = FastColor.ABGR32.red(pixelBlend);

                // take the max of two alpha
                int alpha = Math.max(alphaBase, alphaBlend);
                int blue = Mth.clamp(255 - (255 - blueBase) * (255 - blueBlend) / 255, 0, 255);
                int green = Mth.clamp(255 - (255 - greenBase) * (255 - greenBlend) / 255, 0, 255);
                int red = Mth.clamp(255 - (255 - redBase) * (255 - redBlend) / 255, 0, 255);

                int color = FastColor.ABGR32.color(alpha, blue, green, red);
                base.setPixelRGBA(x, y, color);
            }
        }
    }

    /**
     * Blends two images using overlay blend mode.
     *      f(a, b):
     *          if a < 128:
     *              2ab
     *          else:
     *              255 - 2 * (255 - a) * (255 - b) / 255
     * As for alpha channel, the function takes the highest of two and uses it for the end color
     */
    public static void blendOverlay(NativeImage base, NativeImage blend) {
        int width = Math.max(base.getWidth(), blend.getWidth());
        int height = Math.max(base.getHeight(), blend.getHeight());

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int pixelBase = base.isOutsideBounds(x, y) ? 0 : base.getPixelRGBA(x, y);
                int pixelBlend = blend.isOutsideBounds(x, y) ? 0 : blend.getPixelRGBA(x, y);

                int alphaBase = FastColor.ABGR32.alpha(pixelBase);
                if (alphaBase == 0) {
                    base.setPixelRGBA(x, y, pixelBlend);
                    continue;
                }

                int alphaBlend = FastColor.ABGR32.alpha(pixelBlend);
                if (alphaBlend == 0) {
                    continue;
                }

                int blueBase = FastColor.ABGR32.blue(pixelBase);
                int greenBase = FastColor.ABGR32.green(pixelBase);
                int redBase = FastColor.ABGR32.red(pixelBase);

                int blueBlend = FastColor.ABGR32.blue(pixelBlend);
                int greenBlend = FastColor.ABGR32.green(pixelBlend);
                int redBlend = FastColor.ABGR32.red(pixelBlend);

                // take the max of two alpha
                final int alpha = Math.max(alphaBase, alphaBlend);
                final int blue, green, red;

                if (blueBase < 128) {
                    blue = Mth.clamp(2 * (blueBase * blueBlend / 255), 0, 255);
                } else {
                    blue = Mth.clamp(255 - (2 * (alphaBlend - blueBase) * (alphaBase - blueBlend) / 255), 0, 255);
                }

                if (greenBase < 128) {
                    green = Mth.clamp(2 * (greenBase * greenBlend / 255), 0, 255);
                } else {
                    green = Mth.clamp(255 - (2 * (alphaBlend - greenBase) * (alphaBase - greenBlend) / 255), 0, 255);
                }

                if (redBase < 128) {
                    red = Mth.clamp(2 * (redBase * redBlend / 255), 0, 255);
                } else {
                    red = Mth.clamp(255 - (2 * (alphaBlend - redBase) * (alphaBase - redBlend) / 255), 0, 255);
                }

                int color = FastColor.ABGR32.color(alpha, blue, green, red);
                base.setPixelRGBA(x, y, color);
            }
        }
    }

    /**
     * Returns a unary operator to color all given ABGR pixels with the given ARGB color using overlay blend mode.
     * Designed for {@link NativeImage#applyToAllPixels(IntUnaryOperator)} and {@link NativeImage#mappedCopy(IntUnaryOperator)}
     */
    public static IntUnaryOperator getColorOverlayOperator(int redColor, int greenColor, int blueColor) {
        return (pixel) -> {
            int alphaBase = FastColor.ABGR32.alpha(pixel);
            // no need to do any extra processing as nothing is going to be colored
            if (alphaBase == 0) return pixel;

            int blueBase = FastColor.ABGR32.blue(pixel);
            int greenBase = FastColor.ABGR32.green(pixel);
            int redBase = FastColor.ABGR32.red(pixel);

            final int blue, green, red;

            if (blueBase < 128) {
                blue = 2 * (blueBase * blueColor / 255);
            } else {
                blue = 255 - (2 * (255 - blueBase) * (alphaBase - blueColor) / 255);
            }

            if (greenBase < 128) {
                green = 2 * (greenBase * greenColor / 255);
            } else {
                green = 255 - (2 * (255 - greenBase) * (alphaBase - greenColor) / 255);
            }

            if (redBase < 128) {
                red = 2 * (redBase * redColor / 255);
            } else {
                red = 255 - (2 * (255 - redBase) * (alphaBase - redColor) / 255);
            }

            return FastColor.ABGR32.color(alphaBase, blue, green, red);
        };
    }
}
