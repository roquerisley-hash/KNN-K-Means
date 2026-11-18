# Guia de estudo — Gustavo

## 1. Responsabilidade

Explicar a implementação-base do K-Means e conseguir modificar seu funcionamento sem duplicar os experimentos. Essa base foi produzida com auxílio de um assistente para estudo e revisão do responsável, como registrado no README. Entender o processo é mais importante que decorar a classe.

## 2. Arquivos utilizados

- `Projeto Integrado Verdadeiro/src/KMeans.java`: algoritmo e classe interna `Resultado`.
- `src/ExperimentosKMeans.java`, na mesma pasta de projeto: prepara a matriz e chama o algoritmo.
- `src/NormalizadorMinMax.java`: transformação usada antes do agrupamento.
- `test/TestesKMeans.java`: casos pequenos e verificações com os jogadores reais.

## 3. Processo passo a passo

### Entrada e parâmetros

O construtor recebe K, limite de iterações e semente. Exige números positivos para K e limite. `executar(double[][] dados)` recebe uma matriz: cada linha é um ponto/jogador e cada coluna é uma característica. No trabalho são quatro colunas, mas o cálculo aceita outra dimensão consistente. **Não há parâmetro Perfil no algoritmo.**

`validar` exige ao menos K pontos, dimensão não vazia, mesmo número de colunas em todas as linhas e números finitos. Os experimentos, não o algoritmo, fazem a leitura do DAO e a normalização.

### Centroides iniciais

`sortearCentroides` monta uma lista de índices, embaralha com `Random(semente)` e escolhe os K primeiros. Os índices são distintos; isso não garante vetores de valores diferentes se os próprios dados tiverem duplicatas. Cada centroide é uma cópia do ponto escolhido, para não alterar a matriz original.

### Laço principal

Os grupos começam com -1, indicando que ninguém foi atribuído. Em cada rodada:

1. Copia as atribuições anteriores.
2. `atribuirGrupos` encontra o centroide mais próximo de cada ponto.
3. `tratarGruposVazios` garante que todo grupo tenha integrantes.
4. `calcularCentroides` calcula as novas médias.
5. Aumenta o contador e compara os grupos com a cópia anterior.

O `while` continua até igualdade das atribuições ou limite. Se atingir o limite sem estabilizar, `isConvergiu()` é false; o programa não transforma a parada forçada em convergência.

### Distância e atribuição

`distanciaQuadrada(a,b)` soma `(a[i]-b[i])²`. A raiz seria crescente, então não é necessária para decidir qual é a menor distância. Isso corresponde à comparação por euclidiana e também fornece a quantidade usada na inércia.

Para cada ponto, `atribuirGrupos` começa pelo grupo 0 e só muda quando encontra uma distância **estritamente menor**. Em empate, permanece o menor índice de grupo. O array de grupos usa índices de 0 a K-1; os CSVs mostram 1 a K para facilitar a leitura.

### Grupo vazio

Primeiro contamos os integrantes. Para cada grupo vazio, procuramos o ponto mais distante do centroide de seu grupo atual, mas somente em grupos com mais de um integrante. Movemos esse ponto e atualizamos os tamanhos. Assim, o grupo doador não se torna vazio. Em empate de distância, fica o primeiro índice encontrado.

Com N ≥ K, se existe um vazio, algum grupo ocupado tem integrantes suficientes para doar. Depois da recuperação, recalculamos todas as médias. O tratamento também funciona quando todos os pontos têm os mesmos valores.

### Média e resultado

`calcularCentroides` soma cada coluna por grupo e divide pelo tamanho correspondente. O centroide pode não coincidir com um jogador real, embora os centroides iniciais sejam sorteados entre jogadores.

Ao final, `executar` soma a distância quadrada de cada ponto ao centroide do grupo atribuído. Guarda grupos, centroides, inércia, iterações, convergência e semente em `Resultado`. Os getters de arrays devolvem cópias; a matriz é copiada linha por linha, impedindo alterações externas.

## 4. Exemplo reduzido

**Caso didático usado nos testes, não o banco de jogadores:** pontos 0, 2, 10 e 12, com K=2 e semente 42. O resultado agrupa 0/2 e 10/12. As médias são 1 e 11. A inércia é `1²+1²+1²+1²=4`. Os números dos grupos podem ser invertidos sem mudar essa partição.

**Execução dos 150 jogadores:** os experimentos com cinco sementes convergiram em até 15 iterações; o limite era 100. Essa observação não garante que todo conjunto de dados sempre convergirá em 15 rodadas.

## 5. Perguntas possíveis

1. **O K tem o mesmo significado do KNN?** Não. No KNN é quantidade de vizinhos; no K-Means é quantidade de grupos.
2. **Por que não usar o perfil real para atribuir grupos?** Porque o agrupamento é não supervisionado. Usar os perfis tornaria a avaliação posterior circular.
3. **Por que atualizar o centroide pela média?** Porque cada centroide deve representar o centro dos pontos atribuídos segundo o objetivo de distâncias quadradas usado pelo K-Means.
4. **Por que testar outras sementes?** Inicializações diferentes podem levar a soluções distintas. Os experimentos escolhem a menor inércia observada entre tentativas limitadas.
5. **O algoritmo acha o mínimo global?** Não há essa garantia. A melhor entre cinco sementes continua sendo a melhor apenas entre as tentativas realizadas.
6. **Quando um grupo vazio é resolvido?** Depois da atribuição e antes de recalcular médias. Isso evita divisão por zero e preserva K grupos ocupados.
7. **Se chegar a 100 iterações, convergiu?** Não necessariamente. O resultado registra a condição real; deve ser conferida em vez de presumida.
8. **Por que cópias dos arrays?** Para que quem recebe o resultado ou os centroides não consiga alterar silenciosamente o estado guardado pelo algoritmo.

## 6. O que dominar

Consiga desenhar uma rodada: atribuir, recuperar vazios, tirar médias e comparar grupos. Explique semente, distância quadrada, critério de parada e cópia defensiva. Preserve o contrato `executar(double[][])` ao trabalhar com Risley; mudanças no resultado exigem ajustar experimentos e testes juntos.
