# Sokoban - Push Box Game

A classic Sokoban puzzle game implemented in Java with Swing, featuring themed levels and smooth controls.

## Prerequisites
- Java 11 or higher
- No additional dependencies required

## Build & Run

Build the project:
```bash
./gradlew build
```

Run the game:
```bash
./gradlew run
```

Or run the fat JAR directly:
```bash
java -jar build/libs/sokoban-1.0.jar
```

## Controls

| Key        | Action                |
|------------|-----------------------|
| Arrow Keys | Move player           |
| R          | Restart current level |
| N          | Next level            |
| P          | Previous level        |
| ESC        | Quit                  |

## Levels

| Level | Name          | Theme  | Boxes |
|-------|---------------|--------|-------|
| 1     | Tutorial      | Beige  | 1     |
| 2     | Two Boxes     | Brown  | 2     |
| 3     | Three Boxes   | Gray   | 3     |
| 4     | Four Boxes    | Black  | 4     |
| 5     | Five Boxes    | Yellow | 5     |

## Objective

Push all crates (brown boxes) onto the goal squares (marked spots on the floor). When a crate is on a goal, it changes appearance. Complete all 5 levels to win!

## Asset Credits

All game assets (wall, crate, ground, player sprites) are from the repository root directory.
