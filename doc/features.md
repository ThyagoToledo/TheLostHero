# Funcionalidades

## Características do Jogo

- Movimento fluido com teclas WASD
- Mapa explorável com cenário detalhado
- Boss Battle contra Goblin com 80 HP
- Sistema de combate por turnos completo
- NPC interativo (Rimuru) centralizado no mapa
- Interface gráfica profissional com Java Swing
- Executável standalone - sem dependências externas

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

## Experiência de Jogo

1. **Início**: Clique em "Start" na tela inicial
2. **Exploração**: Use WASD para explorar o mapa
3. **Interação**: Aproxime-se do NPC Rimuru no centro
4. **Combate**: Encontre o Goblin para iniciar a batalha
5. **Estratégia**: Use diferentes ações para derrotar o boss

## Sistema de Combate Detalhado

- 4 opções de ação por turno
- Sistema de defesa funcional
- Sistema de itens implementado
- Dano variável com randomização
- Barras de vida visuais
- Alternância automática de turnos

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
