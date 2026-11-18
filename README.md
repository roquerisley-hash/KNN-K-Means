# Trabalho final de POO e Banco de Dados — Grupo 4

Projeto Java com 150 jogadores sintéticos em H2. O KNN prevê CASUAL, REGULAR ou RANQUEADO; o K-Means forma grupos sem usar esses perfis na decisão.

## Executar

É necessário um **JDK 11 ou superior**, com `java` e `javac` no PATH. A compilação usa `--release 11`; a validação foi feita com o JDK disponível no ambiente. O driver **H2 2.4.240** já está em `Projeto Integrado Verdadeiro/lib`. Não precisa instalar um servidor de banco, Maven, Gradle ou bibliotecas de ML.

Neste Windows, abra a raiz no VS Code e use o terminal integrado. O iniciador `.bat` evita o bloqueio de scripts do Windows PowerShell 5.1, sem alterar a política de execução:

```powershell
& '.\Projeto Integrado Verdadeiro\executar.bat' todos
```

Também é possível dar dois cliques em `Projeto Integrado Verdadeiro/executar.bat`: ele executa tudo e aguarda uma tecla ao terminar. Os modos são `todos`, `banco`, `knn`, `kmeans` e `testes`. Para outro jogador: `executar.bat knn 15 35 80 52`.

O `.ps1` foi corrigido e validado no PowerShell 7. Se sua sessão permitir scripts, na raiz do repositório:

```powershell
& '.\Projeto Integrado Verdadeiro\executar.ps1'
& '.\Projeto Integrado Verdadeiro\executar.ps1' -Modo banco
& '.\Projeto Integrado Verdadeiro\executar.ps1' -Modo knn
& '.\Projeto Integrado Verdadeiro\executar.ps1' -Modo kmeans
& '.\Projeto Integrado Verdadeiro\executar.ps1' -Modo testes
```

Sem modo, executa A, B e C. Para classificar outro jogador:

```powershell
& '.\Projeto Integrado Verdadeiro\executar.ps1' -Modo knn -Caracteristicas 15,35,80,52
```

Ordem: horas por semana, partidas por semana (inteiro), gasto mensal em reais e taxa de vitória de 0 a 100. Use ponto decimal. O exemplo padrão recebe REGULAR, com K = 5, distância euclidiana e min-max do treino.

O script compila sequencialmente, usa até 256 MiB de heap no compilador e 128 MiB no programa, e informa dois processadores disponíveis à JVM. Não faz testes de estresse. O banco é criado em memória a partir do SQL em cada execução; nada é gravado em um servidor externo.

As configurações portáveis de fontes, H2 e execução estão em `.vscode`. O botão **Gravar: projeto completo** foi preparado, mas sua execução pela interface ainda não foi verificada: o controle do computador recusou acesso ao VS Code. O comando `.bat` foi executado com sucesso no shell deste Windows. Veja [COMO_EXECUTAR_E_GRAVAR.md](COMO_EXECUTAR_E_GRAVAR.md) para os passos e [docs/VALIDACAO_LOCAL.md](docs/VALIDACAO_LOCAL.md) para a evidência e os limites da validação.

## Apresentação e estudo

- [Roteiro completo](ROTEIRO_APRESENTACAO.md): falas, arquivos e métodos reais, ações de tela, transições e checklist. Planejado para 7min45s por escolha do grupo; não foi encontrada duração oficial de vídeo nas fontes consultadas.
- [Guias dos cinco integrantes](guias_integrantes): Samuel, Davi, Milena, Gustavo e Risley, cada um com explicação passo a passo, exemplo, oito perguntas respondidas e conceitos a dominar.
- [Como executar e gravar](COMO_EXECUTAR_E_GRAVAR.md): caminho recomendado no Windows, alternativa Run preparada e recuperação de falhas.

## Compilar e executar manualmente

Dentro de `Projeto Integrado Verdadeiro`, no PowerShell:

```powershell
New-Item -ItemType Directory -Path bin -Force | Out-Null
$fontes = @(Get-ChildItem src -Filter '*.java' | ForEach-Object { $_.FullName })
javac -encoding UTF-8 --release 11 -cp 'lib/h2-2.4.240.jar' -d bin @fontes
java -Dfile.encoding=UTF-8 -Djava.awt.headless=true -cp 'bin;lib/h2-2.4.240.jar' TrabalhoFinal todos
```

O ponto de entrada recomendado é `TrabalhoFinal`. Seus modos são `todos`, `a`, `b` e `c`. Exemplo direto de KNN: `TrabalhoFinal b 15 35 80 52`. Em Linux/macOS, compile os mesmos fontes e use `:` no lugar de `;` no classpath da execução. O script `.ps1` foi preparado para Windows; a execução em outros sistemas não foi testada.

