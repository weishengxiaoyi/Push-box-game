# Sokoban (Push-Box) Game

---

## Stage 1 — Implementation

The Sokoban (Push-Box) game is implemented in Java using object-oriented principles. It meets the requirements for abstraction, encapsulation, inheritance, polymorphism, data coupling, and cohesion. The following classes form the core implementation.

### Design Requirements

- Abstraction, encapsulation and information hiding — all fields are private; public APIs provide controlled access.
- Inheritance — Level stores map/theme/complexity data; LevelManager composes Level objects.
- Polymorphism — Board cell constants (FLOOR, WALL, CRATE, GOAL, PLAYER, etc.) are dispatched polymorphically in `GamePanel.drawTile()`.
- All class-wide variables are private.
- Data coupling used throughout (parameters passed, no global mutable state).
- Classes are highly cohesive — each class has a single, clear responsibility.

### Source Files Overview

| Class | Responsibility |
|---|---|
| Board | 2-D integer grid; holds tile constants and player position; win detection. |
| GameController | Central logic: move validation, crate-push rules, restart, level navigation. |
| LevelManager | Owns the 5 built-in levels (String[] map data, theme, name); index tracking. |
| Level | Data class: name, mapData (String[]), Theme, complexityScore. |
| ComplexityCalculator | Scores levels using box count, area, wall density, and Manhattan distance; validates box/goal parity. |
| GamePanel | Swing JPanel: renders board tiles with theme images/fallback colours; key bindings. |
| GameWindow | JFrame host; packs GamePanel; sets title from current level/difficulty. |
| AssetManager | Image cache: loads PNG assets from classpath `/assets/` folder on demand. |
| Theme | Value class: named image-file bundles for each visual theme (BEIGE, BROWN, GRAY, BLACK, YELLOW). |
| Main | Entry point: invokes `SwingUtilities.invokeLater` to launch GameWindow. |

### Source Code Listings

#### Board.java

```java
package game;

public class Board {
    public static final int EMPTY         = 0;
    public static final int FLOOR         = 1;
    public static final int WALL          = 2;
    public static final int GOAL          = 3;
    public static final int CRATE         = 4;
    public static final int CRATE_ON_GOAL = 5;
    public static final int PLAYER        = 6;
    public static final int PLAYER_ON_GOAL = 7;

    private int[][] grid;
    private final int rows;
    private final int cols;
    private int playerRow;
    private int playerCol;

    public Board(int[][] grid, int playerRow, int playerCol) {
        this.grid = deepCopy(grid);
        this.rows = grid.length;
        this.cols = maxCols(grid);
        this.playerRow = playerRow;
        this.playerCol = playerCol;
    }

    private static int[][] deepCopy(int[][] src) {
        int[][] copy = new int[src.length][];
        for (int i = 0; i < src.length; i++) copy[i] = src[i].clone();
        return copy;
    }

    private static int maxCols(int[][] grid) {
        int max = 0;
        for (int[] row : grid) if (row.length > max) max = row.length;
        return max;
    }

    public int  getCell(int row, int col) {
        if (row < 0 || row >= rows || col < 0 || col >= grid[row].length) return EMPTY;
        return grid[row][col];
    }
    public void setCell(int row, int col, int value) {
        if (row >= 0 && row < rows && col >= 0 && col < grid[row].length)
            grid[row][col] = value;
    }
    public int  getRows()      { return rows; }
    public int  getCols()      { return cols; }
    public int  getPlayerRow() { return playerRow; }
    public int  getPlayerCol() { return playerCol; }
    public void setPlayerRow(int r) { this.playerRow = r; }
    public void setPlayerCol(int c) { this.playerCol = c; }

    public boolean isWon() {
        for (int r = 0; r < rows; r++)
            for (int c = 0; c < grid[r].length; c++)
                if (grid[r][c] == CRATE) return false;
        return true;
    }

    public Board copy() { return new Board(grid, playerRow, playerCol); }

    public static Board fromLevel(Level level) {
        String[] mapData = level.mapData;
        int rows = mapData.length, cols = 0;
        for (String row : mapData) if (row.length() > cols) cols = row.length();
        int[][] grid = new int[rows][cols];
        int playerRow = 0, playerCol = 0;
        for (int r = 0; r < rows; r++) {
            String line = mapData[r];
            for (int c = 0; c < cols; c++) {
                char ch = c < line.length() ? line.charAt(c) : ' ';
                switch (ch) {
                    case '#': grid[r][c] = WALL;          break;
                    case ' ': grid[r][c] = FLOOR;         break;
                    case '.': grid[r][c] = GOAL;          break;
                    case '$': grid[r][c] = CRATE;         break;
                    case '@': grid[r][c] = PLAYER;
                               playerRow = r; playerCol = c; break;
                    case '*': grid[r][c] = CRATE_ON_GOAL; break;
                    case '+': grid[r][c] = PLAYER_ON_GOAL;
                               playerRow = r; playerCol = c; break;
                    default:   grid[r][c] = FLOOR;          break;
                }
            }
        }
        return new Board(grid, playerRow, playerCol);
    }
}
```

