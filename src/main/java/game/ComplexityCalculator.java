package game;

import java.util.ArrayList;
import java.util.List;

/**
 * Computes a complexity score for a Sokoban level based on structural features.
 *
 * <p>Score components (all integers):
 * <ol>
 *   <li>Box count  × 10 – primary driver; more boxes means harder</li>
 *   <li>Map area   / 5  – larger play-field means more navigation</li>
 *   <li>Wall density × 20 – denser walls mean tighter corridors</li>
 *   <li>Avg min-Manhattan distance (box → nearest goal) × 2 – farther initial
 *       placement means more moves required</li>
 * </ol>
 *
 * <p>Difficulty thresholds:
 * <pre>
 *   score ≤ 35  →  Easy
 *   score ≤ 55  →  Medium
 *   score ≤ 75  →  Hard
 *   score  > 75  →  Expert
 * </pre>
 */
public class ComplexityCalculator {

    public static final int THRESHOLD_EASY   = 35;
    public static final int THRESHOLD_MEDIUM = 55;
    public static final int THRESHOLD_HARD   = 75;

    /** Computes the complexity score for the given level map data. */
    public static int compute(String[] mapData) {
        int rows = mapData.length;
        int cols = 0;
        for (String row : mapData) {
            cols = Math.max(cols, row.length());
        }
        int totalCells = rows * cols;

        int wallCount = 0;
        List<int[]> boxPositions  = new ArrayList<>();
        List<int[]> goalPositions = new ArrayList<>();

        for (int r = 0; r < rows; r++) {
            String line = mapData[r];
            for (int c = 0; c < line.length(); c++) {
                char ch = line.charAt(c);
                switch (ch) {
                    case '$': boxPositions.add(new int[]{r, c}); break;
                    case '.': goalPositions.add(new int[]{r, c}); break;
                    case '*': // crate already on goal
                        boxPositions.add(new int[]{r, c});
                        goalPositions.add(new int[]{r, c});
                        break;
                    case '+': goalPositions.add(new int[]{r, c}); break; // player on goal
                    case '#': wallCount++; break;
                    default:  break;
                }
            }
        }

        // Component 1: box count (primary driver)
        int boxScore = boxPositions.size() * 10;

        // Component 2: map area
        int areaScore = totalCells / 5;

        // Component 3: wall density (higher = tighter corridors)
        int wallScore = totalCells > 0 ? (wallCount * 20) / totalCells : 0;

        // Component 4: average min-Manhattan distance from each box to its nearest goal
        int totalDist = 0;
        for (int[] box : boxPositions) {
            int minDist = Integer.MAX_VALUE;
            for (int[] goal : goalPositions) {
                int dist = Math.abs(box[0] - goal[0]) + Math.abs(box[1] - goal[1]);
                if (dist < minDist) minDist = dist;
            }
            if (minDist != Integer.MAX_VALUE) totalDist += minDist;
        }
        int distScore = boxPositions.isEmpty() ? 0 : (totalDist * 2) / boxPositions.size();

        return boxScore + areaScore + wallScore + distScore;
    }

    /** Returns a human-readable difficulty label for the given complexity score. */
    public static String getDifficultyLabel(int score) {
        if (score <= THRESHOLD_EASY)   return "Easy";
        if (score <= THRESHOLD_MEDIUM) return "Medium";
        if (score <= THRESHOLD_HARD)   return "Hard";
        return "Expert";
    }

    /**
     * Validates that the number of boxes equals the number of goal squares.
     * Boxes already on goals ('*') count once toward each total.
     *
     * @throws IllegalArgumentException if counts differ
     */
    public static void validateBoxGoalParity(String levelName, String[] mapData) {
        int boxes = 0, goals = 0;
        for (String row : mapData) {
            for (char ch : row.toCharArray()) {
                switch (ch) {
                    case '$': boxes++; break;
                    case '.': goals++; break;
                    case '*': boxes++; goals++; break; // crate on goal counts for both
                    default:  break;
                }
            }
        }
        if (boxes != goals) {
            throw new IllegalArgumentException(
                "Level \"" + levelName + "\" has " + boxes +
                " box(es) but " + goals + " goal(s). Box and goal counts must be equal."
            );
        }
    }
}
