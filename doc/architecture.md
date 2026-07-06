# Arquitetura

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

### Componentes

```
GamePainel     - Engine principal do jogo
TileManager    - Renderização do mapa
Boss           - Lógica do Goblin
CombateSystem  - Sistema de batalha por turnos
NpcPixelArtDev - Lógica do NPC Rimuru
KeyHandler     - Controles de entrada
TelaInicial    - Menu principal
```

## Estrutura Completa do Projeto

```
src/                              - Código fonte
├── Main.java                     - Classe principal
├── TelaInicial.java              - Tela inicial
├── GamePainel.java               - Engine principal do jogo
├── Boss.java                     - Boss Goblin
├── CombateSystem.java            - Sistema de batalha
├── NpcPixelArtDev.java           - NPC Rimuru
├── TileManager.java              - Gerenciador do mapa
└── io/github/jiangdequan/
    └── KeyHandler.java           - Controles

resources/                        - Recursos do jogo
├── Cenarios/                     - Mapas
├── Inimigos/                     - Sprites de inimigos
├── Interface/                    - UI
├── Npcs/                         - Sprites de NPCs
└── Player/                       - Sprites do jogador

the-lost-hero.jar                 - Executável pronto para uso
```
