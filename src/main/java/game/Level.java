package game;

public class Level {
    public final String name;
    public final String[] mapData;
    public final Theme theme;

    public Level(String name, String[] mapData, Theme theme) {
        this.name = name;
        this.mapData = mapData;
        this.theme = theme;
    }
}
