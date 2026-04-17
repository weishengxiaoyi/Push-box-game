package game;

public class LevelManager {
    // Level 1 – 1 box / 1 goal, 5×7 room (Easy, ~30)
    // Introductory level: player, box, and goal are collinear.
    // Solution: push box one step right onto the goal.
    // Encoding conversion: 0→' ', 1→'#', 2→'$', 3→'.', 4→'@'
    private static final String[] LEVEL1 = {
        "#######",
        "#     #",
        "# @$. #",
        "#     #",
        "#######"
    };

    // Level 2 – 1 box / 1 goal, 8×8 room with interior walls (Medium, ~36)
    // Interior walls create a maze; player must navigate to the correct
    // push position before pushing the box onto the goal.
    private static final String[] LEVEL2 = {
        "########",
        "#      #",
        "# ## # #",
        "# #. # #",
        "#   $  #",
        "# #  #@#",
        "#      #",
        "########"
    };

    // Level 3
    private static final String[] LEVEL3 = {
        "  #######",
        "  #     #",
        "###@*** #",
        "#  *$.$ #",
        "#   $.$ #",
        "#   ..  #",
        "#########"
    };

    // Level 4
    private static final String[] LEVEL4 = {
        " ########",
        " #@  *$ #",
        " # *   .#",
        "###$##* #",
        "# $.#   #",
        "# . #*  #",
        "#####   #",
        "    #####"
    };

    // Level 5
    private static final String[] LEVEL5 = {
        "##############",
        "#           @#",
        "# ########## #",
        "# #        # #",
        "# $$###### # #",
        "###      # . #",
        "  ###### # . #",
        "         #####"
    };

    private final Level[] levels;
    private int currentIndex;

    public LevelManager() {
        levels = new Level[]{
            new Level("Level 1 - Introduction",  LEVEL1, Theme.BEIGE),
            new Level("Level 2 - Deadlock",      LEVEL2, Theme.BROWN),
            new Level("Level 3 - Two Paths",     LEVEL3, Theme.GRAY),
            new Level("Level 4 - Goal Room",     LEVEL4, Theme.BLACK),
            new Level("Level 5 - Multi-Room",    LEVEL5, Theme.YELLOW),
        };
        currentIndex = 0;
        // Validate all levels: box count must equal goal count (1:1 rule).
        for (Level level : levels) {
            ComplexityCalculator.validateBoxGoalParity(level.name, level.mapData);
        }
    }

    public Level getCurrentLevel() {
        return levels[currentIndex];
    }

    public int getCurrentIndex() {
        return currentIndex;
    }

    public int getTotalLevels() {
        return levels.length;
    }

    public boolean hasNext() {
        return currentIndex < levels.length - 1;
    }

    public boolean hasPrevious() {
        return currentIndex > 0;
    }

    public void nextLevel() {
        if (hasNext()) currentIndex++;
    }

    public void previousLevel() {
        if (hasPrevious()) currentIndex--;
    }

    public boolean isLastLevel() {
        return currentIndex == levels.length - 1;
    }
}
