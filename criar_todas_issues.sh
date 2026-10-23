#!/usr/bin/env bash
# ==============================================================================
# Script de criação automatizada de 180 issues detalhadas no GitHub
# ==============================================================================

set -e

echo "🚀 Iniciando criação em lote de 180 issues detalhadas no repositório..."

issues=(
  # 1. Arquitetura do Navegador & Camadas (1-18)
  "[ARCH] Implementação do Master StackPane de duas camadas no Navegador|Estruturação do container raiz rootMaster com divisão estrita entre camadaTelas e seletor flutuante de tema para manter o botão sempre visível."
  "[ARCH] Modelagem da pilha de navegação com Deque no Navegador|Utilização de ArrayDeque para gerenciar o histórico de telas permitindo avanço e retrocesso atômico nas transições de cena."
  "[ARCH] Criação do record imutável Tela para histórico de navegação|Encapsulamento do nó raiz Parent e do MenuController correspondente dentro de um Java Record no Navegador."
  "[ARCH] Injeção centralizada do SistemaFacade e Navegador nos controllers|Padronização do método inicializar(sistema, navegador) no MenuController para desacoplar as telas da persistência."
  "[ARCH] Hook de ciclo de vida aoExibir() para recarregamento contextual|Implementação de chamada automática de aoExibir() após cada término de animação de troca de tela."
  "[ARCH] Sincronização automática do título do Stage via getTitulo()|Atualização reativa do título da janela do sistema baseado no retorno de getTitulo() do controlador ativo."
  "[NAV] Animação de transição lateral suave de avanço de ecrã|Combinação coordenada de TranslateTransition horizontal com FadeTransition suave ao navegar para nova tela."
  "[NAV] Transição mecânica reversa para retorno de ecrã|Implementação de animação de slide reverso com desaceleração suave ao acionar o método voltar()."
  "[NAV] Transição em Fade independente via método reiniciar(fxml)|Implementação do método reiniciar para recarregar fluxos limpos (como logout e login) sem histórico prévio."
  "[PERF] Ativação de aceleração gráfica com CacheHint.SPEED nas transições|Uso de cache de bitmap e dica de aceleração de hardware nas telas durante as animações para eliminar engasgos."
  "[PERF] Liberação programada de caches de GPU no callback onFinished|Desativação sistemática de setCache(false) após a finalização das transições para evitar sobrecarga de VRAM."
  "[NAV] Flag animando para blindagem contra cliques múltiplos simultâneos|Bloqueio de novas transições antes do término da animação ativa para prevenir corrupção da pilha de telas."
  "[NAV] Definição de dimensões estruturais mínimas e ideais da janela|Configuração de minWidth=800, minHeight=500 e resolução padrão de 1024x660 no Navegador."
  "[NAV] Resolvedor resiliente de FXML com fallbacks de carregamento|Tratamento de exceções com busca sequencial por nomes alternativos de FXML prevenindo falhas silenciosas."
  "[NAV] Suporte a configurador genérico Consumer<T> em rotas|Capacidade de injetar parâmetros e estados em controladores específicos no momento da abertura da tela."
  "[NAV] Eliminação de flashes brancos de fundo durante transições|Vinculação dinâmica da cor de fundo do container de transição à cor do tema ativo no GerenciadorTema."
  "[NAV] Centralização do ciclo de vida do Stage no Navegador|Encapsulamento completo de setScene, setTitle e fechamento de janelas dentro da infraestrutura do Navegador."
  "[NAV] Exposição dos métodos de acesso getStage() e getScene()|Disponibilização da janela e cena principais para componentes filhos gerenciarem diálogos e overlays."

  # 2. Motor de Temas & GerenciadorTema (19-35)
  "[THEME] Criação da classe singleton de controle GerenciadorTema|Centralização do estado global do tema através de flags e métodos de alternância estáticos."
  "[THEME] Alternância dinâmica de folhas CSS sem reinício de aplicação|Limpeza e aplicação reativa de tema-claro.css e tema-escuro.css na lista de stylesheets da Scene."
  "[THEME] Sincronização de preenchimento da Scene via scene.setFill()|Definição de preenchimento preto puro no modo escuro e cinza claro no modo claro para evitar bordas descoloridas."
  "[THEME] Sistema de preservação de estilo original com CHAVE_ESTILO_ORIGINAL|Armazenamento do valor original de getStyle() no mapa de propriedades de cada nó antes de qualquer alteração."
  "[THEME] Restauração 100% fidedigna do layout ao retornar ao Modo Claro|Reaplicação exata dos estilos originais guardados em memória ao desligar o modo escuro sem perdas de layout."
  "[THEME] Varredura recursiva de nós com Parent.getChildrenUnmodifiable()|Processamento profundo de todos os componentes da árvore visual para adaptação a temas."
  "[THEME] Tratamento de nós gráficos internos de botões com btn.getGraphic()|Inspeção recursiva de ícones e rótulos aninhados dentro de botões para conversão de cores no Dark Mode."
  "[THEME] Forçamento de plano de fundo preto absoluto (#000000) na raiz|Sobrescrita de fundos claros inline por preto OLED puro em todos os painéis raiz e intermediários."
  "[THEME] Conversão de cartões e superfícies para carvão profundo (#0D0E12)|Substituição de containers brancos inline por superfícies escuras com bordas cinza-grafite #1F2229."
  "[THEME] Conversão de títulos e textos principais para branco puro (#FFFFFF)|Remoção de travas de texto preto (#1c1c1e) e injeção de alto contraste no tema escuro."
  "[THEME] Conversão de subtítulos e descrições para prata neutro (#A1A1AA)|Garantia de leitura confortável para textos secundários sem perder legibilidade."
  "[THEME] Injeção de -fx-control-inner-background em TableViews no modo escuro|Eliminação definitiva de fundos brancos em tabelas que herdam de Control."
  "[THEME] Forçamento de zebrado escuro (#08090B) nas linhas de tabelas|Garantia de diferenciação de linhas de tabelas em modo escuro sem destoar do preto profundo."
  "[THEME] Disparo de tv.refresh() após aplicação de tema em TableViews|Atualização forçada das células das tabelas para renderizar as novas cores de imediato."
  "[THEME] Preservação do componente ThemeToggleSwitch durante a sanitização|Isolamento estrutural do seletor para evitar corrupção das suas sombras e gradientes próprios."
  "[THEME] Adaptação automática de campos de entrada TextInputControl|Conversão de caixas de texto para fundo escuro #14161C e placeholder #6B7280."
  "[THEME] Sanitização do botão de encerramento de sessão no Dark Mode|Aplicação de fundo carvão com texto e borda vermelhos translúcidos no botão de logout."

  # 3. Componente ThemeToggleSwitch (36-50)
  "[UI] Criação do componente customizado ThemeToggleSwitch|Implementação de componente visual estendendo HBox para alternância de tema no estilo Apple."
  "[UI] Layout neumórfico com ícone de sol, trilho rebaixado e lua|Composição visual inspirada em interfaces táteis modernas com relevo mecânico."
  "[UI] Ancoragem permanente no canto inferior direito (Pos.BOTTOM_RIGHT)|Fixação flutuante 24/7 sem sobreposição aos botões de cabeçalho das telas."
  "[UI] Definição de margem de respiro de 20px lateral e 18px inferior|Ajuste estético de distância das bordas da janela no StackPane master."
  "[UI] Trava de tamanho com Region.USE_PREF_SIZE no ThemeToggleSwitch|Bloqueio de expansão involuntária do botão na tela evitando deformação no layout."
  "[UI] Trilho neumórfico rebaixado de tamanho compacto (36x18px)|Desenho de trilho elíptico com innershadow para simular depressão física."
  "[UI] Botão deslizante circular de 14x14px com dropshadow suave|Criação do manipulador (thumb) com relevo e sombra projetada suave."
  "[UI] Animação de deslizamento mecânico via TranslateTransition|Deslocamento horizontal de 18px com interpolação Interpolator.EASE_BOTH ao alternar."
  "[UI] Ícones vetoriais de sol (fas-sun) e lua (fas-moon) via Ikonli|Uso de fontes vetoriais FontAwesome com dimensionamento nítido de 11px."
  "[UI] Cores dinâmicas de ícones para o Modo Escuro|Configuração de sol inativo em cinza #6B7280 e lua ativa em lilás suave #A5B4FC."
  "[UI] Cores dinâmicas de ícones para o Modo Claro|Configuração de sol ativo em âmbar #F59E0B e lua inativa em cinza neutro #9CA3AF."
  "[UI] Cápsula container translúcida com cantos arredondados de 16px|Borda protetora do seletor com blur e sombra para contraste em qualquer fundo."
  "[UI] Prevenção de cliques concorrentes com flag animando no seletor|Bloqueio de disparos repetidos antes da conclusão da animação do botão."
  "[UI] Método atualizarVisualSemAnimacao para inicialização de cena|Alinhamento imediato da posição do botão conforme o estado atual ao abrir o app."
  "[A11Y] Feedback de cursor em mãozinha (Cursor.HAND) no ThemeToggleSwitch|Indicação tátil de interatividade em toda a área clicável do componente."

  # 4. Estilos Globais & Scrollbars macOS (51-65)
  "[STYLE] Scrollbars minimalistas ultrafinas no estilo macOS (8px)|Redução da espessura das barras de rolagem vertical e horizontal para 8px."
  "[STYLE] Fundo transparente no trilho das scrollbars globais|Eliminação de fundos cinzas largos do Modena em todas as telas com rolagem."
  "[STYLE] Remoção de botões de seta nas extremidades das scrollbars|Supressão das setas de incremento e decremento para visual limpo estilo macOS."
  "[STYLE] Thumb arredondado de 10px com opacidade dinâmica|Modelagem de pílula flutuante translúcida que ganha opacidade no estado de hover."
  "[STYLE] Adaptação da scrollbar para o tema-claro.css|Configuração de thumb cinza neutro com rgba(60, 60, 67, 0.25) no tema claro."
  "[STYLE] Adaptação da scrollbar para o tema-escuro.css|Configuração de thumb branco translúcido com rgba(255, 255, 255, 0.25) no escuro."
  "[STYLE] Tipografia corporativa baseada no Apple Human Interface Guidelines|Aplicação universal de -apple-system, 'SF Pro Text', 'Segoe UI', sans-serif."
  "[STYLE] Criação da classe CSS reutilizável .card-superficie|Padronização de contêineres com raio de 16px e bordas semânticas."
  "[STYLE] Criação da classe CSS .campo-busca com cantos de 8px|Padronização de caixas de texto com cores contextuais de prompt e foco azul."
  "[STYLE] Definição de paleta base OLED Pitch Black absoluta (#000000)|Configuração da propriedade -fx-base e -fx-background-color em tema-escuro.css."
  "[STYLE] Definição de paleta neutra Apple Light (#F5F5F7)|Configuração de tons de cinza suave para o ambiente claro padrão."
  "[STYLE] Padronização de botões institucionais com cantos de 10px|Botões com borda fina de 1px, texto em peso bold e efeito sutil no hover."
  "[STYLE] Estilização específica para estados de hover em botões escuros|Elevação visual com fundo #1E212B e borda #3B4050 ao passar o mouse."
  "[STYLE] Definição da cor de seleção de linhas de tabelas (#172554)|Azul noturno de alto contraste para marcação de registros selecionados."
  "[STYLE] Padronização de cabeçalhos de tabelas com altura e tipografia limpa|Cabeçalhos escuros #08090B com texto #A1A1AA em negrito e 11px."

  # 5. Sistema de Badges Semânticas & Neon (66-80)
  "[STYLE] Concepção da classe semântica .badge-sucesso|Criação de classe universal para estados positivos (Ativo, Paga, Em Dia, Disponível)."
  "[STYLE] Estilização verde esmeralda Neon (#34D399) em .badge-sucesso|Fundo translúcido rgba(16, 185, 129, 0.20) com borda luminosa no Dark Mode."
  "[STYLE] Concepção da classe semântica .badge-perigo|Criação de classe universal para alertas e falhas (Inativo, Cancelado, Atrasado)."
  "[STYLE] Estilização vermelho coral Neon (#F87171) em .badge-perigo|Fundo translúcido rgba(239, 68, 68, 0.20) com borda luminosa no Dark Mode."
  "[STYLE] Concepção da classe semântica .badge-aviso|Criação de classe universal para pendências e atenção (Pendente, Manutenção)."
  "[STYLE] Estilização âmbar Neon (#FBBF24) em .badge-aviso|Fundo translúcido rgba(245, 158, 11, 0.20) com borda luminosa no Dark Mode."
  "[STYLE] Concepção da classe semântica .badge-info|Criação de classe universal para estados informativos e KPIs (Alugado, Ativos)."
  "[STYLE] Estilização azul celeste Neon (#60A5FA) em .badge-info|Fundo translúcido rgba(59, 130, 246, 0.20) com borda luminosa no Dark Mode."
  "[STYLE] Concepção da classe semântica .badge-neutro|Criação de classe universal para registros arquivados ou finalizados (Concluído, PDF)."
  "[STYLE] Estilização prata grafite (#E5E7EB) em .badge-neutro|Fundo translúcido rgba(156, 163, 175, 0.20) com borda grafite no Dark Mode."
  "[STYLE] Adaptação simétrica de todas as badges para tema-claro.css|Garantia de cores pastéis suaves corporativas com tipografia escura no modo claro."
  "[STYLE] Padronização geométrica de badges com padding de 3px 8px|Uniformização de preenchimento e raio de 10px em todas as tabelas do sistema."
  "[REFACTOR] Eliminação de estilos inline estáticos nas células de status|Migração completa de setStyle() fixo para injeção limpa de classes CSS."
  "[A11Y] Conformidade de contraste WCAG AAA em todas as badges Neon|Garantia de leitura perfeita de todos os badges contra o fundo preto #0D0E12."
  "[UI] Adoção do glifo indicador circular '●' em todas as badges|Inclusão de ponto visual antes do texto para identificação rápida de status."

  # 6. Notificações Toasts & Animações Shake (81-95)
  "[FEAT] Sistema de notificações flutuantes não-bloqueantes notificarPopUp|Substituição de caixas Alert bloqueantes por toasts temporários no topo da janela."
  "[FEAT] Posicionamento centralizado superior dinâmico para Toasts|Cálculo automático de posição X central e offset Y de 45px na janela ativa."
  "[FEAT] Animação coordenada de entrada com ScaleTransition e FadeTransition|Efeito suave de zoom com fade-in na exibição do cartão de notificação."
  "[FEAT] Temporizador de permanência de 3 segundos para notificações|Pausa automática de exibição antes de acionar o desvanecimento de saída."
  "[FEAT] Transição suave de fade-out no encerramento do Toast|Remoção atômica do nó do popup ao concluir a animação de saída."
  "[STYLE] Estilização de alto contraste para Toasts no modo escuro|Fundo carvão #121318 com borda luminosa colorida e texto branco nítido."
  "[STYLE] Estilização limpa para Toasts no modo claro|Fundo branco puro com sombra projetada e bordas suaves no tema claro."
  "[UI] Ícones vetoriais de feedback no Toast (fas-check-circle e fas-exclamation)|Diferenciação semântica imediata entre notificações de sucesso e erro."
  "[FEAT] Microinteração mecânica animarErroShake estilo macOS|Criação de Timeline com 10 KeyFrames de oscilação lateral para indicar recusa."
  "[PERF] Amortecimento físico progressivo na animação de Shake|Variação de deslocamento de 8px a 0px simulando física mecânica realista."
  "[UX] Acoplamento de Shake em campos de formulários inválidos|Tremor automático de contêineres ao submeter campos obrigatórios em branco."
  "[UX] Acoplamento de Shake ao buscar registros inexistentes|Tremor da tabela ao tentar localizar IDs não cadastrados no banco."
  "[FEAT] Diálogos modais com transição coordenada de Scale e Fade|Padronização de animação de zoom suave para caixas de confirmação essenciais."
  "[FEAT] Métodos utilitários de entrada pedirTexto, pedirInteiro e pedirDouble|Encapsulamento de entradas de dados com tratamento preventivo de exceções."
  "[FEAT] Validação estrita de datas no padrão ISO com pedirData|Bloqueio de entradas fora da máscara AAAA-MM-DD com feedback imediato."

  # 7. Módulo de Categorias (96-107)
  "[REFACTOR] Migração da visualização legada de TextArea para TableView|Substituição de saída textual antiga por tabela organizada em MenuCategorias.fxml."
  "[UI] Política de redimensionamento CONSTRAINED_RESIZE_POLICY em Categorias|Ajuste responsivo automático das larguras das colunas preenchendo o espaço útil."
  "[FEAT] Coluna de status com badge dinâmico Ativa/Inativa|Aplicação de CellFactory com classes semânticas na tabela de categorias."
  "[FEAT] Filtro de pesquisa reativo em memória com FilteredList|Busca instantânea que avalia ID, Nome e Descrição da categoria em tempo real."
  "[FEAT] Debounce e listener dinâmico no campo de busca de categorias|Filtragem fluida sem necessidade de apertar botão de buscar ou Enter."
  "[UI] Botão com atalho de limpeza rápida aoLimparPesquisa|Restauração instantânea da visualização completa da lista de categorias."
  "[UX] Modal único estilo Sheet com GridPane para cadastro de categorias|Substituição de sequências de diálogos por formulário unificado em tela única."
  "[FIX] Blindagem de cadastro contra categorias com campos vazios|Validação de isBlank() com feedback de Shake ao submeter formulário incompleto."
  "[UX] Modal de edição com pré-carregamento dos dados selecionados|Abertura de formulário preenchido para atualização rápida de nome e descrição."
  "[UX] Confirmação com diálogo animado para desativação de categorias|Prevenção de desativação acidental de categorias de produtos."
  "[UI] Empty State ilustrado com ícone fas-tags em Categorias|Exibição de arte vetorial e instrução quando a busca não retorna resultados."
  "[FEAT] Toasts informativos em todas as operações de categorias|Confirmações flutuantes ao cadastrar, editar, desativar e recarregar categorias."

  # 8. Módulo de Fornecedores (108-120)
  "[REFACTOR] Remoção de exibição crua de booleanos true/false em Fornecedores|Eliminação de strings booleanas e adoção de badges visuais formatados."
  "[FEAT] Implementação de CellFactory com badges para status de fornecedor|Renderização dinâmica de ● Ativo (verde) e ● Inativo (vermelho) na coluna Ativo."
  "[FIX] Definição de minWidth de 110px para evitar corte no badge de status|Ajuste de largura mínima na coluna Ativo impedindo truncamento com reticências."
  "[FIX] Delimitação de largura de 45px a 65px na coluna ID de Fornecedores|Otimização de área útil na tabela transferindo espaço para nomes e e-mails."
  "[FIX] Correção de fundo branco da tabela de fornecedores no Dark Mode|Injeção forçada de -fx-control-inner-background #0D0E12 no controlador."
  "[STYLE] Cores Neon de alto contraste para badges de fornecedores|Adaptação visual dos badges de parceiros para leitura perfeita no modo escuro."
  "[UX] Modal unificado com GridPane para cadastro de novos fornecedores|Formulário limpo reunindo Razão Social, CNPJ, E-mail e Telefone em tela única."
  "[FIX] Validação estrita de campos em branco no cadastro de fornecedores|Bloqueio preventivo com Shake ao tentar registrar fornecedor sem dados."
  "[UX] Modal de atualização de parceiros comerciais com CNPJ travado|Permissão de edição de dados de contato mantendo integridade do identificador fiscal."
  "[UX] Diálogo de confirmação para desativação de fornecedores|Prevenção de encerramento indevido de parcerias com fornecedores ativos."
  "[FEAT] Pesquisa multicritério reativa por Razão Social, CNPJ ou ID|Filtro em memória com correspondência em tempo real conforme digitação."
  "[UI] Empty State ilustrado com ícone fas-truck para tabela vazia|Mensagem orientativa ilustrada quando não houver fornecedores cadastrados."
  "[FEAT] Toasts de feedback para o ciclo de vida completo de fornecedores|Avisos imediatos ao cadastrar, editar, desativar ou recarregar fornecedores."

  # 9. Módulo de Itens (121-133)
  "[FEAT] Lógica multicritério com 4 estados operacionais no módulo de Itens|Categorização inteligente entre Disponível, Alugado, Manutenção e Indisponível."
  "[STYLE] Badge Neon esmeralda ● Disponível para catálogo de itens|Identificação visual imediata de itens prontos para locação."
  "[STYLE] Badge Neon azul ● Alugado para itens em posse de clientes|Destaque cromático de itens atualmente retidos em contratos ativos."
  "[STYLE] Badge Neon âmbar ● Manutenção para itens em reparo|Sinalização de itens temporariamente retidos para revisão técnica."
  "[STYLE] Badge Neon vermelho ● Indisponível para itens inativados|Indicação de produtos descontinuados ou bloqueados no acervo."
  "[STYLE] Formatação monetária R$ alinhada à direita na Taxa Diária|Uso de NumberFormat pt-BR para padronização de moeda na coluna de diária."
  "[FEAT] Filtro composto reativo com ComboBox de escopo de busca em Itens|Opção de direcionar a pesquisa para Todos, Nome, ID ou Categoria."
  "[FEAT] Debounce e busca reativa encadeada na tabela de itens|Filtragem de registros sem recarga de tela através de FilteredList."
  "[REFACTOR] Remoção de botões redundantes de consulta na tela de itens|Eliminação de botões obsoletos dando prioridade à busca instantânea."
  "[UX] Modal com GridPane para edição de conservação e taxa diária de itens|Interface de ajuste rápido de preços e estado de conservação do acervo."
  "[UX] Desativação segura de itens com diálogo de confirmação|Bloqueio de exclusão acidental de itens com vínculo histórico no sistema."
  "[UI] Empty State ilustrado com ícone fas-boxes para tabela de itens|Exibição de gráfico vetorial quando nenhum item corresponder aos filtros."
  "[FEAT] Sincronização automática do status do item com contratos de aluguel|Atualização do estado de disponibilidade no momento da retirada e devolução."

  # 10. Módulo de Contratos (134-147)
  "[REFACTOR] Mapeamento da entidade ContratoAluguel na TableView de contratos|Tipagem estrita das colunas da tabela vinculadas aos atributos do contrato."
  "[FIX] Eliminação de fundos cinzas pastéis no badge Concluído de contratos|Substituição de estilos inline pelo padrão semântico badge-neutro."
  "[STYLE] Badge ● Concluído com cápsula grafite e tipografia prata|Garantia de leitura nítida sobre o fundo preto OLED no Dark Mode."
  "[STYLE] Badge ● Cancelado em vermelho Neon na tabela de contratos|Destaque imediato para contratos revogados ou cancelados."
  "[STYLE] Badge ● Atrasado com cálculo automático comparando data e hoje|Avaliação em tempo real comparando a data prevista com a data atual."
  "[STYLE] Badge ● Em Dia em verde Neon para contratos vigentes regulares|Identificação de contratos dentro do prazo acordado de devolução."
  "[STYLE] Formatação monetária R$ na coluna de valor total do aluguel|Exibição clara de valores financeiros na moeda corrente nacional."
  "[STYLE] Alinhamento centralizado para colunas de data de retirada e previsão|Padronização de formatação de datas facilitando a leitura vertical."
  "[FEAT] Modal de abertura de novo contrato com seletores DatePicker|Interface unificada para seleção de cliente, item e período de locação."
  "[FEAT] Bloqueio automático de locação para clientes com multas em aberto|Validação impeditiva prevenindo novos contratos para clientes inadimplentes."
  "[FEAT] Modal de devolução com cálculo automático de dias de atraso|Interface para registro da data real de entrega comparando com a prevista."
  "[FEAT] Geração automática de multa financeira em caso de devolução atrasada|Aplicação das regras de negócio de penalidade e registro imediato no backend."
  "[FEAT] Devolução e reativação automática do item no catálogo do sistema|Atualização do status do item para Disponível após conclusão do contrato."
  "[UI] Empty State ilustrado com ícone fas-file-signature em Contratos|Orientação visual quando não existirem contratos registrados no filtro."

  # 11. Módulo de Multas (148-158)
  "[STYLE] Formatação monetária R$ na coluna de valores de multas|Padronização contábil brasileira para visualização de débitos."
  "[STYLE] Badges Neon semânticas para identificação de multas Pagas e Pendentes|Diferenciação clara entre multas liquidadas (verde) e débitos ativos (âmbar)."
  "[FEAT] Filtro dinâmico multicritério na tela de gerenciamento de multas|Busca encadeada por ID de multa, contrato, tipo, descrição e status."
  "[UX] Modal unificado com ComboBox de tipos de infração para multas|Formulário com opções estruturadas (Atraso, Dano, Extravio, Mau Uso)."
  "[FEAT] Registro automático de carimbo de data no fuso America/Recife|Armazenamento da data de criação da multa alinhada ao horário do operador."
  "[FEAT] Consulta contextual rápida de multas por ID de contrato|Filtro imediato exibindo histórico de penalidades de uma locação específica."
  "[FEAT] Ação de listagem exclusiva de multas pendentes de quitação|Visão rápida para cobrança focando apenas em débitos em aberto."
  "[FEAT] Fluxo de liquidação e quitação de multas com diálogo de confirmação|Atualização do status da multa com notificação imediata de sucesso."
  "[FIX] Validação preventiva contra IDs não numéricos no módulo de multas|Tratamento de NumberFormatException com tremor mecânico Shake no input."
  "[UI] Empty State ilustrado com ícone fas-receipt para tabela de multas|Aviso amigável quando nenhuma multa for encontrada na consulta."
  "[FIX] Resolução de contraste e textos apagados no módulo de Multas|Remoção de travas de cores pretas em labels e células da tabela de multas."

  # 12. Módulo de Usuários & Correção de Crash (159-170)
  "[FIX] Correção de crash com IllegalArgumentException em Usuários|Eliminação de injeção duplicada de ComboBox no painel de cadastro de usuários."
  "[FIX] Inicialização segura com Platform.runLater em MenuUsuariosFuncionario|Garantia de que a montagem dos nós FXML esteja concluída antes da injeção de dados."
  "[FIX] Fallback resiliente no botão de Usuários do painel do Funcionário|Mecanismo de busca alternativo caso o FXML principal apresente incompatibilidade."
  "[FEAT] Badges dinâmicos ● Ativo e ● Inativo na tabela de usuários|Substituição de booleanos por pílulas coloridas formatadas com CSS."
  "[FEAT] Filtro de busca inteligente por Nome, ID, E-mail e CPF|Normalização de caracteres ignorando pontos e traços na busca por CPF."
  "[UX] Formulário unificado de cadastro com suporte a seleção de perfil|Interface para inclusão de novos operadores ou clientes no sistema."
  "[UX] Efeito visual temporário campo-invalido para campos obrigatórios|Destaque com borda vermelha em inputs vazios que desaparece em 2.5s."
  "[UI] Transições suaves de Fade entre painéis internos de usuários|Alternância fluida entre tabela de listagem, formulário e painel de busca."
  "[FEAT] Consulta detalhada de usuário por ID com exibição em painel dedicado|Card formatado com dados cadastrais completos e situação da conta."
  "[UI] Empty State ilustrado com ícone fas-search na tabela de usuários|Mensagem amigável com arte vetorial ao não localizar usuários na busca."
  "[STYLE] Uniformização de textos em branco no cadastro de usuários no Dark Mode|Ajuste de contraste de formulários garantindo legibilidade no modo escuro."
  "[ARCH] Desacoplamento da hierarquia entre controladores Admin e Funcionário|Reaproveitamento estrutural de código entre os painéis de usuários."

  # 13. Dashboards Admin/Funcionário & Polimento Final (171-180)
  "[FEAT] Implementação de KPI Badges dinâmicos no Dashboard do Administrador|Pílulas em tempo real para Usuários, Itens, Categorias, Parceiros, Contratos e Multas."
  "[FEAT] Replicação dos mesmos KPI Badges Neon no Dashboard do Funcionário|Equalização visual completa entre os painéis de Administrador e Funcionário."
  "[FEAT] Cálculo em tempo real de multas pendentes para o KPI de Multas|Exibição dinâmica de alerta com número de débitos ou badge 'Zeradas'."
  "[FEAT] Contagem dinâmica de itens do catálogo e contratos em aberto nos KPIs|Alimentação automática das informações operacionais direto do Facade."
  "[STYLE] Amarração elástica de largura dos cards com box.prefWidthProperty()|Dimensionamento fluído dos cartões do dashboard em qualquer tamanho de monitor."
  "[UI] Avatares circulares com extração dinâmica de iniciais do operador|Geração vetorial de iniciais no cabeçalho com cores diferenciadas por perfil."
  "[FEAT] Saudação contextual inteligente personalizada com o nome do operador|Mensagem de boas-vindas no primeiro acesso através de Toast flutuante."
  "[STYLE] Estilização de alto contraste para botão Encerrar Sessão no Dark Mode|Ajuste para padrão escuro com texto e borda vermelhos luminosos."
  "[UI] Transição FadeTransition suave na tela de Login|Entrada amortecida com fade-in ao abrir o aplicativo de locação."
  "[CHORE] Polimento estético universal OLED Pitch Black em todos os módulos|Homogeneização visual completa com badges Neon e alto contraste acessível."
)

total=${#issues[@]}
count=1

for item in "${issues[@]}"; do
  title="${item%%|*}"
  body="${item#*|}"
  
  echo "[$count/$total] Criando issue: $title"
  gh issue create --title "$title" --body "$body" > /dev/null
  
  count=$((count + 1))
  sleep 0.2
done

echo ""
echo "🎉 Concluído com sucesso! Todas as $total issues foram criadas no repositório."
