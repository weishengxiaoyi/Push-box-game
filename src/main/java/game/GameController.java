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

        int pr = board.getPlayerRow();
        int pc = board.getPlayerCol();
        int nr = pr + dr;
        int nc = pc + dc;

        int targetCell = board.getCell(nr, nc);

        if (targetCell == Board.EMPTY || targetCell == Board.WALL) return;

        if (targetCell == Board.CRATE || targetCell == Board.CRATE_ON_GOAL) {
            int cr = nr + dr;
            int cc = nc + dc;
            int crateTarget = board.getCell(cr, cc);
            if (crateTarget != Board.FLOOR && crateTarget != Board.GOAL) return;

            // Move crate
            board.setCell(cr, cc, (crateTarget == Board.GOAL) ? Board.CRATE_ON_GOAL : Board.CRATE);
            board.setCell(nr, nc, (targetCell == Board.CRATE_ON_GOAL) ? Board.GOAL : Board.FLOOR);
        }

        // Move player
        int currentCell = board.getCell(pr, pc);
        board.setCell(pr, pc, (currentCell == Board.PLAYER_ON_GOAL) ? Board.GOAL : Board.FLOOR);
        board.setCell(nr, nc, (board.getCell(nr, nc) == Board.GOAL) ? Board.PLAYER_ON_GOAL : Board.PLAYER);
        board.setPlayerRow(nr);
        board.setPlayerCol(nc);

        if (board.isWon()) {
            levelComplete = true;
            if (levelManager.isLastLevel()) {
                allLevelsComplete = true;
            }
        }
    }

    public void restartLevel() {
        board = initialBoard.copy();
        levelComplete = false;
        allLevelsComplete = false;
    }

    public void nextLevel() {
        if (levelManager.hasNext()) {
            levelManager.nextLevel();
            loadCurrentLevel();
        }
    }

    public void previousLevel() {
        if (levelManager.hasPrevious()) {
            levelManager.previousLevel();
            loadCurrentLevel();
        }
    }

    public Board getBoard() { return board; }
    public Level getCurrentLevel() { return levelManager.getCurrentLevel(); }
    public int getCurrentLevelIndex() { return levelManager.getCurrentIndex(); }
    public int getTotalLevels() { return levelManager.getTotalLevels(); }
    public boolean isLevelComplete() { return levelComplete; }
    public boolean isAllLevelsComplete() { return allLevelsComplete; }
}
