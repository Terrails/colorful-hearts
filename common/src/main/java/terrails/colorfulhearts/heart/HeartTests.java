package terrails.colorfulhearts.heart;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static terrails.colorfulhearts.CColorfulHearts.LOGGER;

public class HeartTests {

    /**
     * Runs a random collection of tests with different health and absorption values
     * in order to assert that nothing is wrong with the custom Heart.calculateHearts method
     */
    public static void runTests() {
        try {
            checkStandardHearts();
            LOGGER.info("All tests passed for standard heart renderer.");
        } catch (RuntimeException e) {
            if (e.getMessage() == null) {
                LOGGER.error("Tests failed for standard heart renderer.");
                e.printStackTrace();
            } else {
                LOGGER.error("Tests failed for standard heart renderer: {}", e.getMessage());
            }
        }

        try {
            checkSameRowHearts();
            LOGGER.info("All tests passed for same row heart renderer.");
        } catch (RuntimeException e) {
            if (e.getMessage() == null) {
                LOGGER.error("Tests failed for same row heart renderer.");
                e.printStackTrace();
            } else {
                LOGGER.error("Tests failed for same row heart renderer: {}", e.getMessage());
            }
        }
    }

    private static void checkStandardHearts() {
        final List<HeartPiece> healthColors = List.of(HeartPiece.custom("#123456", false), HeartPiece.VANILLA_HEALTH, HeartPiece.custom("#321456", false));
        final List<HeartPiece> absorptionColors = List.of(HeartPiece.custom("#654321", true), HeartPiece.VANILLA_ABSORPTION, HeartPiece.custom("#456123", true));

        checkCommonHearts( false);

        // maxHealth: 123, health: 17, absorption: 11
        {
            final Heart[] testResult = new Heart[16];
            Arrays.fill(testResult, 0, 8, Heart.full(healthColors.get(0)));
            testResult[8] = Heart.half(healthColors.get(0), true);
            testResult[9] = Heart.CONTAINER_FULL;

            Arrays.fill(testResult, 10, 15, Heart.full(absorptionColors.get(0)));
            testResult[15] = Heart.half(absorptionColors.get(0), false);

            testHearts(testResult, 11, 17, 123, healthColors, absorptionColors, false);
        }

        // maxHealth: 17, health: 17, absorption: 11
        {
            final Heart[] testResult = new Heart[15];
            Arrays.fill(testResult, 0, 8, Heart.full(healthColors.get(0)));
            testResult[8] = Heart.half(healthColors.get(0), false);

            Arrays.fill(testResult, 9, 14, Heart.full(absorptionColors.get(0)));
            testResult[14] = Heart.half(absorptionColors.get(0), false);

            testHearts(testResult, 11, 17, 17, healthColors, absorptionColors, false);
        }

        // maxHealth: 17, health: 17, absorption: 11
        {
            final Heart[] testResult = new Heart[15];
            Arrays.fill(testResult, 0, 8, Heart.full(healthColors.get(0)));
            testResult[8] = Heart.half(healthColors.get(0), false);

            Arrays.fill(testResult, 9, 14, Heart.full(absorptionColors.get(0)));
            testResult[14] = Heart.half(absorptionColors.get(0), false);

            testHearts(testResult, 11, 17, 17, healthColors, absorptionColors, false);
        }

        // maxHealth: 123, health: 17, absorption: 31
        {
            final Heart[] testResult = new Heart[20];
            Arrays.fill(testResult, 0, 8, Heart.full(healthColors.get(0)));
            testResult[8] = Heart.half(healthColors.get(0), true);
            testResult[9] = Heart.CONTAINER_FULL;

            Arrays.fill(testResult, 10, 15, Heart.full(absorptionColors.get(1)));
            testResult[15] = Heart.full(absorptionColors.get(1), absorptionColors.get(0));
            Arrays.fill(testResult, 16, 20, Heart.full(absorptionColors.get(0)));

            testHearts(testResult, 31, 17, 123, healthColors, absorptionColors, false);
        }

        // maxHealth: 17, health: 17, absorption: 31
        {
            final Heart[] testResult = new Heart[20];
            Arrays.fill(testResult, 0, 8, Heart.full(healthColors.get(0)));
            testResult[8] = Heart.half(healthColors.get(0), false);

            Arrays.fill(testResult, 9, 13, Heart.full(absorptionColors.get(1)));
            testResult[13] = Heart.full(absorptionColors.get(1), absorptionColors.get(0));
            Arrays.fill(testResult, 14, 20, Heart.full(absorptionColors.get(0)));

            testHearts(testResult, 31, 17, 17, healthColors, absorptionColors, false);
        }
    }

