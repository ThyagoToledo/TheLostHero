import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;

/**
 * Classe que representa o Boss do jogo.
 * O Boss é um inimigo (Goblin) que fica em uma posição fixa no mapa.
 * Quando o jogador passa por cima, troca a tela.
 */
public class Boss {
    private GamePainel gp;
    private BufferedImage bossImage;
    private int x, y;
    private final int size;
    private Rectangle hitbox;
    private boolean encountered = false;
    private boolean isDead = false;
    private int hp = 80; // Vida inicial do Goblin ajustada para 80
    private static final int MAX_HP = 80; // Vida máxima ajustada para 80
    private boolean inCombat = false; // Novo estado para controlar se está em combate

    /**
     * Construtor da classe Boss.
     * 
     * @param gp Referência para o painel do jogo
     */
    public Boss(GamePainel gp) {
        this.gp = gp;
        this.size = gp.tileSize; // Usa o mesmo tamanho do player (16x16 * 3)
        carregarImagem();
        definirPosicao();

        // Define a hitbox do boss (um pouco menor que a imagem para colisão mais
        // precisa)
        int hitboxOffset = size / 8;
        this.hitbox = new Rectangle(x + hitboxOffset, y + hitboxOffset,
                size - hitboxOffset * 2, size - hitboxOffset * 2);
    }

    /**
     * Carrega a imagem do Boss (Goblin).
     */
    private void carregarImagem() {
        try {
            // Tenta carregar como recurso primeiro (para JAR)
            InputStream imageStream = getClass().getResourceAsStream("/resources/Inimigos/Goblin.png");
            if (imageStream != null) {
                System.out.println("Carregando imagem do Goblin como recurso do JAR...");
                bossImage = ImageIO.read(imageStream);
                imageStream.close();
            } else {
                // Fallback para arquivo externo (para desenvolvimento)
                File imageFile = new File("resources/Inimigos/Goblin.png");
                System.out.println("Procurando imagem do Goblin em: " + imageFile.getAbsolutePath());

                if (imageFile.exists()) {
                    bossImage = ImageIO.read(imageFile);
                } else {
                    System.out.println("Arquivo de imagem do Goblin não encontrado!");
                    bossImage = null;
                }
            }

            if (bossImage != null) {
                System.out.println("Imagem do Goblin carregada com sucesso!");
                System.out.println("Dimensões: " + bossImage.getWidth() + "x" + bossImage.getHeight());
            } else {
                System.out.println("Erro ao ler imagem do Goblin");
                criarImagemFallback();
            }
        } catch (IOException e) {
            System.out.println("Erro ao carregar a imagem do Goblin: " + e.getMessage());
            e.printStackTrace();
            criarImagemFallback();
        }
    }

    /**
     * Cria uma imagem de fallback caso a imagem original não possa ser carregada.
     */
    private void criarImagemFallback() {
        bossImage = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = bossImage.createGraphics();

        // Desenha um goblin simplificado como fallback
        g2.setColor(new Color(0, 150, 0)); // Verde escuro
        g2.fillOval(0, 0, size, size);

        // Adiciona olhos
        g2.setColor(Color.RED);
        g2.fillOval(size / 4, size / 3, size / 6, size / 6);
        g2.fillOval((int) (size / 4 * 2.5), size / 3, size / 6, size / 6);

        // Adiciona boca
        g2.setColor(Color.BLACK);
        g2.drawArc(size / 4, size / 2, size / 2, size / 4, 0, -180);

        g2.dispose();
    }

    /**
     * Define a posição inicial do Boss no canto esquerdo inferior do mapa.
     */
    private void definirPosicao() {
        // Posiciona no canto esquerdo inferior
        this.x = gp.tileSize * 2; // Duas tiles à direita da borda esquerda
        this.y = gp.screenHeight - gp.tileSize * 3; // Três tiles acima da borda inferior

        System.out.println("Goblin posicionado em: " + x + ", " + y);
    }

    /**
     * Atualiza a posição da hitbox do Boss.
     */
    private void atualizarHitbox() {
        int hitboxOffset = size / 8;
        hitbox.x = x + hitboxOffset;
        hitbox.y = y + hitboxOffset;
    }