`App` e `AppKNN` continuam disponíveis como entradas de compatibilidade para A e B. Elas reutilizam os mesmos componentes; não são projetos ou modelos alternativos.

## Organização

| Local | Conteúdo |
|---|---|
| `Projeto Integrado Verdadeiro/src` | Única pasta de fontes de produção |
| `Projeto Integrado Verdadeiro/test` | Testes sem bibliotecas externas |
| `Projeto Integrado Verdadeiro/lib` | Driver H2 fornecido pelo grupo |
| `Projeto Integrado Verdadeiro/jogadores.sql` | Tabela e 150 jogadores |
| `Projeto Integrado Verdadeiro/resultados` | Saídas geradas a cada execução; ignoradas pelo Git |
| `docs/resultados` | Registro da execução validada nesta integração |

## Responsabilidades e origem

| Responsável no grupo | Classes e tarefas |
|---|---|
| Samuel Couto | `BancoDados`, `JogadorDAO`, `JogadorDAOJdbc`, consultas em `App`, SQL e interpretação final da Parte E |
| Davi Bicalho | `DivisaoDados`, `KNN`, `AppKNN` e integração dos componentes |
| Milena Roque | `NormalizadorMinMax`, `MedidaDeDistancia`, euclidiana/Manhattan, `AvaliacaoKNN` e `ExperimentosKNN` |
| Gustavo Guedes | Revisar e continuar a base em `KMeans` |
| Risley Roque | Revisar e continuar `ExperimentosKMeans`, `GraficoCotovelo` e resultados da Parte C |

Essa divisão identifica **responsabilidades, não a autoria individual de cada linha**. O commit inicial `56d4c7e` e o trabalho existente de Samuel, Milena e Davi foram preservados. A base de K-Means, seus experimentos e os ajustes desta integração foram produzidos com auxílio de um assistente e devem ser estudados e revisados pelos responsáveis. Os autores Git não foram trocados pelos nomes dos colegas.

As consultas SQL do Samuel foram mantidas. A inicialização foi centralizada em `BancoDados`, e `App.executarConsultas` permite usar a mesma conexão na execução completa. O DAO carrega os jogadores uma única vez para B e C. A única extensão do normalizador original, além do trabalho já existente, é `desnormalizar`, necessária para apresentar os centroides na escala original.

## Parte B — como funciona o KNN

Os dados são ordenados por ID e embaralhados com semente **42**: 120 no treino e 30 no teste. A lista original é preservada. O normalizador aprende mínimos e máximos **somente com o treino**. Teste e jogador novo usam esses mesmos parâmetros; características constantes viram zero. Valores fora dos extremos do treino podem ficar fora de [0,1].

O algoritmo usa as quatro características numéricas, calcula a distância até todos os jogadores de treino, ordena com `Comparable`/`Collections.sort`, seleciona K e conta os votos. ID e apelido não entram na distância.

**Desempate:** entre perfis com a maior votação, vence aquele cujo vizinho aparece primeiro na ordenação por distância crescente e ID crescente. O ID também resolve distâncias iguais no corte dos K vizinhos. KNN depende da interface `MedidaDeDistancia`; trocar euclidiana por Manhattan não muda o algoritmo.

Os experimentos medem K = 1, 3, 5, 7, 9 e 11 com euclidiana/min-max. Para comparar distância e normalização, K = **5 foi definido previamente**, sem escolhê-lo pelos acertos no teste. Todas as configurações usam a mesma divisão.

Saídas em `resultados/knn`:

- `acuracias.csv`, `matrizes.csv` e `divisao.csv`;
- `previsoes.csv` e `erros.csv`, com configuração, ID, apelido, real/previsto e características;
- `distribuicao_erros.csv`, com total de testes, acertos, erros e taxa por perfil real do modelo principal.

As matrizes têm perfil real nas linhas e previsto nas colunas, na ordem CASUAL, REGULAR, RANQUEADO. Acurácia é acertos/total; o CSV a apresenta em porcentagem. Os erros e suas características permitem ao Samuel preparar a Parte E. A interpretação final ainda deve ser escrita pelo grupo.

## Parte C — base para Gustavo e Risley

`KMeans` recebe apenas uma matriz de números, **sem perfis reais**. Sorteia índices distintos de pontos para os centroides iniciais, atribui cada ponto ao centroide mais próximo e recalcula as médias. Empate de distância usa o menor índice de grupo.

Se um grupo ficar vazio, move para ele o ponto mais distante de um grupo com pelo menos dois integrantes e depois recalcula as médias. Essa regra funciona também com pontos repetidos. O algoritmo para quando as atribuições, após esse tratamento, não mudarem ou quando atingir **100 iterações**. O resultado informa se convergiu; parar pelo limite não é apresentado como convergência.