    private static void checkSameRowHearts() {
        final List<HeartPiece> healthColors = List.of(HeartPiece.custom("#123456", false), HeartPiece.VANILLA_HEALTH, HeartPiece.custom("#321456", false));
        final List<HeartPiece> absorptionColors = List.of(HeartPiece.custom("#654321", true), HeartPiece.VANILLA_ABSORPTION, HeartPiece.custom("#456123", true));

        checkCommonHearts(true);

        // maxHealth: 123, health: 17, absorption: 11
        {
            final Heart[] testResult = new Heart[10];
            Arrays.fill(testResult, 0, 4, Heart.full(absorptionColors.get(0)));
            Arrays.fill(testResult, 4, 9, Heart.full(healthColors.get(0)));
            testResult[8] = Heart.full(healthColors.get(0), absorptionColors.get(0));
            testResult[9] = Heart.full(absorptionColors.get(0));

            testHearts(testResult, 11, 17, 123, healthColors, absorptionColors, true);
        }

        // maxHealth: 20, health: 17, absorption: 31
        {
            final Heart[] testResult = new Heart[10];
            Arrays.fill(testResult, 0, 5, Heart.full(absorptionColors.get(1)));
            testResult[5] = Heart.full(absorptionColors.get(1), absorptionColors.get(0));
            Arrays.fill(testResult, 6, 10, Heart.full(absorptionColors.get(0)));

            testHearts(testResult, 31, 17, 20, healthColors, absorptionColors, true);
        }

        // maxHealth: 20, health: 27, absorption: 5
        {
            final Heart[] testResult = new Heart[10];
            Arrays.fill(testResult, 0, 3, Heart.full(healthColors.get(1)));
            testResult[3] = Heart.full(healthColors.get(1), absorptionColors.get(0));
            Arrays.fill(testResult, 4, 6, Heart.full(absorptionColors.get(0)));
            Arrays.fill(testResult, 6, 10, Heart.full(healthColors.get(0)));

            testHearts(testResult, 5, 27, 20, healthColors, absorptionColors, true);
        }
    }

    private static void checkCommonHearts(boolean absorptionSameRow) {
        final List<HeartPiece> healthColors = List.of(HeartPiece.custom("#123456", false), HeartPiece.VANILLA_HEALTH, HeartPiece.custom("#321456", false));
        final List<HeartPiece> absorptionColors = List.of(HeartPiece.custom("#654321", true), HeartPiece.VANILLA_ABSORPTION, HeartPiece.custom("#456123", true));

        // maxHealth: 123, health: 17, absorption: 0
        {
            final Heart[] testResult = new Heart[10];
            Arrays.fill(testResult, 0, 8, Heart.full(healthColors.get(0)));
            testResult[8] = Heart.half(healthColors.get(0), true);
            testResult[9] = Heart.CONTAINER_FULL;

            testHearts(testResult, 0, 17, 123, healthColors, absorptionColors, absorptionSameRow);
        }

        // maxHealth: 123, health: 20, absorption: 0
        {
            final Heart[] testResult = new Heart[10];
            Arrays.fill(testResult, Heart.full(healthColors.get(0)));

            testHearts(testResult, 0, 20, 123, healthColors, absorptionColors, absorptionSameRow);
        }

        // maxHealth: 123, health: 37, absorption: 0
        {
            final Heart[] testResult = new Heart[10];
            Arrays.fill(testResult, 0, 8, Heart.full(healthColors.get(1)));
            testResult[8] = Heart.full(healthColors.get(1), healthColors.get(0));
            testResult[9] = Heart.full(healthColors.get(0));

            testHearts(testResult, 0, 37, 123, healthColors, absorptionColors, absorptionSameRow);
        }

        // maxHealth: 123, health: 40, absorption: 0
        {
            final Heart[] testResult = new Heart[10];
            Arrays.fill(testResult, Heart.full(healthColors.get(1)));

            testHearts(testResult, 0, 40, 123, healthColors, absorptionColors, absorptionSameRow);
        }
    }

    private static void testHearts(Heart[] testResult, int absorption, int health, int maxHealth, List<HeartPiece> healthColors, List<HeartPiece> absorptionColors, boolean absorptionSameRow) {
        Heart[] hearts = Heart.calculateHearts(absorption, health, maxHealth, healthColors, absorptionColors, absorptionSameRow);
        if (!Arrays.equals(testResult, hearts)) {
            String errMsg = "Failed to properly calculate a heart bar with { maxHealth: " + maxHealth + ", health: " + health + ", absorption: " + absorption + ", samRow: " + absorptionSameRow + " }";
            errMsg += "\nExpected: " + testResult.length + " [\n" + Arrays.stream(testResult).map(Heart::toString).collect(Collectors.joining("\n")) + "\n]";
            errMsg += "\nGotten: "  + hearts.length + " [\n" + Arrays.stream(hearts).map(Heart::toString).collect(Collectors.joining("\n")) + "\n]";
            throw new RuntimeException(errMsg);
        }
    }

}
