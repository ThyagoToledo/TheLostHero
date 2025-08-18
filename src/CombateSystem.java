import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Sistema de combate por turnos para a batalha contra o Goblin Boss
 */
public class CombateSystem {
    // Referência para o painel do jogo
    private GamePainel gp;
    
    // Atributos do jogador
    private int playerHP;
    private int playerMaxHP;
    private int playerMP;
    private int playerMaxMP;
    private int playerAttack;
    private int playerDefense;
    private int playerSpeed;
    private List<Item> inventario;
    private boolean espadaEquipada;
    
    // Atributos do goblin
    private int goblinHP;
    private int goblinMaxHP;
    private int goblinAttack;
    private int goblinDefense;
    private int goblinSpeed;
    
    // Estado do combate
    private boolean combateAtivo;
    private int turno; // 0 = jogador, 1 = inimigo
    private String mensagem;
    private List<String> historicoMensagens;
    
    // Interface do combate
    private String[] opcoes = {"Ataque Básico", "Ataque Especial", "Defender", "Usar Item"};
    private int opcaoSelecionada;
    private int itemSelecionado;
    private boolean mostraItens;
    private boolean aguardandoInput;
    
    // Animações
    private boolean animacaoAtaque;
    private boolean animacaoDefesa;
    private int tempoAnimacao;
    
    // Random para cálculos aleatórios
    private Random random;
    
    // Fonte para o texto
    private Font combateFont;
    private Font opcaoFont;
    
    /**
     * Construtor padrão
     */
    public CombateSystem() {
        this.random = new Random();
        this.historicoMensagens = new ArrayList<>();
        this.inventario = new ArrayList<>();
        
        // Adiciona alguns itens iniciais para teste
        inventario.add(new Item("Poção de Cura", "Recupera 10 pontos de vida", "vida", 10));
        inventario.add(new Item("Poção de Força", "Aumenta força em 3 pontos", "forca", 3));
        
        combateFont = new Font("Arial", Font.BOLD, 18);
        opcaoFont = new Font("Arial", Font.PLAIN, 16);
        resetCombate();
    }
    
    /**
     * Inicializa um novo combate
     * @param gp Referência ao painel do jogo
     */
    public void iniciarCombate(GamePainel gp) {
        this.gp = gp;
        
        // Configura atributos do jogador (poderiam vir de uma classe Jogador)
        playerMaxHP = 100;
        playerHP = playerMaxHP;
        playerMaxMP = 50;
        playerMP = playerMaxMP;
        playerAttack = 15;
        playerDefense = 10;
        playerSpeed = 10;
        espadaEquipada = false;
        
        // Configura o goblin
        goblinMaxHP = 80;
        goblinHP = goblinMaxHP;
        goblinAttack = 12;
        goblinDefense = 8;
        goblinSpeed = 7;
        
        // Configura o estado inicial do combate
        combateAtivo = true;
        turno = 0; // Jogador começa
        opcaoSelecionada = 0;
        itemSelecionado = 0;
        mostraItens = false;
        aguardandoInput = true;
        
        // Reseta animações
        animacaoAtaque = false;
        animacaoDefesa = false;
        tempoAnimacao = 0;
        
        // Mensagem inicial
        mensagem = "Um Goblin apareceu! O que você vai fazer?";
        adicionarMensagem(mensagem);
    }
    
    /**
     * Reseta o estado do combate
     */
    private void resetCombate() {
        combateAtivo = false;
        turno = 0;
        mensagem = "";
        opcaoSelecionada = 0;
        mostraItens = false;
        aguardandoInput = false;
    }
    
    /**
     * Processa as teclas pressionadas durante o combate
     * @param keyCode Código da tecla pressionada
     */
    public void keyPressed(int keyCode) {
        if (!combateAtivo) return;
        
        if (mostraItens) {
            processarTeclasItens(keyCode);
        } else {
            processarTeclasAcoes(keyCode);
        }
    }
    