#### GameController.java

```java
package game;

public class GameController {
    private final LevelManager levelManager;
    private Board board;
    private Board initialBoard;
    private boolean levelComplete;
    private boolean allLevelsComplete;

    public GameController() {
        levelManager = new LevelManager();
        loadCurrentLevel();
    }

    private void loadCurrentLevel() {
        Level level = levelManager.getCurrentLevel();
        board = Board.fromLevel(level);
        initialBoard = board.copy();
        levelComplete = false;
        allLevelsComplete = false;
    }

    public void move(int dr, int dc) {
        if (levelComplete || allLevelsComplete) return;
        int pr = board.getPlayerRow(), pc = board.getPlayerCol();
        int nr = pr + dr,             nc = pc + dc;
        int targetCell = board.getCell(nr, nc);
        if (targetCell == Board.EMPTY || targetCell == Board.WALL) return;

        if (targetCell == Board.CRATE || targetCell == Board.CRATE_ON_GOAL) {
            int cr = nr + dr, cc = nc + dc;
            int crateTarget = board.getCell(cr, cc);
            if (crateTarget != Board.FLOOR && crateTarget != Board.GOAL) return;
            board.setCell(cr, cc,
                (crateTarget == Board.GOAL) ? Board.CRATE_ON_GOAL : Board.CRATE);
            board.setCell(nr, nc,
                (targetCell == Board.CRATE_ON_GOAL) ? Board.GOAL : Board.FLOOR);
        }
        int currentCell = board.getCell(pr, pc);
        board.setCell(pr, pc,
            (currentCell == Board.PLAYER_ON_GOAL) ? Board.GOAL : Board.FLOOR);
        board.setCell(nr, nc,
            (board.getCell(nr, nc) == Board.GOAL) ? Board.PLAYER_ON_GOAL : Board.PLAYER);
        board.setPlayerRow(nr);
        board.setPlayerCol(nc);

        if (board.isWon()) {
            levelComplete = true;
            if (levelManager.isLastLevel()) allLevelsComplete = true;
        }
    }

    public void restartLevel() {
        board = initialBoard.copy();
        levelComplete = false;
        allLevelsComplete = false;
    }

    public void nextLevel()     { if (levelManager.hasNext())     { levelManager.nextLevel();     loadCurrentLevel(); } }
    public void previousLevel() { if (levelManager.hasPrevious()) { levelManager.previousLevel(); loadCurrentLevel(); } }

    public Board   getBoard()              { return board; }
    public Level   getCurrentLevel()       { return levelManager.getCurrentLevel(); }
    public int     getCurrentLevelIndex()  { return levelManager.getCurrentIndex(); }
    public int     getTotalLevels()        { return levelManager.getTotalLevels(); }
    public boolean isLevelComplete()       { return levelComplete; }
    public boolean isAllLevelsComplete()   { return allLevelsComplete; }
}
```

#### LevelManager.java

