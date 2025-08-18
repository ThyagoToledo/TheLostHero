import javax.swing.JFrame;

public class Main {

    public static void main(String[] args) {
        JFrame window = new JFrame();
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);
        window.setTitle("The Lost Hero");

        // Cria a tela inicial em vez do painel do jogo
        TelaInicial telaInicial = new TelaInicial(window);
        window.add(telaInicial);

        // Define o tamanho da janela
        window.setSize(800, 600);
        
        // Centraliza a janela na tela
        window.setLocationRelativeTo(null);
        
        // Torna a janela visível
        window.setVisible(true);
    }
}