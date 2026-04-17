# Sokoban - Push Box Game

A classic Sokoban puzzle game implemented in Java with Swing, featuring themed levels and smooth controls.

## Project Structure

```
Push-box-game/
├── build.gradle              # Gradle build file (Java + Application plugin)
├── settings.gradle           # Project name: sokoban
├── gradlew / gradlew.bat     # Gradle wrapper scripts
├── src/
│   └── main/
│       ├── java/
│       │   └── game/         # All Java source files
│       │       ├── Main.java                 # Entry point
│       │       ├── GameWindow.java
│       │       ├── GamePanel.java
│       │       ├── GameController.java
│       │       ├── Board.java
│       │       ├── Level.java
│       │       ├── LevelManager.java
│       │       ├── ComplexityCalculator.java # Difficulty scoring & validation
│       │       ├── Theme.java
│       │       └── AssetManager.java         # Loads images from classpath
│       └── resources/
│           └── assets/       # All image assets (PNG sprites)
└── docs/                     # Screenshots and assignment documents
```

## Prerequisites

- **JDK 11 or higher** (JDK 17 or 21 also work)
- **Gradle 8.5+** (included via the Gradle wrapper — no separate install needed)

## Open in IntelliJ IDEA

### Option A — Import as Gradle project (recommended)

1. Launch IntelliJ IDEA.
2. On the Welcome screen choose **Open** (or **File → Open…**).
3. Select the repository root folder (`Push-box-game/`) and click **OK**.
4. IntelliJ detects `build.gradle` automatically.  
   If prompted, choose **"Open as Gradle Project"**.
5. Wait for the Gradle sync to finish (bottom status bar).
6. Open **`src/main/java/game/Main.java`**, right-click in the editor, and choose  
   **Run 'Main.main()'**.

> **Tip:** If IntelliJ does not detect the Gradle project automatically, open the
> **Gradle** tool window (*View → Tool Windows → Gradle*) and click **Reload All
> Gradle Projects**.

### Option B — Run from Gradle tool window

After importing (step 1-5 above):

1. Open **View → Tool Windows → Gradle**.
2. Expand **sokoban → Tasks → application**.
3. Double-click **run**.

### Option C — Create a Run Configuration manually

If you prefer a dedicated run configuration:

1. **Run → Edit Configurations… → + → Application**.
2. Set **Main class** to `game.Main`.
3. Set **Module** (or **Classpath of module**) to `sokoban.main`.
4. Click **OK**, then press the green **Run** button.

## Build & Run from the Command Line

Build the project:
```bash
./gradlew build
```

Run the game:
```bash
./gradlew run
```

Build and run the self-contained fat JAR:
```bash
./gradlew jar
java -jar build/libs/sokoban-1.0.jar
```

## Controls

| Key                  | Action                |
|----------------------|-----------------------|
| Arrow Keys / W A S D | Move player           |
| R                    | Restart current level |
| N                    | Next level            |
| P                    | Previous level        |
| ESC                  | Quit                  |

## Levels

Difficulty is measured by a **complexity score** computed from structural features of
each map (box count, map area, wall density, and average box-to-goal distance).
This replaces the earlier "minimum steps" metric, which did not correlate reliably
with actual solving difficulty.

| Level | Name         | Theme  | Boxes | Goals | Complexity Score | Difficulty | Notes                                    |
|-------|--------------|--------|-------|-------|-----------------|------------|------------------------------------------|
| 1     | Introduction | Beige  | 1     | 1     | ~30             | Easy       | Single collinear push                    |
| 2     | Deadlock     | Brown  | 1     | 1     | ~36             | Medium     | Interior walls force careful positioning |
| 3     | Two Paths    | Gray   | 2     | 2     | ~54             | Medium     | Stacked goals on right; 2 push-sequence solutions |
| 4     | Goal Room    | Black  | 3     | 3     | ~70             | Hard       | Clustered goal room; unique push-order solution   |
| 5     | Multi-Room   | Yellow | 4     | 4     | ~79             | Expert     | Multi-room bottleneck; unique push-order solution |

The complexity score and difficulty label are displayed live in the HUD at the top of
the game window.

### Complexity score formula

```
score = (boxCount × 10)         – primary driver
      + (mapArea  /  5)         – larger playfield needs more navigation
      + (wallDensity × 20)      – denser walls = tighter corridors
      + (avgMinManhattan × 2)   – farther boxes from goals = harder to place
```

Thresholds: ≤ 35 → **Easy** | ≤ 55 → **Medium** | ≤ 75 → **Hard** | > 75 → **Expert**

### Level design notes

These five levels use progressively harder Sokoban mechanics:

- **Level 1** — teaches the basic "push, not pull" rule; one move wins.
- **Level 2** — interior walls introduce the concept of push-angle positioning;
  moving a box into a corner deadlocks it permanently.
- **Level 3** — two boxes with stacked goals on the right; either box can be
  pushed first, giving exactly two push-sequence solutions.
- **Level 4** — three boxes must fill a clustered goal room via a narrow entrance;
  the deepest slot must be filled first — unique push-sequence solution.
- **Level 5** — four boxes scattered across left rooms must pass through a narrow
  bottleneck corridor to reach a clustered right goal area; unique push-sequence
  solution with high deadlock risk if order is wrong.

## Objective

Push all crates onto the goal squares (marked spots on the floor). When a crate reaches a goal it changes appearance. Complete all 5 levels to win!

## Resource Loading

All image assets live under `src/main/resources/assets/` and are loaded at runtime
via the classpath using `AssetManager.getResourceAsStream("/assets/<filename>")`.
This means the assets are bundled inside the JAR automatically — no external paths
or manual copying required.
