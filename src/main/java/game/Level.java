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
}
