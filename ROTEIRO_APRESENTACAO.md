# Roteiro de gravação — Grupo 4

## Regras confirmadas e escolhas do grupo

Conferência no Classroom em **18/11/2026**: foram consultadas a entrega final de Banco de Dados, a atividade Trabalho Trimestral 3 de POO, as listas de atividades e os avisos recentes das duas turmas. O PDF oficial também foi conferido novamente. **Não foram localizadas nesses materiais instruções específicas de duração, câmera, formato de vídeo ou avaliação do vídeo.** Não atribuímos regras não encontradas às professoras.

- Duração planejada: **7min45s**, ajustável no ensaio entre 5 e 10 minutos. É uma escolha nossa, não uma duração oficial confirmada.
- Todos os cinco integrantes falam por decisão do roteiro. Não foi confirmada obrigação de todos aparecerem com câmera.
- Plano: gravação de tela com voz. Mostrar código e execução é a nossa forma de demonstrar o trabalho, não uma exigência adicional inventada.
- Não foi confirmado MP4, link do YouTube/Drive, tamanho máximo ou prazo separado para o vídeo. Não enviar a um destino presumido.
- A [entrega da Camille](https://classroom.google.com/c/ODQ1ODQ1ODc3MDg0/a/OTAwMzA0MDIxNjc3/details) mostrava prazo de **18/11/2026**, sem instruções descritivas de vídeo. A [atividade da Virgínia](https://classroom.google.com/c/ODM4MDgyMzYzMjE5/a/ODg5NzMzNjMyNTkz/details) mostrava **16/11/2026**. Não há confirmação de prorrogação. O aviso de cronograma da Camille, no [mural](https://classroom.google.com/c/ODQ1ODQ1ODc3MDg0), situava apresentação na semana de 16/11, sem detalhar vídeo.
- O [PDF oficial](https://drive.google.com/file/d/1T6qOE7EE8J5s6Kj5kUMQoRvfijAA9zWW/view) exige código, resultados e relatório em Google Docs; este roteiro não substitui o relatório nem confirma a forma de envio do vídeo.

Se uma orientação oficial adicional chegar, ela tem prioridade sobre essas escolhas. Não publique gravações antes de conferir o destino indicado pelas professoras.

## Sequência e tempo

| Tempo | Participante | Duração | Conteúdo |
|---|---|---|---|
| 0:00–1:10 | Samuel | 1min10s | Contexto, H2, DAO, consultas e execução completa |
| 1:10–2:35 | Davi | 1min25s | Divisão, KNN, vizinhos, voto e jogador novo |
| 2:35–3:55 | Milena | 1min20s | Min-max, distâncias, acurácia e matriz |
| 3:55–4:20 | Samuel | 25s | Erro real da Parte E e limite da análise |
| 4:20–5:55 | Gustavo | 1min35s | Iterações, centroides, parada e grupos vazios |
| 5:55–7:45 | Risley | 1min50s | Sementes, inércia, cotovelo, pureza e conclusão |

Samuel participa por 1min35s no total. Os tempos incluem as ações de tela; não é necessário falar rápido para ler cada número. Ensaiar e ajustar as pausas.

## Preparação da tela

Abra a **raiz do repositório**, que contém este documento, o README e a pasta `Projeto Integrado Verdadeiro`. Todos os caminhos a seguir são relativos a essa raiz. A aplicação é `TrabalhoFinal`, não um projeto novo em outra pasta.

No VS Code, prepare o terminal na raiz e o comando `& '.\Projeto Integrado Verdadeiro\executar.bat' todos`. Esse iniciador foi validado neste Windows. A configuração **“Gravar: projeto completo”** também está preparada, com saída no **Console de Depuração**, mas o botão ainda não foi testado visualmente devido ao bloqueio do controle do computador. Use essa alternativa somente depois de conferi-la antes da gravação. Os passos estão em `COMO_EXECUTAR_E_GRAVAR.md`.

Execute **uma única vez durante a apresentação**, no trecho de Samuel. Essa execução produz as saídas de A/B/C que os outros integrantes mostrarão. Não é necessário recompilar ou repetir todos os experimentos em cada troca de pessoa. Deixe abertos os arquivos de resultados depois que a execução terminar.

## Samuel — 0:00 a 1:10

**Fala completa**

> Olá! Somos o Grupo 4: Samuel Couto, Davi Bicalho, Milena Roque, Gustavo Guedes e Risley Roque. Nosso trabalho junta Programação Orientada a Objetos e Banco de Dados. Usamos 150 jogadores sintéticos, com horas de jogo, partidas, gasto mensal e taxa de vitória. Cada jogador também tem um perfil conhecido.
>
> Na parte de banco, usamos o H2 dentro do Java, em memória. O arquivo jogadores.sql cria a tabela e insere os dados automaticamente. Não precisamos de um servidor separado. BancoDados abre a conexão e confere os 150 registros.
>
> O acesso aos jogadores passa pela interface JogadorDAO e pela implementação JDBC. Ela usa PreparedStatement e transforma cada linha em um objeto Jogador. As três consultas mostram a quantidade por perfil, as médias e quem joga mais de vinte horas com vitória acima de cinquenta e cinco por cento.
>
> Vou executar agora o projeto completo. Aqui aparecem os 150 registros e cinquenta jogadores de cada perfil. O Davi vai mostrar como esses dados entram no KNN.

**Na tela, em ordem**

1. 0:00: mostrar rapidamente os nomes no README; não ler o documento inteiro.
2. 0:10: abrir `Projeto Integrado Verdadeiro/jogadores.sql`; destacar `CREATE TABLE jogador` e o formato dos INSERTs, sem percorrer 150 linhas.
3. 0:20: abrir `Projeto Integrado Verdadeiro/src/BancoDados.java`, métodos `abrir` e `carregar`; mostrar `jdbc:h2:mem:grupo4`, RUNSCRIPT e COUNT.
4. 0:30: abrir `Projeto Integrado Verdadeiro/src/JogadorDAOJdbc.java`, método `listarTodos`; apontar PreparedStatement, `while (rs.next())` e `montarJogador`.
5. 0:40: abrir `Projeto Integrado Verdadeiro/src/App.java`, método `executarConsultas`; apontar as três consultas, sem explicar cada comando de impressão.
6. 0:45: executar `& '.\Projeto Integrado Verdadeiro\executar.bat' todos` no terminal, na raiz. Esperar o encerramento; subir até `Número de linhas: 150` e as três contagens de 50. Mostrar as médias e o cabeçalho da consulta 3 brevemente. Se o grupo já tiver conferido o botão, pode substituí-lo pela configuração **Gravar: projeto completo** e **Ctrl+F5**, lendo a saída no Console de Depuração. Nas ações seguintes, “console” significa o painel escolhido para essa única execução.

**Transição:** “O Davi vai mostrar como esses dados entram no KNN.”

## Davi — 1:10 a 2:35

**Fala completa**

> Minha responsabilidade é a classificação com KNN. Primeiro, DivisaoDados faz uma cópia da lista, ordena por ID e embaralha com semente quarenta e dois. Assim, a divisão é reproduzível: cento e vinte jogadores ficam no treino e trinta no teste. Os jogadores de teste não entram na escolha dos vizinhos.
>
> O KNN recebe o treino, o valor de K, uma medida de distância e a opção de normalizar. Ele depende da interface MedidaDeDistancia, então podemos trocar a fórmula sem reescrever o algoritmo.
>
> Em encontrarVizinhos, calculamos a distância da consulta para todos os jogadores do treino. Ordenamos os resultados e ficamos com os K primeiros. O compareTo usa primeiro a distância e depois o ID para resolver distâncias iguais.
>
> No método classificar, cada vizinho dá um voto ao seu perfil. Ganha o perfil mais frequente. Se houver empate de votos, ganha o perfil empatado cujo vizinho aparece primeiro nessa lista ordenada.
>
> Aqui usamos K igual a cinco. O jogador novo tem quinze horas, trinta e cinco partidas, gasto de oitenta reais e cinquenta e dois por cento de vitórias. O resultado real foi REGULAR. Os cinco vizinhos mostrados também são REGULAR. A Milena vai explicar a preparação das características e os resultados dos testes.

**Na tela, em ordem**

1. Abrir `Projeto Integrado Verdadeiro/src/DivisaoDados.java`; destacar o construtor, `Collections.shuffle` e as duas sublistas.
2. Abrir `Projeto Integrado Verdadeiro/src/KNN.java`; mostrar o construtor recebendo `MedidaDeDistancia` e o ajuste do normalizador com `this.treino`.
3. Destacar `encontrarVizinhos`, especialmente o cálculo no laço, `Collections.sort` e `subList(0, k)`.
4. No mesmo arquivo, mostrar `Vizinho.compareTo` e depois `classificar`, nos laços de votos e de desempate.
5. No console da execução já feita, mostrar `Semente: 42 | Treino: 120 | Teste: 30`, o vetor `[15.0, 35.0, 80.0, 52.0]`, `Perfil previsto: REGULAR` e os IDs 75, 62, 37, 122 e 88. **Não rodar novamente só para repetir a mesma saída.**

**Transição:** “A Milena vai explicar a preparação das características e os resultados dos testes.”

## Milena — 2:35 a 3:55

**Fala completa**

> As características têm escalas diferentes. O gasto mensal pode chegar a centenas, enquanto as horas ficam em dezenas. Sem normalização, uma característica pode dominar a distância pelo tamanho dos números.
>
> Em NormalizadorMinMax, encontrarMinMax procura o mínimo e o máximo de cada coluna. No KNN, recebe somente o treino. Normalizar aplica valor menos mínimo, dividido por máximo menos mínimo. Os mesmos parâmetros são usados no teste. Se máximo e mínimo forem iguais, retornamos zero para não dividir por zero.
>
> A distância euclidiana soma os quadrados das diferenças e tira a raiz. A Manhattan soma as diferenças em valor absoluto. As duas implementam o mesmo método calcular.
>
> Nos experimentos, usamos os mesmos trinta jogadores de teste para todos os K e comparações. Com K igual a cinco, euclidiana e normalização, foram vinte e nove acertos em trinta, ou noventa e seis vírgula sessenta e sete por cento. Sem normalização, foram vinte e seis acertos. A Manhattan normalizada teve vinte e oito.
>
> Na matriz, as linhas são os perfis reais e as colunas são as previsões. A diagonal soma vinte e nove. O Samuel vai mostrar qual jogador ficou fora dela.

**Na tela, em ordem**

1. Abrir `Projeto Integrado Verdadeiro/src/NormalizadorMinMax.java`, métodos `encontrarMinMax` e `normalizar`; apontar a fórmula e o ramo da coluna constante.
2. Abrir `Projeto Integrado Verdadeiro/src/DistanciaEuclidiana.java` e `DistanciaManhattan.java`, método `calcular` de cada uma; mostrar a interface `MedidaDeDistancia.java` rapidamente.
3. Abrir `Projeto Integrado Verdadeiro/src/AvaliacaoKNN.java`; destacar `matriz[real][previsto]` no construtor e `getAcuracia`.
4. Mostrar o bloco de acurácias no console e a matriz do modelo principal. Como alternativa legível, abrir `Projeto Integrado Verdadeiro/resultados/knn/acuracias.csv` e `matrizes.csv`. Se os resultados ainda não existirem, a execução não foi concluída: não usar um arquivo vazio como resultado.

**Transição:** “O Samuel vai mostrar qual jogador ficou fora da diagonal.”

## Samuel — retorno da Parte E — 3:55 a 4:20

**Fala completa**

> No modelo principal, CrimsonNinja12 era RANQUEADO e foi previsto como REGULAR. O CSV mostra suas características: dezoito vírgula uma horas, setenta e quatro partidas, gasto zero e cinquenta e seis vírgula oito por cento de vitórias. Um caso não prova uma causa geral. O Gustavo vai apresentar o agrupamento, que não usa os perfis para formar os grupos.

**Na tela**

1. Abrir `Projeto Integrado Verdadeiro/resultados/knn/erros.csv`; mostrar a linha com configuração **euclidiana_normalizada**, ID 130 e os dois perfis. As demais linhas pertencem a outras configurações.
2. Se necessário, abrir `distribuicao_erros.csv`: é o resumo **somente do modelo principal**. Não somar as linhas de configurações diferentes como pessoas distintas.
3. Não afirmar que gasto zero “causou” o erro: isso não foi demonstrado pelo experimento.

**Transição:** “O Gustavo vai apresentar o agrupamento, que não usa os perfis para formar os grupos.”

## Gustavo — 4:20 a 5:55

**Fala completa**

> O K-Means resolve outra pergunta. Em vez de prever um perfil, ele tenta formar grupos de jogadores parecidos. A classe recebe só uma matriz de números. Os perfis reais não entram no algoritmo.
>
> Primeiro, validamos os dados e sorteamos os centroides iniciais entre os pontos existentes. Usamos índices diferentes e uma semente para conseguir repetir o sorteio. Depois começa a repetição.
>
> Em atribuirGrupos, cada jogador vai para o centroide mais próximo. Usamos a distância euclidiana ao quadrado. Não precisamos tirar a raiz para comparar, porque a ordem das distâncias continua a mesma.
>
> Em calcularCentroides, somamos cada característica dos integrantes e dividimos pelo tamanho do grupo. Assim, o novo centroide é a média das quatro características.
>
> Pode acontecer de um grupo ficar vazio. Nesse caso, movemos para ele o ponto mais distante de um grupo que tenha pelo menos dois integrantes. Depois recalculamos as médias, sem dividir por zero.
>
> A execução para quando os grupos não mudam mais depois desse tratamento, ou quando chega a cem iterações. O resultado informa se realmente convergiu. Na nossa execução, todas as inicializações convergiram, e a maior usou quinze iterações. O Risley vai mostrar como comparamos essas execuções e avaliamos os grupos.

**Na tela, em ordem**

1. Abrir `Projeto Integrado Verdadeiro/src/KMeans.java`, método `executar`; mostrar a matriz `double[][]`, os grupos anteriores e o `while` com limite.
2. Mostrar `sortearCentroides`, destacando semente, embaralhamento e cópia dos pontos.
3. Mostrar `atribuirGrupos` e `distanciaQuadrada`.
4. Mostrar `calcularCentroides`, especialmente soma e divisão por tamanho.
5. Mostrar `tratarGruposVazios`, destacando `tamanhos[grupos[i]] > 1`.
6. Voltar a `Arrays.equals(anteriores, grupos)` e à criação de `Resultado`. Não confundir limite de iterações com convergência.

**Transição:** “O Risley vai mostrar como comparamos essas execuções e avaliamos os grupos.”

## Risley — 5:55 a 7:45

**Fala completa**

> Nos experimentos do K-Means, usamos todos os cento e cinquenta jogadores para ajustar o min-max, porque aqui não existe separação entre treino e teste. Executamos cinco sementes para cada configuração e escolhemos a menor inércia observada. Isso reduz a dependência de um único sorteio, mas não garante a melhor solução possível entre todas as inicializações.
>
> A inércia soma as distâncias ao quadrado de cada jogador até o centroide do seu grupo. Para o cotovelo, testamos K de um a seis, com normalização. Neste gráfico, a queda é mais forte de um para dois, e depois os ganhos diminuem. O gráfico ajuda a discutir o número de grupos; não prova sozinho um K ideal. Também apresentamos K igual a três, como o enunciado pede.
>
> Para avaliar os grupos, só agora cruzamos o resultado com os perfis reais. A pureza soma a maior contagem de perfil de cada grupo e divide pelo total de jogadores. Com normalização, as maiores contagens foram trinta e um, trinta e nove e cinquenta. Somadas, dão cento e vinte de cento e cinquenta: oitenta por cento.
>
> Sem normalização, a pureza foi cinquenta e dois vírgula sessenta e sete por cento. Os centroides dos dois cenários são apresentados na escala original. Não comparamos diretamente os valores de inércia entre essas escalas diferentes.
>
> Assim, mostramos a leitura pelo banco, a classificação por vizinhos e o agrupamento por centroides. Os resultados podem ser reproduzidos com as sementes registradas e usados no relatório do grupo. Obrigado!

**Na tela, em ordem**

1. Abrir `Projeto Integrado Verdadeiro/src/ExperimentosKMeans.java`, métodos `executar` e `melhorInicializacao`; mostrar sementes 7, 21, 42, 84 e 123, limite 100 e comparação de inércias.
2. Abrir `Projeto Integrado Verdadeiro/resultados/kmeans/cotovelo.png` e deixá-lo visível durante a explicação do gráfico. O PNG é gerado por `GraficoCotovelo.salvar`, usando apenas o JDK.
3. Abrir `Projeto Integrado Verdadeiro/resultados/kmeans/grupos_normalizado.csv`; apontar tamanhos 42, 57 e 51 e maiores contagens 31, 39 e 50.
4. No código, mostrar `contarPerfis` e `calcularPureza`, sem sugerir que os perfis orientaram o agrupamento.
5. Abrir `resumo_k3.csv` e destacar purezas 0.8000000000 e 0.5266666667. São frações, correspondentes a 80,00% e 52,67%.
6. Abrir brevemente `centroides_normalizado.csv`; explicar que o nome identifica o experimento, mas os números já foram convertidos para a escala original em `salvarCentroides`.
7. Concluir com o gráfico ou com o README, sem rodar testes novamente.

**Transição final:** “Obrigado!”

## Checklist de gravação

- [ ] Abrir a raiz correta do clone, não a antiga pasta de entrega.
- [ ] Ensaiar as falas; os nomes representam responsabilidades, não uma afirmação de autoria de cada linha. O README registra a origem da base.
- [ ] Deixar este roteiro em uma segunda janela ou dispositivo, fora da área capturada.
- [ ] Preparar os arquivos Java por participante e o comando `.bat` no terminal. Se optar por “Gravar: projeto completo”, conferir o botão antes da gravação.
- [ ] Ter a pasta `resultados` acessível; `docs/resultados` é apenas o snapshot anterior, não o local atualizado pelo programa.
- [ ] Conferir microfone, tamanho da fonte e captura da janela. A configuração de câmera é uma escolha enquanto não houver instrução oficial.
- [ ] Ocultar conversas, dados pessoais, notificações e janelas sem relação com o trabalho.
- [ ] Fazer apenas uma execução completa durante a demonstração; aguardar o encerramento antes de navegar nos resultados.
- [ ] Não ler todas as 32 linhas da consulta 3 nem as 35 inicializações; destacar o que sustenta a explicação.
- [ ] Explicar que os números valem para as sementes e dados usados; não prometer desempenho geral.
- [ ] Conferir duração após ensaio e destino oficial de envio antes de publicar. Nenhum vídeo foi gravado ou entregue automaticamente por esta preparação.
