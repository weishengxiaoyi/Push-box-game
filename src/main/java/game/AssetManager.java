package game;

import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;

public class AssetManager {
    private static final Map<String, BufferedImage> cache = new HashMap<>();

    public static BufferedImage getImage(String filename) {
        if (cache.containsKey(filename)) {
            return cache.get(filename);
        }
        try (InputStream is = AssetManager.class.getResourceAsStream("/assets/" + filename)) {
            if (is == null) return null;
            BufferedImage img = ImageIO.read(is);
            cache.put(filename, img);
            return img;
        } catch (Exception e) {
            return null;
        }
    }
}