Os experimentos usam **7, 21, 42, 84 e 123** como sementes. Para cada K, ficam com a menor inércia observada; no empate, mantêm a primeira semente na ordem da lista. São 30 inicializações normalizadas para K = 1 a 6 e cinco sem normalização para K = 3, totalizando 35. O min-max da Parte C é ajustado nos **150 jogadores**, pois aqui não há treino/teste.

Saídas em `resultados/kmeans`:

- `inicializacoes.csv`: todas as sementes, inércias, iterações e situação de convergência;
- `cotovelo.csv` e `cotovelo.png`: menor inércia por K, com normalização;
- `resumo_k3.csv`: comparação de K = 3 com e sem min-max;
- `grupos_normalizado.csv` e `grupos_original.csv`: tamanhos e contagens por perfil;
- `centroides_normalizado.csv` e `centroides_original.csv`: ambos apresentados na escala original;
- `atribuicoes_normalizado.csv` e `atribuicoes_original.csv`: grupo de cada jogador.

O gráfico usa `Graphics2D`, `BufferedImage` e `ImageIO`, do próprio JDK, sem abrir janela. A pureza é a soma da maior contagem de perfil em cada grupo, dividida por 150. Os perfis só entram nessa avaliação posterior. **Grupo 1, 2 ou 3 não é automaticamente CASUAL, REGULAR ou RANQUEADO.**

Não compare numericamente inércias entre escalas distintas como se fossem a mesma unidade. A comparação com/sem normalização deve observar também a pureza, os tamanhos e a composição dos grupos. As cinco sementes não garantem um mínimo global; a regra escolhe a melhor solução entre as tentativas executadas.

Para continuar sem quebrar o projeto, Gustavo deve manter o contrato `KMeans.executar(double[][])` e os resultados encapsulados; Risley deve trabalhar nos experimentos e gráficos sem copiar a lógica de atribuição/recalculo do algoritmo. Alterações necessárias nesse contrato devem ser combinadas e acompanhadas dos testes.

## Validação e resultados

Foram executadas 171 verificações do KNN e 797 do K-Means, com compilação para Java 11, além da execução completa de A/B/C. O número inclui comparações numéricas dos 150 jogadores; não representa centenas de treinamentos. Uma conferência independente em Python/SQLite validou os CSVs e métricas sem repetir a busca de K-Means.

| Experimento | Resultado desta execução |
|---|---|
| KNN K=5, euclidiana, min-max | 29/30 — 96,67% |
| KNN K=5, euclidiana, sem normalização | 26/30 — 86,67% |
| KNN K=5, Manhattan, min-max | 28/30 — 93,33% |
| KNN K=5, Manhattan, sem normalização | 26/30 — 86,67% |
| K-Means K=3, min-max | Pureza 80,00%; inércia 10,554837; semente 84 |
| K-Means K=3, sem normalização | Pureza 52,67%; inércia 256433,191194; semente 84 |

Todas as 35 inicializações convergiram; a maior usou 15 iterações. Os cinco CSVs anteriores do KNN permaneceram idênticos. O erro do modelo principal é o jogador 130, CrimsonNinja12, real RANQUEADO e previsto REGULAR. Isso é um resultado, não uma explicação causal para o erro.

Veja o registro em [docs/resultados](docs/resultados), incluindo consultas A, tabelas, gráfico e `validacao.json`. Os arquivos são UTF-8, delimitados por ponto e vírgula e usam ponto decimal. Os snapshots publicados não são atualizados automaticamente: após mudanças, compare as novas saídas antes de substituí-los.

## O que falta ao grupo

- Gustavo e Risley: estudar, revisar e validar a base criada para suas responsabilidades, incluindo tratamento de grupo vazio, inicialização, gráfico e interpretação da pureza.
- Samuel: interpretar os erros da Parte E com apoio nos números e incorporar as consultas da Parte A ao relatório.
- Davi e Milena: revisar os parâmetros e conseguir demonstrar classificação, distâncias, votação, normalização e métricas.
- Todos: montar o relatório oficial em Google Docs, de 4 a 7 páginas, com seção da Parte E de 1 a 2 páginas e nomes dos integrantes. Este README não substitui esse relatório. Quando um experimento da pesquisa exigir repetição, seguir a exigência do enunciado de pelo menos 20 divisões, média e desvio padrão; essa pesquisa não foi executada nesta integração.

As sete etapas da integração têm commits reais: compartilhamento do banco, complementos da Milena, consolidação do KNN, base do K-Means, experimentos da Parte C, aplicação integrada e documentação/testes. O histórico anterior foi mantido. A branch deve passar pela revisão do grupo antes de ser integrada à main.
