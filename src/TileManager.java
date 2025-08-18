import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;

public class TileManager {
    GamePainel gp;
    BufferedImage mapImage;
    BufferedImage scaledMapImage;

    public TileManager(GamePainel gp) {
        this.gp = gp;
        loadMapImage();
    }

    public void loadMapImage() {
        try {
            // Tenta carregar como recurso primeiro (para JAR)
            InputStream imageStream = getClass().getResourceAsStream("/resources/Cenarios/worldmap.png");
            if (imageStream != null) {
                System.out.println("Carregando mapa como recurso do JAR...");
                mapImage = ImageIO.read(imageStream);
                imageStream.close();
            } else {
                // Fallback para arquivo externo (para desenvolvimento)
                mapImage = ImageIO.read(new File("resources/Cenarios/worldmap.png"));
            }

            updateScaledImage(); // Cria a versão redimensionada
            System.out.println("Imagem do mapa carregada com sucesso!");
        } catch (IOException e) {
            System.out.println("Erro ao carregar a imagem do mapa: " + e.getMessage());
            e.printStackTrace();
            // Cria uma imagem em branco se não conseguir carregar
            mapImage = new BufferedImage(gp.screenWidth, gp.screenHeight, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2 = mapImage.createGraphics();
            g2.setColor(new java.awt.Color(0, 100, 0)); // Fundo verde
            g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);
            g2.dispose();
            scaledMapImage = mapImage; // Usa a mesma imagem como versão escalada
        }
    }

    // Atualiza a imagem dimensionada quando a resolução muda
    public void updateScaledImage() {
        if (mapImage != null) {
            // Cria uma versão escalada da imagem do mapa
            scaledMapImage = new BufferedImage(gp.screenWidth, gp.screenHeight, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = scaledMapImage.createGraphics();

            // Ativa a suavização para melhor qualidade
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Desenha a imagem original redimensionada para o tamanho da tela
            g2.drawImage(mapImage, 0, 0, gp.screenWidth, gp.screenHeight, null);
            g2.dispose();
        }
    }

    public void draw(Graphics2D g2) {
        // Desenha a imagem do mapa como um todo
        g2.drawImage(scaledMapImage, 0, 0, null);
    }
}