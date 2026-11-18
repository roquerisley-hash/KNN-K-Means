# Guia de estudo — Samuel

## 1. Responsabilidade

Apresentar a origem dos dados, o banco H2, o acesso por DAO, as três consultas da Parte A e os dados disponíveis para investigar os erros do KNN. A interpretação final da Parte E deve se apoiar nesses resultados, sem transformar uma observação isolada em causa comprovada.

## 2. Arquivos utilizados

Na pasta `Projeto Integrado Verdadeiro/`:

- `jogadores.sql` e `lib/h2-2.4.240.jar`.
- Em `src/`: `BancoDados.java`, `JogadorDAO.java`, `JogadorDAOJdbc.java`, `Jogador.java`, `Perfil.java`, `App.java` e `TrabalhoFinal.java`.
- Para a Parte E: `resultados/knn/erros.csv`, `previsoes.csv`, `distribuicao_erros.csv` e `matrizes.csv`.

## 3. Como o código funciona

### Inicialização do banco

`BancoDados.abrir` chama `DriverManager.getConnection` com `jdbc:h2:mem:grupo4`, usuário local `sa` e senha vazia. É um H2 embarcado em memória, não uma conexão com um servidor instalado à parte. O JAR fornece o driver JDBC e precisa estar no classpath; o Java o identifica automaticamente.

`carregar` executa `RUNSCRIPT FROM 'jogadores.sql'` por PreparedStatement. O SQL elimina a tabela temporária anterior se existir, cria `jogador` e insere os 150 registros sintéticos. Depois o método executa COUNT, exige 150 e imprime a quantidade. Se a inicialização falhar, a conexão é fechada e o erro é propagado.

O caminho do SQL é relativo ao diretório de execução. Por isso o script entra em `Projeto Integrado Verdadeiro`, e a configuração do VS Code usa essa mesma pasta como `cwd`. Abrir o arquivo Java sozinho, com outro diretório atual, pode fazer o SQL não ser encontrado.

`TrabalhoFinal.main` abre a conexão em try-with-resources. As partes A/B/C usam essa conexão e a lista obtida pelo DAO; no fim a conexão é fechada e o banco em memória desaparece. Não há um arquivo de banco preenchido para mandar aos colegas: enviamos SQL e driver, e o banco é recriado quando executado.

### DAO e objetos

`JogadorDAO` define `listarTodos`, `buscarPorId` e `listarPorPerfil`. A implementação `JogadorDAOJdbc` recebe uma Connection pelo construtor. Essa separação permite ao restante do programa trabalhar com a interface, sem espalhar detalhes JDBC pelo KNN.

`listarTodos` seleciona as colunas e ordena por ID. Abre PreparedStatement e ResultSet em try-with-resources, percorre com `while (rs.next())` e chama `montarJogador`. Esse método lê os tipos corretos e usa `Perfil.valueOf` para converter o texto do banco em enum. Os objetos são adicionados à lista retornada.

`buscarPorId` usa `WHERE id = ?`, liga o valor com `setInt` e retorna Optional preenchido ou vazio. `listarPorPerfil` usa `setString(..., perfil.name())`; uma lista vazia já representa ausência de resultados. SQLException não é escondida: o DAO relança como RuntimeException com contexto.

`Jogador` tem campos privados/finais e não possui setters. `getCaracteristicas()` cria um novo vetor na ordem horas, partidas, gasto, vitória. O código de ML não altera os registros do banco por meio desse vetor. `equals`/`hashCode` usam ID.

### Consultas preservadas em App.executarConsultas

1. COUNT por perfil usando parâmetro `?`: confirma 50 CASUAL, 50 REGULAR e 50 RANQUEADO.
2. `GROUP BY perfil` com AVG das quatro colunas: descreve o comportamento médio de cada perfil conhecido.
3. `horas_semana > 20 AND taxa_vitoria > 55`, com `ORDER BY taxa_vitoria DESC`: lista o subconjunto solicitado. A taxa está em porcentagem de 0 a 100, então o limite é 55, não 0,55.

As consultas existentes foram preservadas; a integração extraiu um método para permitir a mesma conexão na aplicação completa.

### Dados da Parte E

`erros.csv` mistura linhas de **quatro configurações**, identificadas pela primeira coluna. Filtre `euclidiana_normalizada` para o modelo principal de K=5. `previsoes.csv` guarda também os acertos; serve para comparar características de erros e acertos. `distribuicao_erros.csv` resume somente o modelo principal por perfil real. As matrizes mostram origem e destino dos erros.

Não some a presença repetida de um jogador em várias configurações como pessoas diferentes. A pergunta “o que têm em comum?” exige comparar dados e declarar o alcance da observação; nesta divisão, o modelo principal só tem um erro.

## 4. Exemplo real

O ID **130**, CrimsonNinja12, é RANQUEADO e foi previsto como REGULAR pelo modelo principal. Tem 18,1 horas/semana, 74 partidas/semana, R$ 0,00 de gasto e 56,8% de vitória. É correto dizer que esse jogador errou e tem gasto zero. **Não foi demonstrado que gasto zero foi a causa do erro**, nem que todo jogador com esse gasto será classificado assim.

## 5. Perguntas possíveis

1. **Precisa instalar servidor H2?** Não. O driver está no JAR e a URL usa memória dentro do processo Java.
2. **Por que o programa funciona sem arquivo `.db`?** Porque a tabela é criada automaticamente pelo SQL na memória. Os dados não persistem após fechar a última conexão.
3. **O que o DAO separa?** A leitura JDBC da lógica dos algoritmos. O KNN recebe objetos e não executa consultas SQL.
4. **Por que PreparedStatement?** Permite ligar valores como dados com `?`, evitando concatenação de entrada na SQL. Também organiza execução e fechamento dos recursos.
5. **ResultSet é a lista de jogadores?** Não: é o cursor de linhas da consulta. O DAO percorre e constrói os objetos Java que formarão a lista.
6. **Por que enum para perfil?** Limita as opções válidas no Java; `.name()` grava/consulta texto e `valueOf` converte o texto retornado.
7. **A Parte E já prova um padrão geral?** Não. Há saídas reais para investigação. Um teste de 30 pessoas e um erro no modelo principal não bastam para afirmar causalidade ou generalização.
8. **O que verificar se aparece erro de SQL não encontrado?** O diretório atual/cwd, o nome do arquivo e a presença do SQL na pasta do projeto; não instalar outro banco sem investigar o caminho.

## 6. O que dominar

Saiba seguir `Connection → PreparedStatement → ResultSet → Jogador → List`. Localize as três consultas e explique o ciclo do banco em memória. Na Parte E, identifique a configuração analisada e separe resultado observado, hipótese e conclusão sustentada.
