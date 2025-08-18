import io.github.jiangdequan.KeyHandler;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class GamePainel extends JPanel implements Runnable, KeyListener {

    // Configurações da Tela
    final int originalTileSize = 16; // 16x16 titulo
    final int scale = 3;

    final int tileSize = originalTileSize * scale; // 48x48 titulo
    final int maxScreenCol = 16;
    final int maxScreenRow = 12;
    final int screenWidth = tileSize * maxScreenCol; // 768 pixels
    final int screenHeight = tileSize * maxScreenRow; // 576 pixels

    KeyHandler keyH = new KeyHandler();
    Thread gameThread;
    BufferedImage playerImage;
    TileManager tileM;
    Boss goblinBoss;

    // Sistema de combate
    private CombateSystem combate;

    // Seta o player numa posição padrão
    int playerX = 100;
    int playerY = 100;
    int playerSpeed = 4;

    // Margem de colisão (para movimento mais fluido)
    int collisionMargin = 8;

    // Limites do mapa
    int minX = 0;
    int minY = 0;
    int maxX;
    int maxY;

    // FPS Counter
    private int fps;
    private long fpsTimer = 0;
    private int fpsCount = 0;
    private Font fpsFont;

    // Estado do jogo
    private boolean emBatalha = false;

    private NpcPixelArtDev pixelArtDevNpc;
    private boolean showNpcDialog = false;

    public GamePainel() {
        System.out.println("=== CONSTRUINDO GAME PAINEL ===");
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.BLACK); // Fundo preto
        this.setDoubleBuffered(true);
        this.addKeyListener(this); // Usa este painel como KeyListener
        this.setFocusable(true);
        System.out.println("Propriedades básicas configuradas!");

        // Define os limites máximos (considerando o tamanho do personagem)
        maxX = screenWidth - tileSize;
        maxY = screenHeight - tileSize;

        // Inicializa a fonte para o contador de FPS
        fpsFont = new Font("Arial", Font.BOLD, 16);

        System.out.println("Inicializando TileManager...");
        // Inicializa o TileManager
        tileM = new TileManager(this);

        System.out.println("Inicializando Boss...");
        // Inicializa o Goblin Boss
        goblinBoss = new Boss(this);

        System.out.println("Inicializando sistema de combate...");
        // Inicializa o sistema de combate
        combate = new CombateSystem();

        // Carrega a imagem do jogador
        try {
            // Tenta carregar como recurso primeiro (para JAR)
            InputStream imageStream = getClass().getResourceAsStream("/resources/Player/Player.png");
            if (imageStream != null) {
                System.out.println("Carregando imagem do jogador como recurso do JAR...");
                playerImage = ImageIO.read(imageStream);
                imageStream.close();
                System.out.println("Imagem do player carregada com sucesso!");
            } else {
                // Fallback para arquivo externo (para desenvolvimento)
                File playerFile = new File("resources/Player/Player.png");
                System.out.println("Procurando imagem do jogador em: " + playerFile.getAbsolutePath());

                if (playerFile.exists()) {
                    playerImage = ImageIO.read(playerFile);
                    System.out.println("Imagem do player carregada com sucesso!");
                } else {
                    System.out.println("Arquivo de imagem do jogador não encontrado!");
                    playerImage = null;
                }
            }

            if (playerImage == null) {
                // Cria uma imagem em branco para o jogador
                playerImage = new BufferedImage(tileSize, tileSize, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2 = playerImage.createGraphics();
                g2.setColor(Color.WHITE);
                g2.fillRect(0, 0, tileSize, tileSize);
                g2.dispose();
            }
        } catch (IOException e) {
            System.out.println("Erro ao carregar a imagem do player: " + e.getMessage());
            e.printStackTrace();
        }

        // Posiciona o jogador inicialmente em uma posição segura
        playerX = 8 * tileSize; // Posição X inicial
        playerY = 3 * tileSize; // Posição Y inicial

        // Adiciona detector de redimensionamento
        this.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                // Atualiza a imagem do mapa quando a janela é redimensionada
                if (tileM != null) {
                    tileM.updateScaledImage();
                }

                // Atualiza os limites máximos
                maxX = getWidth() - tileSize;
                maxY = getHeight() - tileSize;

                // Corrige a posição do jogador se estiver fora dos limites
                playerX = Math.max(minX, Math.min(playerX, maxX));
                playerY = Math.max(minY, Math.min(playerY, maxY));
            }
        });

        System.out.println("Inicializando NPC...");
        pixelArtDevNpc = new NpcPixelArtDev(
                (screenWidth - (tileSize * 4 / 3)) / 2, // X para centralizar com o novo tamanho
                (screenHeight - (tileSize * 4 / 3)) / 2, // Y para centralizar com o novo tamanho
                (int) (tileSize * 4 / 3) // Tamanho aumentado em 1/3
        );
        System.out.println("=== GAME PAINEL CONSTRUÍDO COM SUCESSO! ===");
    }

    public void startGameThread() {
        System.out.println("=== INICIANDO THREAD DO JOGO ===");
        try {
            if (gameThread != null) {
                System.out.println("Thread do jogo já existe, parando a anterior...");
                gameThread = null;
            }

            System.out.println("Criando nova thread do jogo...");
            gameThread = new Thread(this);
            System.out.println("Iniciando thread...");
            gameThread.start();
            System.out.println("=== THREAD DO JOGO INICIADA COM SUCESSO! ===");
        } catch (Exception e) {
            System.out.println("=== ERRO AO INICIAR THREAD DO JOGO ===");
            System.out.println("Erro: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        System.out.println("=== LOOP DO JOGO INICIADO ===");
        double drawInterval = 1000000000.0 / 60; // 60 FPS alvo
        double nextDrawTime = System.nanoTime() + drawInterval;
        long lastFpsCheck = System.nanoTime();

        try {
            while (gameThread != null) {
                // 1. Atualizar: informações da posição do personagem
                update();

                // 2. Renderizar: desenhar a tela
                repaint();

                // Contador de FPS
                fpsCount++;
                if (System.nanoTime() - lastFpsCheck >= 1000000000) { // A cada segundo
                    fps = fpsCount;
                    fpsCount = 0;
                    lastFpsCheck = System.nanoTime();
                }

                try {
                    double remainingTime = (nextDrawTime - System.nanoTime()) / 1000000;
                    if (remainingTime > 0) {
                        Thread.sleep((long) remainingTime);
                    }
                    nextDrawTime += drawInterval;
                } catch (InterruptedException e) {
                    System.out.println("Thread do jogo interrompida: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            System.out.println("=== ERRO NO LOOP DO JOGO ===");
            System.out.println("Erro: " + e.getMessage());
            e.printStackTrace();
        } finally {
            System.out.println("=== LOOP DO JOGO FINALIZADO ===");
        }
    }

    public void update() {
        // Se estiver em batalha, atualiza o sistema de combate
        if (emBatalha) {
            if (combate != null) {
                combate.update();
            }
            return;
        }

        // Movimentação com verificação de colisão
        if (keyH.isUpPressed()) {
            int nextY = playerY - playerSpeed;
            if (nextY >= minY && !checkNpcCollision(playerX, nextY, tileSize)) {
                playerY = nextY;
            } else if (nextY < minY) {
                playerY = minY;
            }
        } else if (keyH.isDownPressed()) {
            int nextY = playerY + playerSpeed;
            if (nextY <= maxY && !checkNpcCollision(playerX, nextY, tileSize)) {
                playerY = nextY;
            } else if (nextY > maxY) {
                playerY = maxY;
            }
        } else if (keyH.isLeftPressed()) {
            int nextX = playerX - playerSpeed;
            if (nextX >= minX && !checkNpcCollision(nextX, playerY, tileSize)) {
                playerX = nextX;
            } else if (nextX < minX) {
                playerX = minX;
            }
        } else if (keyH.isRightPressed()) {
            int nextX = playerX + playerSpeed;
            if (nextX <= maxX && !checkNpcCollision(nextX, playerY, tileSize)) {
                playerX = nextX;
            } else if (nextX > maxX) {
                playerX = maxX;
            }
        }

        // Verifica colisão com o Boss
        if (goblinBoss != null && goblinBoss.verificarColisao(playerX, playerY, tileSize)) {
            iniciarBatalha();
        }

        if (pixelArtDevNpc != null && pixelArtDevNpc.isVisible()) {
            Rectangle playerRect = new Rectangle(playerX, playerY, tileSize, tileSize);

            // Nova área de detecção para a caixa de diálogo (o dobro da hitbox atual do
            // NPC, e centralizada)
            int dialogTriggerSize = pixelArtDevNpc.getHitbox().width * 2; // Dobra a largura da hitbox
            int dialogTriggerX = pixelArtDevNpc.getHitbox().x + pixelArtDevNpc.getHitbox().width / 2
                    - dialogTriggerSize / 2;
            int dialogTriggerY = pixelArtDevNpc.getHitbox().y + pixelArtDevNpc.getHitbox().height / 2
                    - dialogTriggerSize / 2;
            Rectangle dialogTriggerRect = new Rectangle(dialogTriggerX, dialogTriggerY, dialogTriggerSize,
                    dialogTriggerSize);

            if (playerRect.intersects(dialogTriggerRect)) {
                showNpcDialog = true;
            } else {
                showNpcDialog = false;
            }
        }
    }

    /**
     * Verifica colisão com o NPC PixelArtDev.
     * 
     * @param nextPlayerX Posição X futura do jogador
     * @param nextPlayerY Posição Y futura do jogador
     * @param playerSize  Tamanho do jogador
     * @return true se houver colisão com o NPC, false caso contrário
     */
    private boolean checkNpcCollision(int nextPlayerX, int nextPlayerY, int playerSize) {
        if (pixelArtDevNpc != null && pixelArtDevNpc.isVisible()) {
            Rectangle playerFutureRect = new Rectangle(nextPlayerX, nextPlayerY, playerSize, playerSize);
            if (playerFutureRect.intersects(pixelArtDevNpc.getHitbox())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Inicia uma batalha com o Boss
     */
    private void iniciarBatalha() {
        emBatalha = true;
        System.out.println("Iniciando batalha com o Goblin!");

        // Inicializa o sistema de combate
        combate.iniciarCombate(this);
    }

    /**
     * Finaliza o combate e retorna ao jogo normal
     * 
     * @param vitoria true se o jogador venceu, false se perdeu
     */
    public void finalizarCombate(boolean vitoria) {
        emBatalha = false;

        // Reseta o estado das teclas para evitar movimento indesejado
        if (keyH != null) {
            keyH.reset();
        }

        if (vitoria) {
            System.out.println("Jogador venceu o combate!");
            // Atualiza o estado do Boss para morto e vida zero
            if (goblinBoss != null) {
                goblinBoss.updateFromCombat(0, 80, true);
            }
            // Recompensas já são aplicadas na classe Combate
            if (pixelArtDevNpc != null) {
                pixelArtDevNpc.setVisible(true);
            }
        } else {
            System.out.println("Jogador perdeu o combate!");
            // Reposiciona o jogador em uma área segura
            playerX = 8 * tileSize; // Posição inicial X
            playerY = 3 * tileSize; // Posição inicial Y
        }

        // Reseta o Boss para permitir interações futuras
        // goblinBoss.reset(); // Removido para não reviver o boss após derrota
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        // Desenha o mapa
        tileM.draw(g2);

        // Desenha o Boss
        if (goblinBoss != null) {
            goblinBoss.draw(g2);
        }

        // Desenha a imagem do jogador
        if (playerImage != null) {
            g2.drawImage(playerImage, playerX, playerY, tileSize, tileSize, null);
        } else {
            // Fallback para o retângulo branco caso a imagem não carregue
            g2.setColor(Color.white);
            g2.fillRect(playerX, playerY, tileSize, tileSize);
        }

        // Se estiver em batalha, desenha a interface de combate
        if (emBatalha && combate != null) {
            combate.draw(g2);
        }

        // Desenha o contador de FPS
        g2.setFont(fpsFont);
        g2.setColor(Color.YELLOW);
        g2.drawString("FPS: " + fps, 10, 20);

        if (pixelArtDevNpc != null) {
            pixelArtDevNpc.draw(g2);
        }

        if (showNpcDialog) {
            g2.setColor(new Color(0, 0, 0, 200));
            g2.fillRect(100, screenHeight - 100, screenWidth - 200, 60);
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Arial", Font.BOLD, 18));
            g2.drawString("Obrigado por jogar, dev Thyago", 120, screenHeight - 60);
        }

        g2.dispose();
    }

    // Implementação dos métodos da interface KeyListener
    @Override
    public void keyTyped(KeyEvent e) {
        // Não utilizado
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();

        // Se estiver em batalha, passa o input para o sistema de combate
        if (emBatalha && combate != null) {
            combate.keyPressed(code);
            return;
        }

        // Caso contrário, passa os inputs para o KeyHandler normal
        keyH.keyPressed(e);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // Não passamos eventos keyReleased durante batalha
        if (!emBatalha) {
            keyH.keyReleased(e);
        }
    }
}
