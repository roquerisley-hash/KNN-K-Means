# Guia de estudo — Risley

## 1. Responsabilidade

Explicar os experimentos e avaliar os grupos produzidos pelo K-Means. Sua parte utiliza o algoritmo de Gustavo, sem copiar a atribuição de pontos ou o recálculo de centroides. O README registra que a base foi preparada com auxílio de um assistente para revisão e continuidade dos responsáveis.

## 2. Arquivos utilizados

- `Projeto Integrado Verdadeiro/src/ExperimentosKMeans.java`.
- `Projeto Integrado Verdadeiro/src/GraficoCotovelo.java`.
- `src/KMeans.java` e `src/NormalizadorMinMax.java`, no mesmo projeto, como componentes utilizados.
- `Projeto Integrado Verdadeiro/resultados/kmeans/`: CSVs e gráfico atuais.

## 3. Experimentos passo a passo

### Preparação

`executar(List<Jogador>, Path)` cria a pasta de saída e um normalizador próprio. Ajusta com todos os 150 jogadores e monta duas matrizes: original e normalizada. No K-Means não há treino/teste; os perfis dos objetos não entram nessas matrizes numéricas.

O limite é 100 iterações. As sementes são 7, 21, 42, 84 e 123. Para K=1 até 6, chama `melhorInicializacao` com a matriz normalizada. Depois faz o cenário sem normalização com K=3. São 6×5+5=35 inicializações, não uma busca ilimitada.

### Seleção pela inércia

`melhorInicializacao` cria um KMeans para cada semente, executa e grava inércia, iterações e convergência em `inicializacoes.csv`. Compara `atual.getInercia() < melhor.getInercia()`. Se houver empate, mantém a primeira semente da ordem definida.

A inércia é `Σ distânciaQuadrada(ponto, centroide do grupo)`. Menor indica maior compactação **na mesma escala e no mesmo conjunto**, mas aumentar K normalmente facilita reduzir essa soma. Não basta escolher a menor inércia entre todos os K e concluir que o maior K é ideal.

### Cotovelo e gráfico

`cotovelo.csv` guarda a menor inércia observada em cada K normalizado. `GraficoCotovelo.salvar` usa BufferedImage, Graphics2D e ImageIO, todos do JDK: desenha eixos, valores, linha e pontos e grava `cotovelo.png`. Não usa biblioteca de ML nem abre uma janela gráfica.

O gráfico ajuda a procurar onde os ganhos de compactação começam a diminuir. Nesta execução, a maior queda é de K=1 para 2; isso é uma leitura visual, não a prova de um K universalmente correto. O enunciado pede a apresentação detalhada com K=3 de qualquer forma.

### Comparação com perfis e pureza

`salvarK3` recebe o resultado já pronto. Só então `contarPerfis` cruza o grupo de cada jogador com seu perfil verdadeiro. Cada linha representa um grupo e as três colunas representam CASUAL, REGULAR e RANQUEADO. Esse uso posterior dos perfis não contamina a formação dos grupos.

`calcularPureza` soma a maior contagem de cada linha e divide pelo total de jogadores. Pureza não é a mesma acurácia do KNN: os grupos não nasceram com nomes de perfis e seus números são arbitrários.

### Centroides e arquivos

`salvarCentroides` usa `desnormalizar` quando o cenário é normalizado. Os dois arquivos de centroides já estão na unidade original. Portanto, o nome `centroides_normalizado.csv` significa “resultado do experimento normalizado”, não “valores ainda entre 0 e 1”. A média de partidas pode ser fracionária, mesmo que cada jogador tenha partidas inteiras.

`salvarGrupos` registra tamanhos e perfis; `salvarAtribuicoes` registra ID, apelido, perfil e grupo. `resumo_k3.csv` reúne semente, inércia, pureza, iterações e convergência dos dois cenários. Não compare diretamente uma inércia original com outra normalizada como se fossem a mesma unidade.

## 4. Exemplo real e conta da pureza

Com min-max e K=3, os tamanhos foram 42, 57 e 51. As maiores contagens de perfil em cada grupo foram 31, 39 e 50: `(31+39+50)/150=0,8`, ou **80%**.

Sem normalização, as maiores contagens foram 19, 33 e 27: `79/150≈0,5266667`, ou **52,67%**. Esses valores correspondem aos dados e sementes desta execução; não são uma previsão para outras bases.

As menores inércias de K=3 ocorreram com semente 84: 10,554837 no cenário normalizado e 256433,191194 no original. A diferença de escala explica por que não usamos a diferença desses números como medida direta da melhora.

## 5. Perguntas possíveis

1. **Como se calcula a inércia?** Soma das distâncias ao quadrado de cada ponto ao centroide de seu grupo. Não é média nem soma das raízes.
2. **Por que executar cinco sementes?** Para observar a dependência da inicialização e escolher a menor inércia entre tentativas reproduzíveis. Não garante o mínimo global.
3. **Por que K=3 se o gráfico cai mais antes?** Porque o enunciado exige detalhar K=3. O gráfico serve à discussão do número de grupos; não substitui essa análise obrigatória.
4. **Pureza de 80% significa classificar 80% dos novos jogadores?** Não. É a predominância dos perfis nos grupos da base agrupada, não uma acurácia de previsão sobre teste.
5. **Pode usar o perfil para escolher o centroide?** Não neste algoritmo. O perfil é consultado depois, para montar a tabela e avaliar.
6. **É obrigatório o grupo 1 ser CASUAL?** Não. Rótulos de grupo podem trocar de número entre sementes. Compare sua composição, não apenas o número.
7. **Por que usar os 150 jogadores no min-max?** O K-Means agrupa a base completa sem treino/teste. Isso difere do KNN, onde a escala deve vir somente do treino.
8. **Como o gráfico foi produzido sem biblioteca externa?** O próprio Java desenha uma imagem e grava PNG com ImageIO. Os dados também ficam em CSV para conferência.

## 6. O que dominar

Saiba reconstruir a conta de pureza a partir da tabela, explicar inércia e interpretar o cotovelo com cautela. Localize a escolha da semente vencedora e a conversão dos centroides. Trabalhe nos experimentos sem duplicar ou alterar silenciosamente o algoritmo de Gustavo.
