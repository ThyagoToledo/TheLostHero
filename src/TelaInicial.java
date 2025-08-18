import javax.swing.*;
import java.awt.*;
import java.io.InputStream;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.util.Arrays;

public class TelaInicial extends JPanel {

    // Imagem de fundo
    private BufferedImage backgroundImage;

    // Referência para a janela principal
    private JFrame janelaPrincipal;

    // Botão de start
    private JButton startButton;

    // Dimensões da tela
    private final int WIDTH = 800;
    private final int HEIGHT = 600;

    private boolean verificarImagem(BufferedImage img) {
        if (img == null) {
            System.out.println("Imagem é null");
            return false;
        }

        int width = img.getWidth();
        int height = img.getHeight();

        System.out.println("Verificando imagem: " + width + "x" + height);
        System.out.println("Tipo da imagem: " + img.getType());

        // Verifica se as dimensões são razoáveis
        if (width < 100 || height < 100 || width > 4096 || height > 4096) {
            System.out.println("Dimensões da imagem fora do esperado: " + width + "x" + height);
            return false;
        }

        // Verifica se a imagem tem conteúdo
        boolean hasContent = false;
        int transparentPixels = 0;
        int totalPixels = width * height;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pixel = img.getRGB(x, y);
                if ((pixel & 0xFF000000) != 0) {
                    hasContent = true;
                } else {
                    transparentPixels++;
                }
            }
        }

        System.out.println("Pixels transparentes: " + transparentPixels + " (" +
                (transparentPixels * 100 / totalPixels) + "%)");

        if (!hasContent) {
            System.out.println("Imagem parece estar vazia");
            return false;
        }

        if (transparentPixels == totalPixels) {
            System.out.println("Imagem completamente transparente");
            return false;
        }

        return true;
    }

    private BufferedImage corrigirImagem(BufferedImage original) {
        if (original == null)
            return null;

        int width = original.getWidth();
        int height = original.getHeight();

        // Cria uma nova imagem com o mesmo tamanho
        BufferedImage corrigida = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = corrigida.createGraphics();

        // Configura a qualidade de renderização
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        // Desenha a imagem original
        g2.drawImage(original, 0, 0, null);

        // Verifica se a imagem está completamente transparente
        boolean isTransparent = true;
        for (int y = 0; y < height && isTransparent; y++) {
            for (int x = 0; x < width && isTransparent; x++) {
                if ((original.getRGB(x, y) & 0xFF000000) != 0) {
                    isTransparent = false;
                }
            }
        }

        if (isTransparent) {
            // Se estiver transparente, adiciona um fundo
            g2.setColor(new Color(20, 20, 40));
            g2.fillRect(0, 0, width, height);
            g2.drawImage(original, 0, 0, null);
        }

        g2.dispose();
        return corrigida;
    }

    public TelaInicial(JFrame janela) {
        this.janelaPrincipal = janela;

        // Configurações do painel
        setLayout(null); // Layout absoluto para posicionar o botão manualmente
        setPreferredSize(new Dimension(WIDTH, HEIGHT));

        // Carrega a imagem de fundo
        try {
            // Tenta carregar a imagem como recurso primeiro (para JAR)
            InputStream imageStream = getClass().getResourceAsStream("/resources/Interface/inicio.png");
            if (imageStream != null) {
                System.out.println("Carregando imagem como recurso do JAR...");
                backgroundImage = ImageIO.read(imageStream);
                imageStream.close();
            } else {
                // Fallback para arquivo externo (para desenvolvimento)
                File imageFile = new File("resources/Interface/inicio.png");
                System.out.println("Procurando imagem em: " + imageFile.getAbsolutePath());

                if (imageFile.exists()) {
                    System.out.println("Arquivo encontrado! Tamanho: " + imageFile.length() + " bytes");
                    backgroundImage = ImageIO.read(imageFile);
                } else {
                    System.out.println("Arquivo de imagem não encontrado!");
                    backgroundImage = null;
                }
            }

            if (backgroundImage != null) {
                System.out.println("Imagem carregada com sucesso!");
                System.out.println("Tipo da imagem: " + backgroundImage.getType());
                System.out.println("Dimensões: " + backgroundImage.getWidth() + "x" + backgroundImage.getHeight());

                // Verifica se a imagem tem conteúdo
                boolean hasContent = false;
                for (int y = 0; y < backgroundImage.getHeight() && !hasContent; y++) {
                    for (int x = 0; x < backgroundImage.getWidth() && !hasContent; x++) {
                        if ((backgroundImage.getRGB(x, y) & 0xFF000000) != 0) {
                            hasContent = true;
                        }
                    }
                }

                if (!hasContent) {
                    System.out.println("Imagem está vazia ou completamente transparente");
                    criarImagemFallback();
                }
            } else {
                System.out.println("Falha ao carregar imagem");
                criarImagemFallback();
            }
        } catch (Exception e) {
            System.out.println("Erro ao carregar imagem: " + e.getMessage());
            e.printStackTrace();
            criarImagemFallback();
        }

        // Cria e configura o botão de início
        criarBotaoStart();

    }

    // Método para criar a imagem de fallback caso a imagem real não seja encontrada
    private void criarImagemFallback() {
        backgroundImage = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = backgroundImage.createGraphics();

        // Configura a qualidade de renderização
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // Fundo gradiente
        GradientPaint gradient = new GradientPaint(0, 0, new Color(20, 20, 40), 0, HEIGHT, new Color(40, 40, 80));
        g2.setPaint(gradient);
        g2.fillRect(0, 0, WIDTH, HEIGHT);

        // Título
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 40));
        String title = "The Lost Hero";
        int titleWidth = g2.getFontMetrics().stringWidth(title);
        g2.drawString(title, (WIDTH - titleWidth) / 2, 200);

        // Mensagem de erro
        g2.setFont(new Font("Arial", Font.PLAIN, 16));
        String errorMsg = "Imagem Inicio.png não encontrada";
        int errorWidth = g2.getFontMetrics().stringWidth(errorMsg);
        g2.drawString(errorMsg, (WIDTH - errorWidth) / 2, 250);

        // Instruções
        g2.setFont(new Font("Arial", Font.ITALIC, 14));
        String instructions = "Verifique se o arquivo existe em: Img/Interface/Inicio.png";
        int instrWidth = g2.getFontMetrics().stringWidth(instructions);
        g2.drawString(instructions, (WIDTH - instrWidth) / 2, 280);

        g2.dispose();
    }

    // Método para criar e configurar o botão de início
    private void criarBotaoStart() {
        startButton = new JButton(""); // O texto 'START' foi removido para torná-lo invisível
        startButton.setFont(new Font("Arial", Font.BOLD, 20));
        startButton.setFocusPainted(false); // Remove a borda de foco

        // Torna o botão visualmente invisível, mas ainda clicável
        startButton.setOpaque(false);
        startButton.setContentAreaFilled(false);
        startButton.setBorderPainted(false);

        // Tamanho e posição do botão
        int buttonWidth = 250; // Ajustado para corresponder à imagem
        int buttonHeight = 70; // Ajustado para corresponder à imagem
        int buttonX = (WIDTH - buttonWidth) / 2; // Centraliza horizontalmente
        int buttonY = 330; // Ajustado para ir um pouco mais para cima (era 350)
        startButton.setBounds(buttonX, buttonY, buttonWidth, buttonHeight);

        // Adiciona ação ao botão - iniciar o jogo quando clicado
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                iniciarJogo();
            }
        });

        // Adiciona o botão ao painel
        add(startButton);
    }

    // Método para iniciar o jogo
    private void iniciarJogo() {
        System.out.println("=== INICIANDO JOGO ===");
        System.out.println("Botão de start foi clicado!");

        try {
            System.out.println("Removendo componentes da tela inicial...");
            // Remove a tela inicial e o botão
            janelaPrincipal.getContentPane().removeAll();
            System.out.println("Componentes removidos com sucesso!");

            System.out.println("Criando GamePainel...");
            // Cria e adiciona o painel do jogo
            GamePainel gamePainel = new GamePainel();
            System.out.println("GamePainel criado com sucesso!");

            System.out.println("Adicionando GamePainel à janela...");
            janelaPrincipal.add(gamePainel);
            System.out.println("GamePainel adicionado com sucesso!");

            System.out.println("Configurando tamanho da janela...");
            // Atualiza o tamanho da janela conforme o tamanho preferido do painel
            janelaPrincipal.setSize(gamePainel.getPreferredSize());
            System.out.println("Tamanho configurado: " + gamePainel.getPreferredSize());

            System.out.println("Atualizando layout...");
            // Atualiza o layout
            janelaPrincipal.revalidate();
            janelaPrincipal.repaint();
            System.out.println("Layout atualizado!");

            System.out.println("Centralizando janela...");
            // Centraliza a janela na tela
            janelaPrincipal.setLocationRelativeTo(null);
            System.out.println("Janela centralizada!");

            System.out.println("Configurando foco...");
            // Dá foco ao painel do jogo para receber input do teclado
            gamePainel.requestFocusInWindow();
            System.out.println("Foco configurado!");

            System.out.println("Iniciando thread do jogo...");
            // Garante que os eventos de teclado são processados
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    // Inicia a thread do jogo
                    try {
                        gamePainel.startGameThread();
                        System.out.println("Thread do jogo iniciada com sucesso!");
                    } catch (Exception ex) {
                        System.out.println("ERRO ao iniciar thread do jogo: " + ex.getMessage());
                        ex.printStackTrace();
                    }
                }
            });

            System.out.println("=== JOGO INICIADO COM SUCESSO! ===");
        } catch (Exception e) {
            System.out.println("=== ERRO CRÍTICO AO INICIAR O JOGO ===");
            System.out.println("Erro: " + e.getMessage());
            System.out.println("Causa: " + (e.getCause() != null ? e.getCause().getMessage() : "Não especificada"));
            e.printStackTrace();

            // Tenta mostrar uma mensagem de erro ao usuário
            try {
                javax.swing.JOptionPane.showMessageDialog(janelaPrincipal,
                        "Erro ao iniciar o jogo:\n" + e.getMessage(),
                        "Erro",
                        javax.swing.JOptionPane.ERROR_MESSAGE);
            } catch (Exception dialogError) {
                System.out.println("Erro ao mostrar diálogo de erro: " + dialogError.getMessage());
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Verifica se a imagem foi carregada
        if (backgroundImage != null) {
            // Desenha a imagem de fundo redimensionada para o tamanho do painel
            // Usando drawImage com melhor qualidade
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2d.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }
}
