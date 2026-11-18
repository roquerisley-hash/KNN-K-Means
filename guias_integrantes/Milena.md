# Guia de estudo — Milena

## 1. Sua responsabilidade

Entender a preparação das características, as duas distâncias e a avaliação do KNN. Você precisa distinguir “o algoritmo previu” de “a previsão estava correta”. A normalização e as distâncias originais foram preservadas; os complementos de avaliação e experimentos foram consolidados no mesmo projeto.

## 2. Arquivos utilizados

Em `Projeto Integrado Verdadeiro/src/`: `NormalizadorMinMax.java`, `MedidaDeDistancia.java`, `DistanciaEuclidiana.java`, `DistanciaManhattan.java`, `AvaliacaoKNN.java` e `ExperimentosKNN.java`. Consulte também o construtor de `KNN` para conferir onde o normalizador é ajustado.

Resultados atuais: `Projeto Integrado Verdadeiro/resultados/knn/`. A pasta `docs/resultados/knn/` guarda o snapshot publicado, não as saídas atualizadas automaticamente.

## 3. Código explicado

### Min-max

`encontrarMinMax(List<Jogador>)` rejeita lista vazia, cria `mins` e `maxs` com uma posição para cada característica e começa com os valores do primeiro jogador. Percorre a lista e usa `Math.min`/`Math.max` para cada coluna.

`normalizar(double[])` exige que o ajuste já tenha sido feito e que o vetor tenha a dimensão esperada. A fórmula por coluna é:

`normalizado = (valor - mínimo) / (máximo - mínimo)`

Se a diferença entre máximo e mínimo for zero, devolve zero naquela posição. Cria um novo array: não altera o jogador original. No KNN, o construtor passa apenas o treino ao ajuste. Todos os testes e consultas seguintes usam esses mesmos limites. Não se limita artificialmente o resultado a [0,1]; um valor novo acima do máximo pode gerar um número maior que 1.

`desnormalizar` faz a operação inversa: `mínimo + normalizado * (máximo - mínimo)`. Foi acrescentado para mostrar os centroides do K-Means na unidade original. Na Parte C, um normalizador separado é ajustado com os 150 jogadores, porque não há treino/teste.

### Distâncias intercambiáveis

`MedidaDeDistancia` define `calcular(double[], double[])`. A euclidiana verifica dimensões, acumula `Math.pow(a[i] - b[i], 2)` e devolve `Math.sqrt(soma)`. A Manhattan acumula `Math.abs(a[i] - b[i])`. Não olham apelido, ID ou rótulo. O KNN recebe o contrato, não uma fórmula fixa.

### Avaliação

O construtor de `AvaliacaoKNN` percorre o teste, pede uma previsão ao KNN e guarda uma `Previsao` com o jogador original e o perfil previsto. O perfil real só entra **depois** para conferir. A matriz é incrementada em `[real.ordinal()][previsto.ordinal()]`. Se os perfis coincidirem, aumenta o número de acertos.

`getAcuracia` divide acertos pelo total com conversão para `double`, evitando divisão inteira. `getErros` seleciona previsões diferentes do perfil real. `getMatrizConfusao` copia as linhas da matriz para impedir que uma alteração externa corrompa a avaliação.

### Experimentos

`ExperimentosKNN.executar` recebe uma única `DivisaoDados`. `testarValoresK` avalia 1, 3, 5, 7, 9 e 11 com euclidiana/min-max. `compararConfiguracoes` fixa K=5 e combina duas distâncias com normalização ligada/desligada. A avaliação euclidiana normalizada de K=5 é reutilizada; as duas linhas em tabelas diferentes não significam um novo sorteio.

Os métodos de gravação exportam acurácias, matrizes, previsões e erros. `salvarDistribuicaoErros` resume apenas o modelo principal por perfil real. Não deve ser confundido com a lista de erros das quatro configurações.

## 4. Exemplos

**Didático:** se horas varia de 2 a 22 no treino e a consulta tem 12, o resultado é `(12-2)/(22-2)=0,5`. Se todos os valores da coluna forem 8, o denominador é zero e o código escolhe 0. Para pontos reduzidos `[0,0]` e `[3,4]`, euclidiana dá 5 e Manhattan dá 7. São exemplos de fórmula, não registros do banco.

**Execução real:** K=5/euclidiana/min-max acertou 29/30, ou 96,67%; euclidiana sem min-max, 26/30; Manhattan com min-max, 28/30; Manhattan sem min-max, 26/30. A diagonal da matriz principal é 9+5+15=29. A soma de todas as células é 30.

## 5. Perguntas possíveis

1. **Por que normalizar?** Para reduzir a influência da unidade/escala: centenas de reais não deveriam dominar dezenas de horas apenas pelo tamanho dos números.
2. **Por que o teste não participa dos mínimos e máximos?** Porque isso deixaria informação do teste influenciar a preparação do modelo, causando vazamento de dados.
3. **Normalizar muda o perfil do jogador?** Não. Transforma apenas os vetores numéricos usados na distância; o objeto original e seu perfil ficam preservados.
4. **Qual distância é melhor?** Depende dos dados e da preparação. Nesta divisão, a euclidiana normalizada teve um acerto a mais. Não é uma conclusão universal.
5. **O que a matriz mostra além da acurácia?** De quais perfis reais vieram os erros e para quais perfis foram previstos. Acurácia sozinha esconde essa distribuição.
6. **96,67% significa 96,67 jogadores corretos?** Não: foram 29 de 30. A porcentagem é uma apresentação arredondada de 29/30.
7. **Por que não usar StandardScaler da aula?** O enunciado deste trabalho pede min-max e algoritmo manual em Java. O exemplo da aula não substitui essa exigência.
8. **Por que comparar sobre a mesma divisão?** Para que a mudança observada venha da configuração testada, sem misturar o efeito de trocar os próprios jogadores de teste.

## 6. O que dominar

Saiba aplicar as três fórmulas, localizar onde o ajuste ocorre e ler uma matriz com linhas reais/colunas previstas. Entenda os limites de uma avaliação com 30 casos e não anuncie um K ou distância universalmente melhores. Para a tela, prepare `normalizar`, os dois métodos `calcular`, `getAcuracia` e os CSVs do KNN.
