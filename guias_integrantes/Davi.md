# Guia de estudo — Davi

## 1. Sua responsabilidade

Explicar como o KNN usa jogadores conhecidos para prever o perfil de um jogador novo. Você também precisa entender a divisão treino/teste e como sua parte usa as distâncias e a normalização da Milena. A divisão de tarefas é uma responsabilidade de estudo e apresentação; o README registra a origem do código.

## 2. Arquivos que precisa conhecer

Todos estão em `Projeto Integrado Verdadeiro/src/`:

- `DivisaoDados.java`: separa treino e teste.
- `KNN.java`: prepara treino, procura vizinhos e vota; inclui `Vizinho`.
- `AppKNN.java`: lê o exemplo novo, executa experimentos e apresenta previsão/vizinhos.
- `TrabalhoFinal.java`: entrada principal que conecta banco, Parte B e Parte C.
- `Jogador.java`, `Perfil.java`, `MedidaDeDistancia.java`: contratos que o KNN utiliza.

## 3. Código passo a passo

### Como os dados entram

Em `TrabalhoFinal.main`, `BancoDados.abrir()` inicializa o H2. Uma variável `JogadorDAO` recebe `JogadorDAOJdbc`; `listarTodos()` devolve a lista de 150 objetos. Essa lista é passada a `AppKNN.executar`. O algoritmo não consulta SQL: recebe a lista já carregada.

`AppKNN.lerCaracteristicas` usa `[15, 35, 80, 52]` por padrão ou converte quatro argumentos. Confere limites e exige partidas inteiras. A consulta é um `double[]`, porque não conhecemos seu perfil e `Jogador` exige um perfil conhecido. Não inventamos um rótulo só para criar o jogador novo.

### Separação dos dados

O construtor de `DivisaoDados` copia a lista para não alterar o DAO. Ordena por ID antes de `Collections.shuffle(..., new Random(semente))`; assim, a divisão não depende da ordem em que os mesmos IDs chegaram. Com semente 42, 80% viram treino e o restante, teste: 120 e 30. Não há estratificação: esta divisão tem 9 CASUAL, 5 REGULAR e 16 RANQUEADO no teste, não dez de cada.

### Preparação do KNN

O construtor recebe treino, K, `MedidaDeDistancia` e a opção de normalizar. Rejeita treino vazio, K fora de 1 até o tamanho do treino e distância ausente. Guarda uma cópia do treino e valida os números.

Quando há normalização, cria seu próprio `NormalizadorMinMax`, ajusta com `this.treino` e prepara os vetores do treino. `preparar` transforma também cada consulta com esses mesmos extremos. Nenhum mínimo/máximo é aprendido com teste ou jogador novo.

### Vizinhos e ordenação

`encontrarVizinhos` valida quatro números finitos, prepara a consulta e percorre todos os jogadores de treino. Para cada um, chama `distancia.calcular` e cria um `Vizinho` com o jogador original e a distância. Não entra ID, apelido ou perfil no vetor de características.

`Collections.sort(vizinhos)` chama `Vizinho.compareTo`: menor distância primeiro; em igualdade, menor ID primeiro. Depois `subList(0, k)` pega exatamente K e `List.copyOf` protege a lista retornada. Não significa que a classificação usa apenas K cálculos: ela calcula contra todo o treino e depois escolhe K.

### Votação e retorno

`classificar` recebe essa lista ordenada. Cria um array de contagens com uma posição por `Perfil`. `ordinal()` fornece o índice: CASUAL, REGULAR ou RANQUEADO. Cada vizinho acrescenta um voto ao seu perfil, e `maiorVotacao` acompanha o maior total.

O segundo laço percorre os vizinhos na ordem de proximidade e retorna o primeiro perfil que tenha o maior total. Isso resolve empate de votos entre perfis. Se também há distância igual, o ID já definiu a ordem. A escolha não depende de HashMap nem de sorteio.

`AppKNN.mostrarClassificacao` imprime o perfil e os vizinhos. A API recebe `MedidaDeDistancia`, então euclidiana e Manhattan podem ser trocadas sem editar `KNN`.

## 4. Exemplo prático

**Execução real do projeto:** com K=5, euclidiana e min-max do treino, `[15,35,80,52]` recebeu REGULAR. Os IDs dos vizinhos foram 75, 62, 37, 122 e 88; todos têm esse perfil.

**Exemplo reduzido ilustrativo de empate:** três vizinhos ordenados têm perfis CASUAL, REGULAR e RANQUEADO. Cada perfil tem um voto. Nosso código devolve CASUAL, por estar no vizinho mais próximo. Se as distâncias forem iguais, vem antes o menor ID. K ímpar só impede empate entre duas classes em uma votação sem outros rótulos; com três perfis, ainda pode empatar.

## 5. Perguntas possíveis e respostas

1. **Por que separar treino e teste?** Para medir previsões sobre jogadores que não participaram da escolha dos vizinhos. Usar o próprio treino como teste pode inflar os acertos, especialmente com K=1.
2. **O KNN aprende uma fórmula durante o treino?** Não. Guarda exemplos e, aqui, ajusta a escala. As distâncias e a votação acontecem na consulta.
3. **Por que usar semente fixa?** Para repetir a mesma divisão e comparar configurações sobre os mesmos jogadores. Isso não garante que outra divisão tenha a mesma acurácia.
4. **Por que não colocar ID na distância?** O número é um identificador, não uma medida de comportamento. Ele só é usado para uma ordenação determinística em distâncias iguais.
5. **Qual a diferença entre ordenar e votar?** Ordenar escolhe os mais próximos; votar escolhe o perfil mais frequente entre eles. O perfil do vizinho mais próximo sozinho não vence sempre.
6. **Como o empate é resolvido?** Maior número de votos; entre os empatados, primeiro perfil na lista ordenada por distância e ID.
7. **Por que K=5 se outros K acertaram mais?** Foi definido previamente para as comparações, como opção intermediária. Não estamos afirmando que ele é ótimo nem escolhendo o parâmetro pelos acertos do teste.
8. **Que princípios de POO aparecem?** DAO separa banco; `MedidaDeDistancia` permite polimorfismo; atributos privados e cópias protegem os dados; métodos dividem as etapas sem novos frameworks.

## 6. O que dominar

Consiga seguir a lista do DAO até a previsão e explicar, sem ler cada linha, `DivisaoDados`, `encontrarVizinhos`, `compareTo` e `classificar`. Saiba diferenciar rótulo de característica, maioria de proximidade e avaliação de consulta nova. Para demonstrar, use o modo `knn` do iniciador. A configuração **Gravar: KNN (Partes B e E)** é uma alternativa preparada, ainda pendente de teste visual; veja `COMO_EXECUTAR_E_GRAVAR.md`.
