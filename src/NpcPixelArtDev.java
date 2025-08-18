import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;

public class NpcPixelArtDev {
    private int x, y, size;
    private BufferedImage image;
    private boolean visible = false;

    public NpcPixelArtDev(int x, int y, int size) {
        this.x = x;
        this.y = y;
        this.size = size;
        try {
            // Tenta carregar como recurso primeiro (para JAR)
            InputStream imageStream = getClass().getResourceAsStream("/resources/Npcs/Rimuru.png");
            if (imageStream != null) {
                image = ImageIO.read(imageStream);
                imageStream.close();
            } else {
                // Fallback para arquivo externo (para desenvolvimento)
                image = ImageIO.read(new File("resources/Npcs/Rimuru.png"));
            }
        } catch (IOException e) {
            image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = image.createGraphics();
            g2.setColor(Color.MAGENTA);
            g2.fillRect(0, 0, size, size);
            g2.dispose();
        }
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public boolean isVisible() {
        return visible;
    }

    public void draw(Graphics2D g2) {
        if (visible && image != null) {
            g2.drawImage(image, x, y, size, size, null);
        }
    }

    public Rectangle getHitbox() {
        int hitboxSize = size / 2; // Metade do tamanho visual do NPC
        int hitboxX = x + (size - hitboxSize) / 2;
        int hitboxY = y + (size - hitboxSize) / 2;
        return new Rectangle(hitboxX, hitboxY, hitboxSize, hitboxSize);
    }
}