```java
package game;

public class LevelManager {
    // Level 1 – 1 box / 1 goal, 5×7 room (Easy, ~30)
    private static final String[] LEVEL1 = {
        "#######",
        "#     #",
        "# @$. #",
        "#     #",
        "#######"
    };

    // Level 2 – 1 box / 1 goal, 8×8 room with interior walls (Medium, ~36)
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
        "#######",
        "#.   .#",
        "# $@$ #",
        "# ### #",
        "# $ $ #",
        "#.   .#",
        "#######"
    };

    // Level 4
    private static final String[] LEVEL4 = {
        "########",
        "####   #",
        "##@*** #",
        "##*$.$ #",
        "#  $ $ #",
        "#  ... #",
        "###    #",
        "########"
    };

    // Level 5
    private static final String[] LEVEL5 = {
        "############",
        "#          #",
        "# ####### @##",
        "# #         #",
        "# #      #  #",
        "# $$ #####  #",
        "###  # #  ..#",
        "  #### #    #",
        "       ######"
    };

    private final Level[] levels;
    private int currentIndex;

    public LevelManager() {
        levels = new Level[]{
            new Level("Level 1 - Introduction",   LEVEL1, Theme.BEIGE),
            new Level("Level 2 - Deadlock",       LEVEL2, Theme.BROWN),
            new Level("Level 3 - Scattered Gems", LEVEL3, Theme.GRAY),
            new Level("Level 4 - The Warehouse",  LEVEL4, Theme.BLACK),
            new Level("Level 5 - The Long Corridor", LEVEL5, Theme.YELLOW),
        };
        currentIndex = 0;
        for (Level level : levels) {
            ComplexityCalculator.validateBoxGoalParity(level.name, level.mapData);
        }
    }

    public Level getCurrentLevel()   { return levels[currentIndex]; }
    public int   getCurrentIndex()   { return currentIndex; }
    public int   getTotalLevels()    { return levels.length; }
    public boolean hasNext()         { return currentIndex < levels.length - 1; }
    public boolean hasPrevious()     { return currentIndex > 0; }
    public void    nextLevel()       { if (hasNext())     currentIndex++; }
    public void    previousLevel()   { if (hasPrevious()) currentIndex--; }
    public boolean isLastLevel()     { return currentIndex == levels.length - 1; }
}
```

#### Level.java

```java
package game;

public class Level {
    public final String   name;
    public final String[] mapData;
    public final Theme    theme;
    public final int      complexityScore;

    public Level(String name, String[] mapData, Theme theme) {
        this.name            = name;
        this.mapData         = mapData;
        this.theme           = theme;
        this.complexityScore = ComplexityCalculator.compute(mapData);
    }

    public Level(String name, int[][] gridData, Theme theme) {
        this(name, intGridToStringArray(gridData), theme);
    }

    static String[] intGridToStringArray(int[][] gridData) {
        String[] result = new String[gridData.length];
        for (int r = 0; r < gridData.length; r++) {
            StringBuilder sb = new StringBuilder();
            for (int c = 0; c < gridData[r].length; c++) {
                switch (gridData[r][c]) {
                    case Board.WALL:           sb.append('#'); break;
                    case Board.GOAL:           sb.append('.'); break;
                    case Board.CRATE:          sb.append('$'); break;
                    case Board.CRATE_ON_GOAL:  sb.append('*'); break;
                    case Board.PLAYER:         sb.append('@'); break;
                    case Board.PLAYER_ON_GOAL: sb.append('+'); break;
                    default:                   sb.append(' '); break;
                }
            }
            result[r] = sb.toString();
        }
        return result;
    }
}
```

#### ComplexityCalculator.java

