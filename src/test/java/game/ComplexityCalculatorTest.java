package game;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link ComplexityCalculator}.
 *
 * Verifies the complexity scoring formula, difficulty labels,
 * ascending ordering across the built-in levels, and the box/goal
 * parity validation.
 */
class ComplexityCalculatorTest {

    // ─────────────────────────────────────────────────────────────
    // Difficulty label thresholds
    // ─────────────────────────────────────────────────────────────

    @Test
    void difficultyLabels_boundaryValues() {
        assertEquals("Easy",   ComplexityCalculator.getDifficultyLabel(0));
        assertEquals("Easy",   ComplexityCalculator.getDifficultyLabel(ComplexityCalculator.THRESHOLD_EASY));
        assertEquals("Medium", ComplexityCalculator.getDifficultyLabel(ComplexityCalculator.THRESHOLD_EASY + 1));
        assertEquals("Medium", ComplexityCalculator.getDifficultyLabel(ComplexityCalculator.THRESHOLD_MEDIUM));
        assertEquals("Hard",   ComplexityCalculator.getDifficultyLabel(ComplexityCalculator.THRESHOLD_MEDIUM + 1));
        assertEquals("Hard",   ComplexityCalculator.getDifficultyLabel(ComplexityCalculator.THRESHOLD_HARD));
        assertEquals("Expert", ComplexityCalculator.getDifficultyLabel(ComplexityCalculator.THRESHOLD_HARD + 1));
        assertEquals("Expert", ComplexityCalculator.getDifficultyLabel(999));
    }

    // ─────────────────────────────────────────────────────────────
    // Score for the five built-in levels falls in expected ranges
    // ─────────────────────────────────────────────────────────────

    /** Level 1 (1 box, small L-shaped map) should classify as Easy. */
    @Test
    void level1_scoreIsEasy() {
        String[] map = {
            "#######",
            "#     #",
            "#  $  #",
            "## .  #",
            " #    #",
            " #  @ #",
            " ######"
        };
        int score = ComplexityCalculator.compute(map);
        assertTrue(score <= ComplexityCalculator.THRESHOLD_EASY,
            "Level 1 should be Easy, but got score " + score);
    }

    /** Level 2 (2 boxes, enclosed 7×7) should classify as Medium. */
    @Test
    void level2_scoreIsMedium() {
        String[] map = {
            "#######",
            "#     #",
            "# $   #",
            "# . . #",
            "#   $ #",
            "#  @  #",
            "#######"
        };
        int score = ComplexityCalculator.compute(map);
        assertTrue(score > ComplexityCalculator.THRESHOLD_EASY &&
                   score <= ComplexityCalculator.THRESHOLD_MEDIUM,
            "Level 2 should be Medium, but got score " + score);
    }

    /** Level 5 (5 boxes, largest map) should classify as Expert. */
    @Test
    void level5_scoreIsExpert() {
        String[] map = {
            "#########",
            "#       #",
            "# $ $ $ #",
            "#       #",
            "#       #",
            "# . . . #",
            "#       #",
            "# $   $ #",
            "# .   . #",
            "#   @   #",
            "#########"
        };
        int score = ComplexityCalculator.compute(map);
        assertTrue(score > ComplexityCalculator.THRESHOLD_HARD,
            "Level 5 should be Expert, but got score " + score);
    }

    // ─────────────────────────────────────────────────────────────
    // Built-in levels have strictly increasing complexity scores
    // ─────────────────────────────────────────────────────────────

    @Test
    void builtInLevels_scoresAreStrictlyIncreasing() {
        LevelManager lm = new LevelManager();
        int prevScore = -1;
        for (int i = 0; i < lm.getTotalLevels(); i++) {
            Level level = lm.getCurrentLevel();
            assertTrue(level.complexityScore > prevScore,
                "Level " + (i + 1) + " complexity score (" + level.complexityScore +
                ") must exceed previous score (" + prevScore + ")");
            prevScore = level.complexityScore;
            if (lm.hasNext()) lm.nextLevel();
        }
    }

    // ─────────────────────────────────────────────────────────────
    // The complexityScore stored in Level equals compute(mapData)
    // ─────────────────────────────────────────────────────────────

    @Test
    void levelComplexityScore_matchesCompute() {
        LevelManager lm = new LevelManager();
        for (int i = 0; i < lm.getTotalLevels(); i++) {
            Level level = lm.getCurrentLevel();
            int expected = ComplexityCalculator.compute(level.mapData);
            assertEquals(expected, level.complexityScore,
                "Level " + (i + 1) + " stored complexityScore should match compute()");
            if (lm.hasNext()) lm.nextLevel();
        }
    }

    // ─────────────────────────────────────────────────────────────
    // Box / goal parity validation
    // ─────────────────────────────────────────────────────────────

    @Test
    void validateBoxGoalParity_passesForMatchingCounts() {
        // 2 boxes, 2 goals – should not throw
        String[] map = {
            "#####",
            "# $.#",
            "# $.#",
            "#  @#",
            "#####"
        };
        assertDoesNotThrow(() ->
            ComplexityCalculator.validateBoxGoalParity("test", map));
    }

    @Test
    void validateBoxGoalParity_throwsWhenBoxesExceedGoals() {
        String[] map = {
            "#####",
            "# $ #",  // box, no goal
            "# . #",  // goal
            "#$@ #",  // extra box, no matching goal
            "#####"
        };
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
            ComplexityCalculator.validateBoxGoalParity("Bad Level", map));
        assertTrue(ex.getMessage().contains("Bad Level"),
            "Exception message should mention the level name");
    }

    @Test
    void validateBoxGoalParity_throwsWhenGoalsExceedBoxes() {
        String[] map = {
            "#####",
            "# . #",  // goal, no box
            "# . #",  // goal
            "#$@ #",  // only 1 box
            "#####"
        };
        assertThrows(IllegalArgumentException.class, () ->
            ComplexityCalculator.validateBoxGoalParity("Bad Level 2", map));
    }

    @Test
    void validateBoxGoalParity_crateOnGoalCountsForBoth() {
        // '*' counts as 1 box AND 1 goal, so this map is balanced
        String[] map = {
            "#####",
            "# * #",  // crate-on-goal
            "#   #",
            "# @ #",
            "#####"
        };
        assertDoesNotThrow(() ->
            ComplexityCalculator.validateBoxGoalParity("test-crate-on-goal", map));
    }

    @Test
    void allBuiltInLevels_passBoxGoalValidation() {
        // LevelManager constructor calls validateBoxGoalParity; this just confirms it runs cleanly
        assertDoesNotThrow(LevelManager::new,
            "All built-in levels must satisfy the 1:1 box/goal rule");
    }
}
