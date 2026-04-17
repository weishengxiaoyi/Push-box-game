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

    // Level 3 – 2 boxes / 2 goals, 9×9 two-room layout (Medium, ~54)
    // Two separate rooms connected by a central corridor; ordering matters
    // because the boxes block each other's paths if pushed in the wrong order.
    private static final String[] LEVEL3 = {
        "#########",
        "#   #   #",
        "# $ # . #",
        "#       #",
        "### # ###",
        "#   #   #",
        "# . # $@#",
        "#       #",
        "#########"
    };

    // Level 4 – 3 boxes / 3 goals, 10×10 two-section room (Hard, ~70)
    // Three goals are clustered in the upper-right section ("goal room");
    // boxes must be pushed in the correct fill order or the entrance is blocked.
    private static final String[] LEVEL4 = {
        "##########",
        "#   #    #",
        "# $ # ...#",
        "#   #    #",
        "### ### ##",
        "#        #",
        "# $ #    #",
        "#   # $ @#",
        "#        #",
        "##########"
    };

    // Level 5 – 4 boxes / 4 goals, 11×12 multi-room layout (Expert, ~79)
    // Boxes and goals are split across multiple rooms connected by narrow
    // corridors; optimal push order requires careful planning to avoid
    // permanent deadlocks.
    // Note: box at (3,9) was added to satisfy the 1:1 box/goal parity
    // requirement (original design had 3 boxes but 4 goals).
    private static final String[] LEVEL5 = {
        "############",
        "#   #      #",
        "# $ # . #. #",
        "#   #   # $#",
        "### ### # ##",
        "#          #",
        "# $ # ### $#",
        "#   #      #",
        "# . #  @  .#",
        "#   #      #",
        "############"
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