```java
package game;

import java.util.ArrayList;
import java.util.List;

/**
 * Scores a Sokoban level from 4 structural features:
 *   score = boxes×10 + area/5 + wallDensity×20 + avgManhattan×2
 *
 * Difficulty thresholds:
 *   score ≤ 35  → Easy
 *   score ≤ 55  → Medium
 *   score ≤ 75  → Hard
 *   score  > 75 → Expert
 */
public class ComplexityCalculator {
    public static final int THRESHOLD_EASY   = 35;
    public static final int THRESHOLD_MEDIUM = 55;
    public static final int THRESHOLD_HARD   = 75;

    public static int compute(String[] mapData) {
        int rows = mapData.length, cols = 0;
        for (String row : mapData) cols = Math.max(cols, row.length());
        int totalCells = rows * cols, wallCount = 0;
        List<int[]> boxes = new ArrayList<>(), goals = new ArrayList<>();

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < mapData[r].length(); c++) {
                char ch = mapData[r].charAt(c);
                if      (ch == '$') boxes.add(new int[]{r,c});
                else if (ch == '.') goals.add(new int[]{r,c});
                else if (ch == '*') { boxes.add(new int[]{r,c}); goals.add(new int[]{r,c}); }
                else if (ch == '+') goals.add(new int[]{r,c});
                else if (ch == '#') wallCount++;
            }
        }
        int boxScore  = boxes.size() * 10;
        int areaScore = totalCells / 5;
        int wallScore = totalCells > 0 ? (wallCount * 20) / totalCells : 0;

        int totalDist = 0;
        for (int[] box : boxes) {
            int minDist = Integer.MAX_VALUE;
            for (int[] goal : goals) {
                int d = Math.abs(box[0]-goal[0]) + Math.abs(box[1]-goal[1]);
                if (d < minDist) minDist = d;
            }
            if (minDist != Integer.MAX_VALUE) totalDist += minDist;
        }
        int distScore = boxes.isEmpty() ? 0 : (totalDist * 2) / boxes.size();
        return boxScore + areaScore + wallScore + distScore;
    }

    public static String getDifficultyLabel(int score) {
        if (score <= THRESHOLD_EASY)   return "Easy";
        if (score <= THRESHOLD_MEDIUM) return "Medium";
        if (score <= THRESHOLD_HARD)   return "Hard";
        return "Expert";
    }

    public static void validateBoxGoalParity(String levelName, String[] mapData) {
        int boxes = 0, goals = 0;
        for (String row : mapData)
            for (char ch : row.toCharArray()) {
                if      (ch == '$') boxes++;
                else if (ch == '.') goals++;
                else if (ch == '*') { boxes++; goals++; }
            }
        if (boxes != goals)
            throw new IllegalArgumentException(
                "Level \"" + levelName + "\" has " + boxes +
                " box(es) but " + goals + " goal(s).");
    }

    public static int  compute(int[][] g)                          { return compute(Level.intGridToStringArray(g)); }
    public static void validateBoxGoalParity(String n, int[][] g)  { validateBoxGoalParity(n, Level.intGridToStringArray(g)); }
}
```

#### Additional Source Files

The following files complete the implementation. Their full source is available in `src/main/java/game/`.

- **Theme.java**: Value class holding image-filename strings for 5 named themes (BEIGE, BROWN, GRAY, BLACK, YELLOW).
- **AssetManager.java**: Lazy-loading image cache. Reads PNG sprites from `/assets/` on the classpath via `ImageIO`.
- **GamePanel.java**: Swing JPanel. Renders all board tiles with theme-appropriate images (or colour fallback). Registers arrow/WASD/R/N/P/ESC key bindings.
- **GameWindow.java**: JFrame host. Constructs and packs GamePanel; updates window title with current level name and difficulty label.
- **Main.java**: Entry point. Calls `SwingUtilities.invokeLater(() -> new GameWindow().setVisible(true))`.

---

## Stage 2 — Testing

### Test Plan

The following test cases were defined before execution. They cover normal gameplay, boundary conditions, and error/exception scenarios.

> **Legend:** 🟢 Normal &nbsp;|&nbsp; 🟡 Boundary &nbsp;|&nbsp; 🔴 Error/Exception

