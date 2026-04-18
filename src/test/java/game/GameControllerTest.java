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
 *   row 2: # @$. #   player at (2,2), crate at (2,3), goal at (2,4)
 *   row 3: #     #
 *   row 4: #######
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
        assertEquals(2, b.getPlayerRow(), "Player should start at row 2");
        assertEquals(2, b.getPlayerCol(), "Player should start at col 2");
        assertEquals(Board.PLAYER, b.getCell(2, 2));
    }

    // ------------------------------------------------------------------
    // Movement into floor
    // ------------------------------------------------------------------

    @Test
    void moveUpIntoFloor() {
        controller.move(-1, 0); // (2,2) → (1,2) which is floor in "#     #"
        Board b = controller.getBoard();
        assertEquals(1, b.getPlayerRow());
        assertEquals(2, b.getPlayerCol());
        assertEquals(Board.PLAYER, b.getCell(1, 2));
        assertEquals(Board.FLOOR, b.getCell(2, 2), "Original cell should become floor");
    }

    @Test
    void moveLeftIntoFloor() {
        controller.move(0, -1); // (2,2) → (2,1) which is floor in "# @$. #"
        Board b = controller.getBoard();
        assertEquals(2, b.getPlayerRow());
        assertEquals(1, b.getPlayerCol());
    }

    // ------------------------------------------------------------------
    // Blocked by wall
    // ------------------------------------------------------------------

    @Test
    void moveDownBlockedByWall() {
        // Move down to (3,2), then a second down hits '#' at (4,2)
        controller.move(1, 0); // (2,2) → (3,2)
        controller.move(1, 0); // (3,2) → wall at (4,2) → blocked
        Board b = controller.getBoard();
        assertEquals(3, b.getPlayerRow(), "Player should not move into wall below");
        assertEquals(2, b.getPlayerCol());
    }

    @Test
    void moveLeftBlockedByWall() {
        // Move left once to (2,1), second left hits '#' at (2,0)
        controller.move(0, -1); // (2,2) → (2,1)
        controller.move(0, -1); // (2,1) → wall at (2,0) → blocked
        Board b = controller.getBoard();
        assertEquals(2, b.getPlayerRow());
        assertEquals(1, b.getPlayerCol(), "Player should be stopped by wall at col 0");
    }

    // ------------------------------------------------------------------
    // Crate pushing
    // ------------------------------------------------------------------

    @Test
    void pushCrateToGoal() {
        // From (2,2), push right: crate (2,3) → (2,4) [goal], player → (2,3)
        controller.move(0, 1);

        Board b = controller.getBoard();
        assertEquals(2, b.getPlayerRow(), "Player should be at (2,3)");
        assertEquals(3, b.getPlayerCol());
        assertEquals(Board.PLAYER, b.getCell(2, 3));
        assertEquals(Board.CRATE_ON_GOAL, b.getCell(2, 4), "Crate should be on the goal");
        assertTrue(controller.isLevelComplete(), "Level should be complete");
    }

    @Test
    void cannotPushCrateIntoWall() {
        // Navigate to (3,3) so the crate can be pushed upward.
        // Path from (2,2): down to (3,2), right to (3,3).
        controller.move(1, 0);  // (2,2) → (3,2)
        controller.move(0, 1);  // (3,2) → (3,3)
        // Push crate up: player at (3,3) moves up → crate (2,3) → (1,3), player → (2,3)
        controller.move(-1, 0);
        // Try push crate up again: crate (1,3) → (0,3) which is '#' → blocked
        controller.move(-1, 0);

        Board b = controller.getBoard();
        assertEquals(2, b.getPlayerRow());
        assertEquals(3, b.getPlayerCol(), "Player should be blocked when crate hits wall");
        assertEquals(Board.CRATE, b.getCell(1, 3), "Crate should remain at (1,3)");
    }

    @Test
    void testPushBoxIntoWall() {
        // Navigate above the crate: (2,2) → (1,2) → (1,3)
        controller.move(-1, 0); // (2,2) → (1,2)
        controller.move(0, 1);  // (1,2) → (1,3)
        // Push crate down: player at (1,3) moves down → crate (2,3) → (3,3), player → (2,3)
        controller.move(1, 0);
        // Try push crate down again: crate (3,3) → (4,3) which is '#' → blocked
        controller.move(1, 0);

        Board b = controller.getBoard();
        assertEquals(2, b.getPlayerRow(), "Player should be blocked when crate hits wall");
        assertEquals(3, b.getPlayerCol(), "Player should be blocked when crate hits wall");
        assertEquals(Board.CRATE, b.getCell(3, 3), "Crate should remain at (3,3)");
        assertEquals(Board.WALL, b.getCell(4, 3), "Bottom cell should still be a wall");
    }

    // ------------------------------------------------------------------
    // Restart
    // ------------------------------------------------------------------

    @Test
    void restartResetsPlayerPosition() {
        controller.move(-1, 0);
        controller.move(1, 0);
        controller.restartLevel();

        Board b = controller.getBoard();
        assertEquals(2, b.getPlayerRow(), "Row should reset after restart");
        assertEquals(2, b.getPlayerCol(), "Col should reset after restart");
    }

    @Test
    void restartResetsCratePosition() {
        // Navigate to (3,3) and push crate from (2,3) up to (1,3)
        controller.move(1, 0);  // (2,2) → (3,2)
        controller.move(0, 1);  // (3,2) → (3,3)
        controller.move(-1, 0); // push crate (2,3) → (1,3), player → (2,3)

        controller.restartLevel();

        Board b = controller.getBoard();
        assertEquals(Board.CRATE, b.getCell(2, 3), "Crate should be back at original position");
        assertEquals(Board.FLOOR, b.getCell(1, 3), "Pushed position should be floor again");
    }
}
