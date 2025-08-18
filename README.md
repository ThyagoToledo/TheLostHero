# The Lost Hero

## Descrição
The Lost Hero é um jogo RPG 2D desenvolvido em Java com sistema de combate por turnos. O jogador explora um mundo, interage com NPCs e enfrenta um poderoso Goblin Boss em batalhas estratégicas.

## Características do Jogo
- Movimento fluido com teclas WASD
- Mapa explorável com cenário detalhado
- Boss Battle contra Goblin com 80 HP
- Sistema de combate por turnos completo
- NPC interativo (Rimuru) centralizado no mapa
- Interface gráfica profissional com Java Swing
- Executável standalone - sem dependências externas

## Como Jogar

**Requisito:** Java 11 ou superior instalado

```bash
java -jar the-lost-hero.jar
```

## Como Funciona

### Tela Inicial
- Interface com botão de start funcional
- Carregamento automático de recursos
- Transição suave para o jogo principal

### Exploração
- **Movimento**: WASD para mover o personagem
- **Colisão**: Sistema de detecção precisa
- **NPCs**: Rimuru posicionado no centro do mapa
- **Limites**: Bordas do mapa impedem saída

### Sistema de Combate
- **Iniciação**: Colidir com o Goblin inicia a batalha
- **Turnos**: Jogador e Goblin alternam ações
- **Opções**: Ataque Básico, Ataque Especial, Defender, Usar Item
- **HP**: Goblin com 80 pontos de vida
- **Interface**: Sistema completo de menu de batalha

## Controles

| Tecla | Ação |
|-------|------|
| **W** | Mover para cima |
| **A** | Mover para esquerda |
| **S** | Mover para baixo |
| **D** | Mover para direita |
| **Mouse** | Interagir com botões da interface |
| **Setas** | Navegar no menu de combate |
| **Enter/Espaço** | Confirmar ações |

## Especificações Técnicas

### Visual
- **Resolução**: 768x576 pixels
- **Sprites**: Player, Goblin (1024x1024), Rimuru
- **Cenário**: Mapa mundial detalhado
- **Interface**: Tela inicial customizada

### Tecnologia
- **Java** - Linguagem principal (compatível Java 11+)
- **Java Swing** - Interface gráfica nativa
- **BufferedImage** - Renderização de sprites
- **Threading** - Loop de jogo otimizado (60 FPS)
- **ResourceStream** - Sistema de recursos para JAR

### Arquitetura
```
GamePainel     - Engine principal do jogo
TileManager    - Renderização do mapa
Boss           - Lógica do Goblin
CombateSystem  - Sistema de batalha por turnos
NpcPixelArtDev - Lógica do NPC Rimuru
KeyHandler     - Controles de entrada
TelaInicial    - Menu principal
```

## Estado Atual

### Implementado e Funcionando
- Tela inicial com botão funcional
- Loop principal do jogo a 60 FPS
- Sistema de movimento suave e responsivo
- Carregamento de recursos para JAR
- Detecção de colisão precisa
- Boss Goblin totalmente funcional (80 HP)
- Sistema de combate por turnos completo
- NPC Rimuru interativo posicionado
- Logs de debug detalhados
- Tratamento de erros robusto

### Sistema de Combate Detalhado
- 4 opções de ação por turno
- Sistema de defesa funcional
- Sistema de itens implementado
- Dano variável com randomização
- Barras de vida visuais
- Alternância automática de turnos

## Experiência de Jogo
1. **Início**: Clique em "Start" na tela inicial
2. **Exploração**: Use WASD para explorar o mapa
3. **Interação**: Aproxime-se do NPC Rimuru no centro
4. **Combate**: Encontre o Goblin para iniciar a batalha
5. **Estratégia**: Use diferentes ações para derrotar o boss

## Estrutura do Projeto
```
src/                    - Código fonte
├── Main.java          - Classe principal
├── TelaInicial.java   - Tela inicial
├── GamePainel.java    - Engine principal do jogo
├── Boss.java          - Boss Goblin
├── CombateSystem.java - Sistema de batalha
├── NpcPixelArtDev.java - NPC Rimuru
├── TileManager.java   - Gerenciador do mapa
└── io/github/jiangdequan/
    └── KeyHandler.java - Controles

resources/              - Recursos do jogo
├── Cenarios/          - Mapas
├── Inimigos/          - Sprites de inimigos
├── Interface/         - UI
├── Npcs/             - Sprites de NPCs
└── Player/           - Sprites do jogador

the-lost-hero.jar      - Executável pronto para uso
```

## Para Desenvolvedores
- Código limpo e bem documentado
- Arquitetura modular e extensível
- Sistema de recursos dual (desenvolvimento + JAR)
- Logs detalhados para debug
- Tratamento de exceções abrangente

## Licença
Este projeto é licenciado sob a Licença MIT - veja o arquivo [LICENSE](LICENSE) para detalhes.

**Desenvolvido como demonstração de game development em Java puro.**