    /**
     * Processa teclas quando o menu de itens está aberto
     */
    private void processarTeclasItens(int keyCode) {
        switch (keyCode) {
            case KeyEvent.VK_W:
            case KeyEvent.VK_UP:
                itemSelecionado = (itemSelecionado > 0) ? itemSelecionado - 1 : inventario.size() - 1;
                break;
                
            case KeyEvent.VK_S:
            case KeyEvent.VK_DOWN:
                itemSelecionado = (itemSelecionado < inventario.size() - 1) ? itemSelecionado + 1 : 0;
                break;
                
            case KeyEvent.VK_SPACE:
            case KeyEvent.VK_ENTER:
                if (inventario.size() > 0) {
                    usarItem(itemSelecionado);
                    mostraItens = false;
                    proximoTurno();
                }
                break;
                
            case KeyEvent.VK_ESCAPE:
                mostraItens = false;
                break;
        }
    }
    
    /**
     * Processa teclas quando o menu principal de ações está aberto
     */
    private void processarTeclasAcoes(int keyCode) {
        switch (keyCode) {
            case KeyEvent.VK_W:
            case KeyEvent.VK_UP:
                opcaoSelecionada = (opcaoSelecionada > 0) ? opcaoSelecionada - 1 : opcoes.length - 1;
                break;
                
            case KeyEvent.VK_S:
            case KeyEvent.VK_DOWN:
                opcaoSelecionada = (opcaoSelecionada < opcoes.length - 1) ? opcaoSelecionada + 1 : 0;
                break;
                
            case KeyEvent.VK_SPACE:
            case KeyEvent.VK_ENTER:
                executarAcao();
                break;
        }
    }
    
    /**
     * Executa a ação selecionada pelo jogador
     */
    private void executarAcao() {
        if (turno != 0) return; // Não executa ações fora do turno do jogador
        
        switch (opcaoSelecionada) {
            case 0: // Ataque Básico
                ataqueBasico();
                break;
                
            case 1: // Ataque Especial
                ataqueEspecial();
                break;
                
            case 2: // Defender
                defender();
                break;
                
            case 3: // Usar Item
                if (inventario.size() > 0) {
                    mostraItens = true;
                    itemSelecionado = 0;
                } else {
                    adicionarMensagem("Você não possui itens no inventário!");
                }
                return; // Não avança o turno até que um item seja usado
        }
        
        proximoTurno();
    }
    
    /**
     * Executa o ataque básico do jogador
     */
    private void ataqueBasico() {
        int dano = calcularDano();
        goblinHP -= dano;
        
        animacaoAtaque = true;
        tempoAnimacao = 20;
        
        adicionarMensagem("Você atacou o Goblin causando " + dano + " de dano!");
        
        if (goblinHP <= 0) {
            adicionarMensagem("Você derrotou o Goblin Boss!");
            finalizarCombate(true);
        }
    }
    
    /**
     * Executa o ataque especial do jogador
     */
    private void ataqueEspecial() {
        int dano = calcularDano() * 2; // Dobra o dano para o ataque especial
        goblinHP -= dano;
        
        animacaoAtaque = true;
        tempoAnimacao = 30;
        
        adicionarMensagem("Você usou um Ataque Especial causando " + dano + " de dano crítico!");
        
        if (goblinHP <= 0) {
            adicionarMensagem("Você derrotou o Goblin Boss!");
            finalizarCombate(true);
        }
    }
    
    /**
     * Jogador se coloca em posição defensiva
     */
    private void defender() {
        animacaoDefesa = true;
        tempoAnimacao = 40;
        
        adicionarMensagem("Você assumiu uma postura defensiva!");
    }
    
    /**
     * Usa um item do inventário
     * @param indice Índice do item a ser usado
     */
    private void usarItem(int indice) {
        if (indice < 0 || indice >= inventario.size()) return;
        
        Item item = inventario.get(indice);
        
        switch (item.atributo) {
            case "vida":
                playerHP = Math.min(playerMaxHP, playerHP + item.valor);
                adicionarMensagem("Você usou " + item.nome + " e recuperou " + item.valor + " pontos de vida!");
                break;
                
            case "forca":
                playerAttack += item.valor;
                adicionarMensagem("Você usou " + item.nome + " e aumentou sua força em " + item.valor + "!");
                break;
                
            case "agilidade":
                playerSpeed += item.valor;
                adicionarMensagem("Você usou " + item.nome + " e aumentou sua agilidade em " + item.valor + "!");
                break;
                
            case "inteligencia":
                playerDefense += item.valor;
                adicionarMensagem("Você usou " + item.nome + " e aumentou sua inteligência em " + item.valor + "!");
                break;
        }
        
        // Remove o item usado (exceto itens equipáveis)
        if (!item.nome.equals("Espada do Herói Antigo")) {
            inventario.remove(indice);
        } else {
            espadaEquipada = true;
            adicionarMensagem("Você equipou a " + item.nome + "!");
        }
    }
    
