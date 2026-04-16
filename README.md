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
│       │       ├── Main.java          # Entry point
│       │       ├── GameWindow.java
│       │       ├── GamePanel.java
│       │       ├── GameController.java
│       │       ├── Board.java
│       │       ├── Level.java
│       │       ├── LevelManager.java
│       │       ├── Theme.java
│       │       └── AssetManager.java  # Loads images from classpath
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

| Level | Name          | Theme  | Boxes |
|-------|---------------|--------|-------|
| 1     | Tutorial      | Beige  | 1     |
| 2     | Two Boxes     | Brown  | 2     |
| 3     | Three Boxes   | Gray   | 3     |
| 4     | Four Boxes    | Black  | 4     |
| 5     | Five Boxes    | Yellow | 5     |

## Objective

Push all crates onto the goal squares (marked spots on the floor). When a crate reaches a goal it changes appearance. Complete all 5 levels to win!

## Resource Loading

All image assets live under `src/main/resources/assets/` and are loaded at runtime
via the classpath using `AssetManager.getResourceAsStream("/assets/<filename>")`.
This means the assets are bundled inside the JAR automatically — no external paths
or manual copying required.
