package game;

public class Theme {
    public final String wallImage;
    public final String crateImage;
    public final String crateDarkImage;
    public final String endPointImage;
    public final String groundImage;
    public final String groundGravelImage;

    public Theme(String wallImage, String crateImage, String crateDarkImage,
                 String endPointImage, String groundImage, String groundGravelImage) {
        this.wallImage = wallImage;
        this.crateImage = crateImage;
        this.crateDarkImage = crateDarkImage;
        this.endPointImage = endPointImage;
        this.groundImage = groundImage;
        this.groundGravelImage = groundGravelImage;
    }

    public static final Theme BEIGE = new Theme(
        "Wall_Beige.png", "Crate_Beige.png", "CrateDark_Beige.png",
        "EndPoint_Beige.png", "Ground_Concrete.png", "GroundGravel_Concrete.png"
    );
    public static final Theme BROWN = new Theme(
        "Wall_Brown.png", "Crate_Brown.png", "CrateDark_Brown.png",
        "EndPoint_Brown.png", "Ground_Dirt.png", "GroundGravel_Dirt.png"
    );
    public static final Theme GRAY = new Theme(
        "Wall_Gray.png", "Crate_Gray.png", "CrateDark_Gray.png",
        "EndPoint_Gray.png", "Ground_Grass.png", "GroundGravel_Grass.png"
    );
    public static final Theme BLACK = new Theme(
        "Wall_Black.png", "Crate_Black.png", "CrateDark_Black.png",
        "EndPoint_Black.png", "Ground_Sand.png", "GroundGravel_Sand.png"
    );
    public static final Theme YELLOW = new Theme(
        "Wall_Gray.png", "Crate_Yellow.png", "CrateDark_Yellow.png",
        "EndPoint_Yellow.png", "GroundGravel_Concrete.png", "GroundGravel_Concrete.png"
    );
}