    /**
     * Cálculo de dano baseado nos atributos do jogador
     */
    private int calcularDano() {
        int danoBase = playerAttack - goblinDefense/2;
        int variacao = random.nextInt(5) - 2; // -2 a +2
        int dano = Math.max(1, danoBase + variacao);
        
        if (espadaEquipada) {
            dano += 5; // Bônus da espada
        }
        
        // Chance de crítico baseada na agilidade
        if (random.nextInt(20) < playerSpeed) {
            dano *= 1.5; // Dano crítico
        }
        
        return Math.max(1, dano); // Garantir pelo menos 1 de dano
    }
    
    /**
     * Avança para o próximo turno
     */
    private void proximoTurno() {
        // Verifica se o combate já acabou
        if (!combateAtivo) return;
        
        if (turno == 0) {
            // Turno do jogador acabou, inicia o turno do inimigo
            turno = 1;
            ataqueGoblin();
        } else {
            // Turno do inimigo acabou, inicia o turno do jogador
            turno = 0;
        }
    }
    
    /**
     * Executa o ataque do goblin
     */
    private void ataqueGoblin() {
        // Aguarda um pouco antes do goblin atacar
        new Thread(() -> {
            try {
                Thread.sleep(1000); // Espera 1 segundo
                
                // Calcula o dano do goblin
                int dano = 3 + random.nextInt(4); // 3-6 de dano
                
                // Reduz o dano se o jogador estiver defendendo
                if (animacaoDefesa) {
                    dano = Math.max(1, dano / 2);
                    animacaoDefesa = false;
                }
                
                // Aplica o dano
                playerHP -= dano;
                adicionarMensagem("O Goblin Boss atacou causando " + dano + " de dano!");
                
                // Verifica se o jogador foi derrotado
                if (playerHP <= 0) {
                    playerHP = 0;
                    adicionarMensagem("Você foi derrotado pelo Goblin Boss!");
                    finalizarCombate(false);
                } else {
                    // Passa o turno de volta para o jogador
                    turno = 0;
                }
                
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
    
    /**
     * Finaliza o combate
     */
    private void finalizarCombate(boolean vitoria) {
        combateAtivo = false;
        
        if (vitoria) {
            adicionarMensagem("Você derrotou o Goblin Boss!");
            
            // Adiciona recompensa
            if (!inventarioContem("Espada do Herói Antigo")) {
                Item espadaHeroi = new Item("Espada do Herói Antigo", 
                                          "Aumenta seu ataque em +5. Cada golpe causa mais dano.", 
                                          "equipavel", 5);
                inventario.add(espadaHeroi);
                adicionarMensagem("Você obteve a Espada do Herói Antigo!");
            } else {
                Item pocaoCura = new Item("Poção de Cura Grande", 
                                        "Restaura 20 pontos de vida", 
                                        "vida", 20);
                inventario.add(pocaoCura);
                adicionarMensagem("Você obteve uma Poção de Cura Grande!");
            }
        } else {
            adicionarMensagem("Você foi derrotado...");
        }
        
        // Volta para o jogo após um delay
        new Thread(() -> {
            try {
                Thread.sleep(2000);
                gp.finalizarCombate(vitoria);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
    
    /**
     * Adiciona uma mensagem ao histórico
     */
    private void adicionarMensagem(String msg) {
        historicoMensagens.add(msg);
        if (historicoMensagens.size() > 5) {
            historicoMensagens.remove(0);
        }
    }
    
    /**
     * Verifica se o inventário contém um item específico
     */
    private boolean inventarioContem(String nomeItem) {
        for (Item item : inventario) {
            if (item.nome.equals(nomeItem)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Atualiza as animações a cada frame
     */
    public void update() {
        if (tempoAnimacao > 0) {
            tempoAnimacao--;
            if (tempoAnimacao == 0) {
                animacaoAtaque = false;
            }
        }
    }
    
    /**
     * Desenha a interface de combate
     */
    public void draw(Graphics2D g2) {
        if (!combateAtivo) return;
        
        // Salva as configurações originais
        Color originalColor = g2.getColor();
        Font originalFont = g2.getFont();
        
        // Desenha o fundo escurecido
        g2.setColor(new Color(0, 0, 0, 200));
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);
        
        // Título
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 40));
        String titulo = "COMBATE";
        int tituloLargura = g2.getFontMetrics().stringWidth(titulo);
        g2.drawString(titulo, (gp.screenWidth - tituloLargura) / 2, 80);
        
        // Desenha o Goblin (se disponível)
        if (gp.goblinBoss != null) {
            int tamanhoGoblin = gp.tileSize * 2;
            int goblinX = gp.screenWidth / 4 - tamanhoGoblin / 2;
            int goblinY = gp.screenHeight / 3;
            g2.drawImage(gp.goblinBoss.getImage(), goblinX, goblinY, tamanhoGoblin, tamanhoGoblin, null);
            
            // Barra de vida do Goblin
            desenharBarraVida(g2, "Goblin Boss", goblinHP, goblinMaxHP, 
                            goblinX - 20, goblinY - 30, tamanhoGoblin + 40);
        }
        
        // Desenha o jogador (se disponível)
        if (gp.playerImage != null) {
            int tamanhoJogador = gp.tileSize * 2;
            int jogadorX = gp.screenWidth * 3/4 - tamanhoJogador / 2;
            int jogadorY = gp.screenHeight / 3;
            g2.drawImage(gp.playerImage, jogadorX, jogadorY, tamanhoJogador, tamanhoJogador, null);
            
            // Barra de vida do jogador
            desenharBarraVida(g2, "Jogador", playerHP, playerMaxHP, 
                            jogadorX - 20, jogadorY - 30, tamanhoJogador + 40);
        }
        
        // Histórico de mensagens
        int mensagemY = gp.screenHeight / 2;
        g2.setFont(new Font("Arial", Font.PLAIN, 16));
        g2.setColor(Color.WHITE);
        
        for (String msg : historicoMensagens) {
            int largura = g2.getFontMetrics().stringWidth(msg);
            g2.drawString(msg, (gp.screenWidth - largura) / 2, mensagemY);
            mensagemY += 25;
        }
        
        // Menu de ações
        if (combateAtivo && turno == 0) {
            if (mostraItens) {
                desenharMenuItens(g2);
            } else {
                desenharMenuAcoes(g2);
            }
        } else if (turno == 1) {
            // Mensagem durante turno do inimigo
            g2.setColor(Color.YELLOW);
            g2.setFont(new Font("Arial", Font.ITALIC, 16));
            String turnoMsg = "Turno do inimigo...";
            int largura = g2.getFontMetrics().stringWidth(turnoMsg);
            g2.drawString(turnoMsg, (gp.screenWidth - largura) / 2, gp.screenHeight - 80);
        }
        
        // Restaura as configurações originais
        g2.setColor(originalColor);
        g2.setFont(originalFont);
    }
    
    /**
     * Desenha o menu de ações de combate
     */
    private void desenharMenuAcoes(Graphics2D g2) {
        int menuX = gp.screenWidth / 4;
        int menuY = gp.screenHeight - 200;
        int menuLargura = gp.screenWidth / 2;
        int menuAltura = 150;
        
        // Fundo do menu
        g2.setColor(new Color(50, 50, 50, 200));
        g2.fillRoundRect(menuX, menuY, menuLargura, menuAltura, 20, 20);
        g2.setColor(Color.WHITE);
        g2.drawRoundRect(menuX, menuY, menuLargura, menuAltura, 20, 20);
        
        // Título do menu
        g2.setFont(new Font("Arial", Font.BOLD, 20));
        g2.drawString("Escolha sua ação:", menuX + 20, menuY + 30);
        
        // Opções do menu
        g2.setFont(new Font("Arial", Font.PLAIN, 16));
        for (int i = 0; i < opcoes.length; i++) {
            if (i == opcaoSelecionada) {
                g2.setColor(Color.YELLOW);
                g2.fillRect(menuX + 10, menuY + 45 + i*25, menuLargura - 20, 20);
                g2.setColor(Color.BLACK);
            } else {
                g2.setColor(Color.WHITE);
            }
            g2.drawString(opcoes[i], menuX + 30, menuY + 60 + i*25);
        }
    }
    
    /**
     * Desenha o menu de itens
     */
    private void desenharMenuItens(Graphics2D g2) {
        int menuX = gp.screenWidth / 4;
        int menuY = gp.screenHeight - 250;
        int menuLargura = gp.screenWidth / 2;
        int menuAltura = 200;
        
        // Fundo do menu
        g2.setColor(new Color(50, 50, 50, 200));
        g2.fillRoundRect(menuX, menuY, menuLargura, menuAltura, 20, 20);
        g2.setColor(Color.WHITE);
        g2.drawRoundRect(menuX, menuY, menuLargura, menuAltura, 20, 20);
        
        // Título do menu
        g2.setFont(new Font("Arial", Font.BOLD, 20));
        g2.drawString("Itens Disponíveis:", menuX + 20, menuY + 30);
        
        // Lista de itens
        g2.setFont(new Font("Arial", Font.PLAIN, 14));
        
        if (inventario.isEmpty()) {
            g2.drawString("Nenhum item disponível.", menuX + 30, menuY + 60);
        } else {
            for (int i = 0; i < inventario.size(); i++) {
                Item item = inventario.get(i);
                
                if (i == itemSelecionado) {
                    g2.setColor(Color.YELLOW);
                    g2.fillRect(menuX + 10, menuY + 45 + i*25, menuLargura - 20, 20);
                    g2.setColor(Color.BLACK);
                } else {
                    g2.setColor(Color.WHITE);
                }
                
                g2.drawString(item.nome + " - " + item.descricao, menuX + 20, menuY + 60 + i*25);
            }
        }
        
        // Instruções
        g2.setColor(Color.CYAN);
        g2.setFont(new Font("Arial", Font.ITALIC, 12));
        g2.drawString("ESC para voltar", menuX + menuLargura - 100, menuY + menuAltura - 10);
    }
    
    /**
     * Desenha uma barra de vida
     */
    private void desenharBarraVida(Graphics2D g2, String nome, int vida, int vidaMax, 
                                 int x, int y, int largura) {
        // Desenha o nome
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 14));
        g2.drawString(nome, x, y);
        
        // Fundo da barra
        g2.setColor(Color.DARK_GRAY);
        g2.fillRect(x, y + 5, largura, 15);
        
        // Barra de vida atual
        double proporcao = (double)vida / vidaMax;
        int larguraAtual = (int)(largura * proporcao);
        
        if (proporcao > 0.6) {
            g2.setColor(Color.GREEN);
        } else if (proporcao > 0.3) {
            g2.setColor(Color.YELLOW);
        } else {
            g2.setColor(Color.RED);
        }
        
        g2.fillRect(x, y + 5, larguraAtual, 15);
        
        // Borda da barra
        g2.setColor(Color.WHITE);
        g2.drawRect(x, y + 5, largura, 15);
        
        // Valor numérico
        g2.setFont(new Font("Arial", Font.PLAIN, 12));
        String valorVida = vida + "/" + vidaMax;
        g2.drawString(valorVida, x + largura/2 - 15, y + 18);
    }
    
    /**
     * Verifica se o combate está ativo
     * @return true se o combate ainda estiver acontecendo
     */
    public boolean isCombateAtivo() {
        return combateAtivo;
    }
    
    /**
     * Classe para representar itens
     */
    private class Item {
        private String nome;
        private String descricao;
        private String atributo;
        private int valor;
        
        public Item(String nome, String descricao, String atributo, int valor) {
            this.nome = nome;
            this.descricao = descricao;
            this.atributo = atributo;
            this.valor = valor;
        }
    }
} 