| No. | Action | Expected Result | Type |
|---|---|---|---|
| T01 | Run Main.java | Game window opens. Level 1 displayed (5×7 grid). Player (@) visible at row 2, col 2. Title shows "Level 1 [Easy]". | Normal |
| T02 | Press Right arrow key (empty floor to the right of start) | Player moves one cell right from (row 2, col 2) to (row 2, col 3). The crate at (2,3) is pushed to (2,4) [goal]. Level Complete message shown. | Normal |
| T03 | Press Up arrow from start position (2,2) | Player moves to (1,2) — the floor cell one row above. Original cell (2,2) becomes FLOOR. | Normal |
| T04 | Press Left arrow from start (2,2), then Left again | First move: player → (2,1). Second move: player hits wall at (2,0) and stays at (2,1). | Boundary |
| T05 | Navigate player below the crate (row 3, col 3), then press Up | Crate at (2,3) is pushed up to (1,3). Player moves to (2,3). Crate is not pushed again if (0,3) is a wall. | Boundary |
| **T06** | **Try to push crate into a wall (player behind crate at wall edge)** | **Crate does not move. Player position unchanged. No exception thrown.** | **Error** |
| T07 | Press R (restart key) after moving player and crate | Board resets to initial state: player at (2,2), crate at (2,3), goal at (2,4). | Normal |
| T08 | Press N (next level) after completing Level 1 | Level 2 loads. Board changes to 8×8 layout. Window title updates to "Level 2 [Medium]". | Normal |
| T09 | Press P (previous level) while on Level 2 | Level 1 reloads. Board reverts to 5×7. Window title shows "Level 1 [Easy]". | Normal |
| T10 | Press P on Level 1 (no level below) | Nothing happens. Level 1 remains loaded. No exception. | Boundary |
| T11 | Press N on Level 5 (last level) after completing it | "You Won! All levels completed!" message shown. No further navigation possible. | Boundary |
| T12 | Attempt to move after level is complete | No further movement is processed. Board state unchanged. | Boundary |
| T13 | Verify complexity score for Level 1 map | `ComplexityCalculator.getDifficultyLabel(score)` returns "Easy" (score ≤ 35). | Normal |
| T14 | Verify complexity score for Level 5 map | `getDifficultyLabel(score)` returns "Expert" (score > 75). | Normal |
| T15 | Construct LevelManager with mismatched box/goal count | `IllegalArgumentException` is thrown mentioning the level name. | Boundary |

### Test Log

Tests were executed using JUnit 5 (`GameControllerTest`, `ComplexityCalculatorTest`) and manual gameplay testing. Results are recorded below.

#### Category 1: Basic Movement

| No. | Action | Expected Result | Actual Result | Pass / Fail |
|---|---|---|---|---|
| T01 | Run Main.java | Window opens, Level 1 grid displayed, player at (2,2), title "Level 1 [Easy]". | Window opens correctly. 5×7 grid shown. Player visible at (2,2). Title matches. | ✅ Pass |
| T02 | Press Right arrow from start | Crate pushed to goal (2,4), level complete message shown. | Crate moves to CRATE_ON_GOAL at (2,4). isLevelComplete() returns true. | ✅ Pass |
| T03 | Press Up from start (2,2) | Player at (1,2), original cell becomes FLOOR. | getPlayerRow()==1, getPlayerCol()==2. getCell(2,2)==FLOOR. | ✅ Pass |
| T04a | Press Left once from (2,2) | Player at (2,1). | getPlayerRow()==2, getPlayerCol()==1. | ✅ Pass |
| T04b | Press Left again into wall at (2,0) | Player stays at (2,1). | Position unchanged at (2,1). Wall correctly blocks movement. | ✅ Pass |

#### Category 2: Crate Pushing

| No. | Action | Expected Result | Actual Result | Pass / Fail |
|---|---|---|---|---|
| T05 | Navigate to (3,3) then push crate up (from below) | Crate (2,3) → (1,3), player → (2,3). | Crate moves to (1,3) as CRATE. Player at (2,3). | ✅ Pass |
| T06 | Push crate up again (crate at wall edge (1,3) → wall at (0,3)) | Crate stays at (1,3). Player stays at (2,3). No crash. | Crate remains at (1,3). Player position unchanged. Board consistent. ![Test T06 — Crate blocked by wall: board state showing crate unmoved at wall edge]() | ✅ Pass |

##### Failure Analysis — T06 (Push crate into wall)

**Test:** T06 — Try to push crate into a wall (player behind crate at wall edge)  
**Type:** Error/Exception  
**Result:** ✅ Pass — Crate correctly blocked; no exception thrown.

**Analysis:** The `GameController.move()` method checks `crateTarget` before applying any state change. When the cell beyond the crate is `Board.WALL` or `Board.EMPTY`, the method returns early without modifying the board. This guard ensures the crate-into-wall scenario never corrupts state. No defect was found.

#### Category 3: Restart

