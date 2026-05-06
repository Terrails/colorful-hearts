package terrails.colorfulhearts.config;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;
import java.util.function.Predicate;

public class Configuration {

    private static final Predicate<Object> HEX_COLOR_STRING = (o) -> o instanceof String str && str.matches("#[A-Fa-f0-9]{6}");
    private static final Function<List<String>, List<Integer>> COLOR_DESERIALIZER = strings -> strings.stream().map(Integer::decode).toList();
    private static final Function<List<Integer>, List<String>> COLOR_SERIALIZER = integers -> integers.stream().map(val -> "#" + Integer.toHexString(val).toUpperCase(Locale.ROOT)).toList();

    public static final Health HEALTH = new Health();
    public static final Absorption ABSORPTION = new Absorption();

    public static class Health {

        public final SimpleConfigOption<Boolean> vanillaHearts = new SimpleConfigOption<>(
                "health.vanillaHearts",
                "Render vanilla hearts",
                true
        );

        public final ConfigOption<List<String>, List<Integer>> colors = new ConfigOption<>(
                "health.colors",
                """
                        Colors for every 10 hearts (not counting the default red)
                        All values are written as hexadecimal RGB color in '#RRGGBB' format""",
                Arrays.asList(
                        "#F06E14", "#F5DC23", "#2DB928", "#1EAFBE", "#7346E1",
                        "#FA7DEB", "#EB375A", "#FF8278", "#AAFFFA", "#EBEBFF"
                ),
                COLOR_SERIALIZER, COLOR_DESERIALIZER, HEX_COLOR_STRING
        );
        public final ConfigOption<List<String>, List<Integer>> poisonedColors = new ConfigOption<>(
                "health.poisonedColors",
                """
                        Two alternating colors while poisoned
                        There can be one color in case vanilla poisoned heart is wanted""",
                List.of("#739B00"),
                COLOR_SERIALIZER, COLOR_DESERIALIZER, HEX_COLOR_STRING
        );
        public final ConfigOption<List<String>, List<Integer>> witheredColors = new ConfigOption<>(
                "health.witheredColors",
                """
                        Two alternating colors while withered
                        There can be one color in case vanilla withered heart is wanted""",
                List.of("#0F0F0F"),
                COLOR_SERIALIZER, COLOR_DESERIALIZER, HEX_COLOR_STRING
        );
        public final ConfigOption<List<String>, List<Integer>> frozenColors = new ConfigOption<>(
                "health.frozenColors",
                """
                        Two alternating colors while freezing
                        There can be one color in case vanilla frozen heart is wanted""",
                List.of("#3E70E6"),
                COLOR_SERIALIZER, COLOR_DESERIALIZER, HEX_COLOR_STRING
        );
    }

    public static class Absorption {

        public SimpleConfigOption<Boolean> vanillaHearts = new SimpleConfigOption<>(
                "absorption.vanillaHearts",
                "Render vanilla hearts",
                true
        );

        public final ConfigOption<List<String>, List<Integer>> colors = new ConfigOption<>(
                "absorption.colors",
                """
                        Colors for every 10 hearts (not counting the default red)
                        All values are written as hexadecimal RGB color in '#RRGGBB' format""",
                Arrays.asList(
                        "#E1FA9B", "#A0FFAF", "#AAFFFA", "#AACDFF", "#D7B4FF",
                        "#FAA5FF", "#FFB4B4", "#FFAA7D", "#D7F0FF", "#EBFFFA"
                ),
                COLOR_SERIALIZER, COLOR_DESERIALIZER, HEX_COLOR_STRING
        );
        public final ConfigOption<List<String>, List<Integer>> poisonedColors = new ConfigOption<>(
                "absorption.poisonedColors",
                "Two alternating colors while poisoned",
                Arrays.asList("#BFF230", "#7AA15A"),
                COLOR_SERIALIZER, COLOR_DESERIALIZER, HEX_COLOR_STRING
        );
        public final ConfigOption<List<String>, List<Integer>> witheredColors = new ConfigOption<>(
                "absorption.witheredColors",
                "Two alternating colors while withered",
                Arrays.asList("#787061", "#73625C"),
                COLOR_SERIALIZER, COLOR_DESERIALIZER, HEX_COLOR_STRING
        );
        public final ConfigOption<List<String>, List<Integer>> frozenColors = new ConfigOption<>(
                "absorption.frozenColors",
                "Two alternating colors while freezing",
                Arrays.asList("#90D136", "#36D183"),
                COLOR_SERIALIZER, COLOR_DESERIALIZER, HEX_COLOR_STRING
        );
    }

}
