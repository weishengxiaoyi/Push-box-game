package game;

public class Level {
    public final String name;
    public final String[] mapData;
    public final Theme theme;
    /** Complexity score computed by {@link ComplexityCalculator#compute}. */
    public final int complexityScore;

    public Level(String name, String[] mapData, Theme theme) {
        this.name = name;
        this.mapData = mapData;
        this.theme = theme;
        this.complexityScore = ComplexityCalculator.compute(mapData);
    }

    /**
     * Constructs a Level from an {@code int[][]} grid using
     * {@link Board} tile constants (WALL=2, FLOOR=1, GOAL=3, CRATE=4, PLAYER=6, etc.).
     * The grid is converted to a {@code String[]} character map internally so that
     * all existing logic (rendering, complexity, parity) continues to work unchanged.
     */
    public Level(String name, int[][] gridData, Theme theme) {
        this(name, intGridToStringArray(gridData), theme);
    }

    /**
     * Converts an {@code int[][]} grid (using Board tile constants) to a
     * {@code String[]} character map compatible with {@link Board#fromLevel}.
     */
    static String[] intGridToStringArray(int[][] gridData) {
        String[] mapData = new String[gridData.length];
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
                    default:                   sb.append(' '); break; // FLOOR / EMPTY
                }
            }
            mapData[r] = sb.toString();
        }
        return mapData;
    }
}