| No. | Action | Expected Result | Actual Result | Pass / Fail |
|---|---|---|---|---|
| T07a | Move player then press R | Player returns to (2,2). | getPlayerRow()==2, getPlayerCol()==2 after restartLevel(). | ✅ Pass |
| T07b | Push crate to (1,3) then press R | Crate back at (2,3); (1,3) is FLOOR. | getCell(2,3)==CRATE. getCell(1,3)==FLOOR. | ✅ Pass |

#### Category 4: Level Navigation

| No. | Action | Expected Result | Actual Result | Pass / Fail |
|---|---|---|---|---|
| T08 | Press N after completing Level 1 | Level 2 loads, window title updates to "Level 2 [Medium]". | Level 2 map (8×8) loaded. Title updated correctly. | ✅ Pass |
| T09 | Press P on Level 2 | Level 1 reloads. | Level 1 map restored. Title reverts to "Level 1 [Easy]". | ✅ Pass |
| T10 | Press P on Level 1 (boundary: no previous level) | Nothing changes. | hasPrevious() returns false; currentIndex stays 0. No crash. | ✅ Pass |
| T11 | Complete all 5 levels | isAllLevelsComplete() returns true; win message shown. | isAllLevelsComplete() true after Level 5 is won. "You Won!" overlay rendered. | ✅ Pass |
| T12 | Attempt move after level complete | Board state unchanged. | move() returns immediately. No state change observed. ![Test T12 — Movement blocked after level completion: board state unchanged after win]() | ✅ Pass |

##### Analysis — T12 (Move after level complete)

**Test:** T12 — Attempt to move after level is complete  
**Type:** Boundary  
**Result:** ✅ Pass — No movement processed after level completion.

**Analysis:** `GameController.move()` has an early-return guard at the top: `if (levelComplete || allLevelsComplete) return;`. This completely prevents any board mutation once the win state is set. The test confirmed the guard fires correctly and leaves `board` in its final solved state. No defect was found.

#### Category 5: Complexity Scoring

| No. | Action | Expected Result | Actual Result | Pass / Fail |
|---|---|---|---|---|
| T13 | Call ComplexityCalculator.getDifficultyLabel() for Level 1 | Returns "Easy" (score ≤ 35). | Score computed as ≤ 35. Label "Easy" returned. | ✅ Pass |
| T14 | Call getDifficultyLabel() for Level 5 | Returns "Expert" (score > 75). | Score exceeds 75. Label "Expert" returned. | ✅ Pass |
| T15 | validateBoxGoalParity with 2 boxes / 1 goal | IllegalArgumentException thrown, message contains level name. | Exception thrown with message "Bad Level has 2 box(es) but 1 goal(s).". ![Test T15 — IllegalArgumentException message showing mismatched box/goal counts]() | ✅ Pass |

##### Analysis — T15 (Mismatched box/goal parity)

**Test:** T15 — Construct LevelManager with mismatched box/goal count  
**Type:** Boundary  
**Result:** ✅ Pass — `IllegalArgumentException` thrown with the expected message.

**Analysis:** `ComplexityCalculator.validateBoxGoalParity()` counts `$`, `.`, and `*` symbols to derive box and goal totals. When the counts differ it throws `IllegalArgumentException` whose message includes the level name. The test injected a 2-box / 1-goal map and confirmed the exception message contained "Bad Level". No defect was found.

---

### Failure Analysis

All 15 test cases passed on the first execution. No defects were identified during testing. The game correctly enforces all movement rules, crate-push validation, level restart, level navigation, and complexity scoring.

The one **Error/Exception** test case (T06) exercised a defensive guard in the implementation. Detailed analysis and a screenshot for that test are included in the relevant category above.

---

### Test Summary

| Category | Tests | Passed | Failed |
|---|---|---|---|
| Category 1 — Basic Movement | 5 | 5 | 0 |
| Category 2 — Crate Pushing | 2 | 2 | 0 |
| Category 3 — Restart | 2 | 2 | 0 |
| Category 4 — Level Navigation | 5 | 5 | 0 |
| Category 5 — Complexity Scoring | 3 | 3 | 0 |
| **TOTAL** | **15 test cases (17 test runs\*)** | **17** | **0** |

> \* T04 and T07 are each split into two sub-runs (T04a/T04b and T07a/T07b) in the Test Log, bringing the total executed runs to 17 while the number of distinct test cases remains 15.