    /**
     * Atualiza o estado do Boss após o combate por turnos.
     * 
     * @param combatHp    Vida atual do Goblin no combate
     * @param maxCombatHp Vida máxima do Goblin no combate
     * @param wasDefeated true se o Goblin foi derrotado no combate
     */
    public void updateFromCombat(int combatHp, int maxCombatHp, boolean wasDefeated) {
        if (wasDefeated) {
            isDead = true;
            hp = 0;
            inCombat = false;
        } else {
            // Atualiza a vida proporcionalmente
            hp = (int) ((float) combatHp / maxCombatHp * MAX_HP);
            inCombat = false;
        }
    }

    /**
     * Inicia o combate com o Goblin.
     * 
     * @return true se o combate pode ser iniciado, false se o Goblin está morto ou
     *         já em combate
     */
    public boolean startCombat() {
        if (isDead || inCombat) {
            return false;
        }
        inCombat = true;
        return true;
    }

    /**
     * Verifica se o Goblin está em combate.
     * 
     * @return true se está em combate, false caso contrário
     */
    public boolean isInCombat() {
        return inCombat;
    }

    /**
     * Verifica colisão com o jogador e realiza ações se necessário.
     * 
     * @param playerX    Posição X do jogador
     * @param playerY    Posição Y do jogador
     * @param playerSize Tamanho do jogador
     * @return true se houve colisão e ainda não encontrou antes, false caso
     *         contrário
     */
    public boolean verificarColisao(int playerX, int playerY, int playerSize) {
        if (isDead || inCombat)
            return false; // Não inicia combate se estiver morto ou já em combate

        // Cria retângulo para o player (também com offset para hitbox mais precisa)
        int playerOffset = playerSize / 8;
        Rectangle playerRect = new Rectangle(
                playerX + playerOffset,
                playerY + playerOffset,
                playerSize - playerOffset * 2,
                playerSize - playerOffset * 2);

        // Verifica colisão
        if (!encountered && hitbox.intersects(playerRect)) {
            encountered = true;
            inCombat = true; // Marca que entrou em combate
            System.out.println("Jogador encontrou o Goblin!");
            return true;
        }

        return false;
    }

    /**
     * Desenha o Boss na tela.
     * 
     * @param g2 Objeto Graphics2D para desenho
     */
    public void draw(Graphics2D g2) {
        if (isDead)
            return; // Não desenha se estiver morto

        if (bossImage != null) {
            g2.drawImage(bossImage, x, y, size, size, null);

            // Desenha a barra de vida
            int barWidth = size;
            int barHeight = 5;
            int barX = x;
            int barY = y - 10;

            // Fundo da barra
            g2.setColor(Color.RED);
            g2.fillRect(barX, barY, barWidth, barHeight);

            // Vida atual
            g2.setColor(Color.GREEN);
            int currentBarWidth = (int) ((float) hp / MAX_HP * barWidth);
            g2.fillRect(barX, barY, currentBarWidth, barHeight);

            // Borda da barra
            g2.setColor(Color.BLACK);
            g2.drawRect(barX, barY, barWidth, barHeight);
        }
    }

    /**
     * Verifica se o Boss está morto.
     * 
     * @return true se estiver morto, false caso contrário
     */
    public boolean isDead() {
        return isDead;
    }

    /**
     * Reseta o estado do Boss para permitir um novo encontro.
     */
    public void reset() {
        encountered = false;
        isDead = false;
        hp = MAX_HP;
        inCombat = false;
    }

    /**
     * Retorna se já houve encontro com o jogador.
     * 
     * @return true se já encontrou, false caso contrário
     */
    public boolean isEncountered() {
        return encountered;
    }

    /**
     * Retorna a posição X do Boss.
     * 
     * @return Posição X
     */
    public int getX() {
        return x;
    }

    /**
     * Retorna a posição Y do Boss.
     * 
     * @return Posição Y
     */
    public int getY() {
        return y;
    }

    /**
     * Retorna a imagem do Boss.
     * 
     * @return A imagem do Boss
     */
    public BufferedImage getImage() {
        return bossImage;
    }
}
