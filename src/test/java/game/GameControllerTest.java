package game;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for movement logic in GameController.
 *
 * Level 1 layout (the default level loaded by GameController):
 *
 *   col:  0123456
 *   row 0: #######
 *   row 1: #     #
 *   row 2: #  $  #   crate at (2,3)
 *   row 3: ## .  #   goal  at (3,3)
 *   row 4:  #    #
 *   row 5:  #  @ #   player starts at (5,4)
 *   row 6:  ######
 */
class GameControllerTest {

    private GameController controller;

    @BeforeEach
    void setUp() {
        controller = new GameController();
    }

    // ------------------------------------------------------------------
    // Basic position checks
    // ------------------------------------------------------------------

    @Test
    void playerStartsAtCorrectPosition() {
        Board b = controller.getBoard();
        assertEquals(5, b.getPlayerRow(), "Player should start at row 5");
        assertEquals(4, b.getPlayerCol(), "Player should start at col 4");
        assertEquals(Board.PLAYER, b.getCell(5, 4));
    }

    // ------------------------------------------------------------------
    // Movement into floor
    // ------------------------------------------------------------------

    @Test
    void moveUpIntoFloor() {
        controller.move(-1, 0); // (5,4) → (4,4) which is floor in row 4 " #    #"
        Board b = controller.getBoard();
        assertEquals(4, b.getPlayerRow());
        assertEquals(4, b.getPlayerCol());
        assertEquals(Board.PLAYER, b.getCell(4, 4));
        assertEquals(Board.FLOOR, b.getCell(5, 4), "Original cell should become floor");
    }

    @Test
    void moveRightIntoFloor() {
        controller.move(0, 1); // (5,4) → (5,5) which is floor in " #  @ #"
        Board b = controller.getBoard();
        assertEquals(5, b.getPlayerRow());
        assertEquals(5, b.getPlayerCol());
    }

    // ------------------------------------------------------------------
    // Blocked by wall
    // ------------------------------------------------------------------

    @Test
    void moveDownBlockedByWall() {
        // Row 6 col 4 is '#' in " ######" → move blocked
        controller.move(1, 0);
        Board b = controller.getBoard();
        assertEquals(5, b.getPlayerRow(), "Player should not move into wall below");
        assertEquals(4, b.getPlayerCol());
    }

    @Test
    void moveLeftBlockedByWall() {
        // Move left twice: (5,4)→(5,3)→(5,2). A third left hits '#' at (5,1).
        controller.move(0, -1);
        controller.move(0, -1);
        controller.move(0, -1); // (5,2) → wall at (5,1)
        Board b = controller.getBoard();
        assertEquals(5, b.getPlayerRow());
        assertEquals(2, b.getPlayerCol(), "Player should be stopped by wall at col 1");
    }

    // ------------------------------------------------------------------
    // Crate pushing
    // ------------------------------------------------------------------

    @Test
    void pushCrateToGoal() {
        // Navigate player to (1,3) then push the crate at (2,3) down onto the goal (3,3).
        // Path from (5,4):
        //   up 4 times  → (1,4)
        //   left 1 time → (1,3)
        //   down 1 time → push crate (2,3) onto goal (3,3), player moves to (2,3)
        controller.move(-1, 0); // (5,4)→(4,4)
        controller.move(-1, 0); // (4,4)→(3,4)
        controller.move(-1, 0); // (3,4)→(2,4)
        controller.move(-1, 0); // (2,4)→(1,4)
        controller.move(0, -1); // (1,4)→(1,3)
        controller.move(1, 0);  // push crate from (2,3) to (3,3)

        Board b = controller.getBoard();
        assertEquals(2, b.getPlayerRow(), "Player should be at (2,3)");
        assertEquals(3, b.getPlayerCol());
        assertEquals(Board.PLAYER, b.getCell(2, 3));
        assertEquals(Board.CRATE_ON_GOAL, b.getCell(3, 3), "Crate should be on the goal");
        assertTrue(controller.isLevelComplete(), "Level should be complete");
    }

    @Test
    void cannotPushCrateIntoWall() {
        // Push crate at (2,3) leftward: player needs to be at (2,4) facing left.
        // Crate target would be (2,2) which is floor — that push is fine, so instead
        // push the crate further left until it would hit wall at (2,0)/'#'.
        // (2,3)→(2,2): fine; (2,2)→(2,1): fine; (2,1)→(2,0): wall, blocked.
        // Navigate: from (5,4) go up 3 times to (2,4), then push left 3 times.
        controller.move(-1, 0); // (5,4)→(4,4)
        controller.move(-1, 0); // (4,4)→(3,4)
        controller.move(-1, 0); // (3,4)→(2,4) — player right of crate
        controller.move(0, -1); // push crate (2,3)→(2,2), player→(2,3)
        controller.move(0, -1); // push crate (2,2)→(2,1), player→(2,2)
        controller.move(0, -1); // try push crate (2,1)→(2,0) which is '#' → blocked

        Board b = controller.getBoard();
        assertEquals(2, b.getPlayerRow());
        assertEquals(2, b.getPlayerCol(), "Player should be blocked when crate hits wall");
        assertEquals(Board.CRATE, b.getCell(2, 1), "Crate should remain at (2,1)");
    }

    // ------------------------------------------------------------------
    // Restart
    // ------------------------------------------------------------------

    @Test
    void restartResetsPlayerPosition() {
        controller.move(-1, 0);
        controller.move(-1, 0);
        controller.restartLevel();

        Board b = controller.getBoard();
        assertEquals(5, b.getPlayerRow(), "Row should reset after restart");
        assertEquals(4, b.getPlayerCol(), "Col should reset after restart");
    }

    @Test
    void restartResetsCratePosition() {
        // Move player next to crate and push it
        controller.move(-1, 0);
        controller.move(-1, 0);
        controller.move(-1, 0); // player at (2,4)
        controller.move(0, -1); // push crate from (2,3) to (2,2)

        controller.restartLevel();

        Board b = controller.getBoard();
        assertEquals(Board.CRATE, b.getCell(2, 3), "Crate should be back at original position");
        assertEquals(Board.FLOOR, b.getCell(2, 2), "Pushed position should be floor again");
    }
}
