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

    // Level 2 – 2 boxes / 2 goals, fully enclosed 7×7 room, ~16 steps
    // Verified solution (16 moves): LLUUUURDRRRDDDLU
    //   LLUUUURD: navigate to (1,2), push box(2,2) down to goal(3,2)
    //   RRRDDDLU: navigate to (5,4), push box(4,4) up to goal(3,4)
    private static final String[] LEVEL2 = {
        "#######",
        "#     #",
        "# $   #",
        "# . . #",
        "#   $ #",
        "#  @  #",
        "#######"
    };

    // Level 3 – 3 boxes / 3 goals, fully enclosed 8×8 room, ~18 steps
    // Boxes at (2,2),(3,3),(4,4) each pushed down their column to goals at (5,2),(5,3),(5,4).
    // Verified solution (18 moves): RUUULDUULDDUUULDDD
    //   RUUULD: push box(4,4)→goal(5,4)
    //   UULDD:  push box(3,3)→(4,3)→goal(5,3)
    //   UUULDDD: push box(2,2)→(3,2)→(4,2)→goal(5,2)
    private static final String[] LEVEL3 = {
        "########",
        "#      #",
        "# $    #",
        "#  $   #",
        "#   $  #",
        "# ...  #",
        "#   @  #",
        "########"
    };

    // Level 4 – 4 boxes / 4 goals, fully enclosed 9×10 room, ~25 steps
    // Upper boxes (2,3)&(2,6) pushed down 2 rows to goals (4,3)&(4,6).
    // Lower boxes (6,2)&(6,5) pushed down 1 row to goals (7,2)&(7,5).
    // Verified solution (25 moves): UUURDULLLDUUUUURDDUURRRDD
    //   UUURD:    push box(6,5)→goal(7,5)
    //   ULLLD:    push box(6,2)→goal(7,2)
    //   UUUUURDD: push box(2,3)→(3,3)→goal(4,3)
    //   UURRRDD:  push box(2,6)→(3,6)→goal(4,6)
    private static final String[] LEVEL4 = {
        "#########",
        "#       #",
        "#  $  $ #",
        "#       #",
        "#  .  . #",
        "#       #",
        "# $  $  #",
        "# .  .  #",
        "#   @   #",
        "#########"
    };

    // Level 5 – 5 boxes / 5 goals, fully enclosed 9×11 room, ~39 steps
    // Three upper boxes (2,2),(2,4),(2,6) each pushed down 3 rows to goals (5,2),(5,4),(5,6).
    // Two lower boxes (7,2)&(7,6) pushed down 1 row to goals (8,2)&(8,6).
    // Verified solution (39 moves): UUULLDURRRRDUUUULUURDDDUUULLDDDUUULLDDD
    //   UUULLD:      push box(7,2)→goal(8,2)
    //   URRRRD:      push box(7,6)→goal(8,6)
    //   UUUULUURDDD: push box(2,6)→(3,6)→(4,6)→goal(5,6)
    //   UUULLDDD:    push box(2,4)→(3,4)→(4,4)→goal(5,4)
    //   UUULLDDD:    push box(2,2)→(3,2)→(4,2)→goal(5,2)
    private static final String[] LEVEL5 = {
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
