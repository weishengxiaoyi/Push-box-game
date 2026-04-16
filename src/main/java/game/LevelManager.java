package game;

public class LevelManager {
    private static final String[] LEVEL1 = {
        "#######",
        "#     #",
        "#  $  #",
        "## .  #",
        " #    #",
        " #  @ #",
        " ######"
    };

    private static final String[] LEVEL2 = {
        "########",
        "#   #  #",
        "# $ $  #",
        "## ## ##",
        "#  .@. #",
        "#      #",
        "########"
    };

    private static final String[] LEVEL3 = {
        " #######",
        "##  #  #",
        "#  $   #",
        "# $ .#.#",
        "# .@$  #",
        "#  ##  #",
        "########"
    };

    private static final String[] LEVEL4 = {
        "  #####",
        "###   ##",
        "#  $$ .#",
        "# $  . #",
        "## @.$.#",
        " # . ###",
        " #####  "
    };

    private static final String[] LEVEL5 = {
        "########",
        "## ## ##",
        "#  $  .#",
        "#.$@$.##",
        "#  $  .#",
        "## . ###",
        "########"
    };

    private final Level[] levels;
    private int currentIndex;

    public LevelManager() {
        levels = new Level[]{
            new Level("Level 1 - Tutorial",     LEVEL1, Theme.BEIGE),
            new Level("Level 2 - Two Boxes",    LEVEL2, Theme.BROWN),
            new Level("Level 3 - Three Boxes",  LEVEL3, Theme.GRAY),
            new Level("Level 4 - Four Boxes",   LEVEL4, Theme.BLACK),
            new Level("Level 5 - Five Boxes",   LEVEL5, Theme.YELLOW),
        };
        currentIndex = 0;
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
