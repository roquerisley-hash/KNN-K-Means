# Como executar e gravar

## Caminho recomendado neste Windows

1. Abra o **VS Code**. Em **Arquivo → Abrir Pasta**, escolha `knn-kmeans-grupo4`, a raiz que contém este guia, o README e `Projeto Integrado Verdadeiro`. Não abra somente um arquivo Java ou a antiga pasta de entrega.
2. Abra **Terminal → Novo Terminal**. O terminal deve estar na raiz do repositório.
3. Execute este único comando:

   ```powershell
   & '.\Projeto Integrado Verdadeiro\executar.bat' todos
   ```

4. Aguarde aparecer **Execucao concluida**. O comando compila e executa as partes A/B/C em sequência. Não inicie outra execução enquanto esta estiver em andamento.
5. Confira `Número de linhas: 150`, treino 120/teste 30, KNN principal 29/30 e os dois resumos do K-Means. A saída termina; não fica um servidor de banco aberto.
6. No explorador do VS Code, abra `Projeto Integrado Verdadeiro/resultados`. As subpastas `knn` e `kmeans` contêm os CSVs atuais; abra `kmeans/cotovelo.png` para o gráfico.

Esse comando foi executado e terminou com sucesso **neste computador Windows**, não somente em um ambiente remoto. O teste automatizado também passou. O controle do computador recusou acesso à janela do VS Code; por isso a interação com seu botão Run e seu terminal integrado **não foi verificada visualmente**. Os comandos foram verificados no shell local. O VS Code abriu na pasta correta, e as extensões Java já estavam instaladas.

**Sem abrir terminal:** no Explorador de Arquivos, entre em `Projeto Integrado Verdadeiro` e dê dois cliques em **executar.bat**. Sem argumentos ele usa o mesmo modo `todos` e acrescenta uma pausa ao final, para a janela não fechar antes da leitura. A interação por duplo clique não foi automatizada nesta validação.

## O que já está disponível

- JDK 17 com `java` e `javac`; o projeto compila para Java 11.
- Git, VS Code e extensões Java, incluindo suporte a depuração. Não foi necessário instalar ferramentas.
- Driver `Projeto Integrado Verdadeiro/lib/h2-2.4.240.jar`, incluído no repositório e usado no classpath pelos iniciadores.
- Banco **H2 em memória**, URL `jdbc:h2:mem:grupo4`. `BancoDados.abrir()` executa `jogadores.sql`, cria a tabela e carrega 150 jogadores a cada início. A conexão é fechada ao terminar. Não há servidor, porta, senha pessoal ou arquivo de banco para configurar.
- O iniciador entra na pasta do projeto antes de executar, então o SQL é encontrado mesmo quando o comando parte da raiz.

## Botão de execução do VS Code — configuração preparada

1. Com a raiz aberta, aguarde o suporte Java reconhecer o projeto.
2. Abra **Executar e Depurar** (`Ctrl+Shift+D`).
3. Escolha **Gravar: projeto completo** na lista e pressione **Ctrl+F5**.
4. A saída está no **Console de Depuração**, não no terminal. Ao terminar, procure os mesmos resultados do passo 5 acima.

Também há configurações para banco, KNN, K-Means e testes. Os arquivos `.vscode/settings.json` e `launch.json` usam caminhos relativos para fontes, testes, classes compiladas, JAR e diretório atual. Não incluem caminhos pessoais. A compilação automática está desativada, e a configuração pede compilação antes de Run.

**Estado desta opção:** JSON e caminhos conferidos; execução pelo botão ainda não confirmada devido à recusa de permissão do controle do computador. Se o editor apresentar erro de configuração, use o `.bat` já validado para a gravação. Não escolha continuar com erros de compilação. O guia não afirma que o botão já foi testado.

## Executar apenas uma parte ou outro jogador

No terminal, sempre na raiz:

```powershell
& '.\Projeto Integrado Verdadeiro\executar.bat' banco
& '.\Projeto Integrado Verdadeiro\executar.bat' knn
& '.\Projeto Integrado Verdadeiro\executar.bat' kmeans
& '.\Projeto Integrado Verdadeiro\executar.bat' testes
```

Escolha **um** comando conforme a necessidade. Não execute todos antes de cada gravação. Para informar um jogador ao KNN:

```powershell
& '.\Projeto Integrado Verdadeiro\executar.bat' knn 15 35 80 52
```

A ordem é horas por semana, partidas inteiras por semana, gasto mensal e taxa de vitória. Use ponto para decimal, por exemplo `18.1`. O exemplo acima foi validado e retorna REGULAR. A classe principal é `TrabalhoFinal`; seus argumentos Java são `todos`, `a`, `b` e `c`. Os iniciadores traduzem os nomes mais fáceis para esses argumentos.

O `executar.ps1` também foi corrigido e validado no PowerShell 7. Neste computador, o Windows PowerShell 5.1 bloqueou `.ps1` pela política de execução. **Não é preciso mudar essa política:** use `executar.bat`.

## Sequência para gravar sem procurar arquivos

1. Abra `ROTEIRO_APRESENTACAO.md` em outra tela/dispositivo e ensaie a divisão de 7min45s. Essa duração é uma escolha do grupo.
2. Prepare abas de código: `BancoDados.java`, `JogadorDAOJdbc.java`, `App.java`, `DivisaoDados.java`, `KNN.java`, `NormalizadorMinMax.java`, as duas distâncias, `AvaliacaoKNN.java`, `KMeans.java` e `ExperimentosKMeans.java`. Todos estão em `Projeto Integrado Verdadeiro/src`.
3. No trecho de Samuel, execute o comando `.bat` completo **uma única vez**. Se o botão Run tiver sido conferido por vocês antes, ele é uma alternativa; não use os dois.
4. No terminal, suba até a contagem de 150 e as consultas. Depois Davi mostra a divisão, o novo jogador e os cinco vizinhos.
5. Milena mostra acurácias e matriz. Samuel mostra a linha `euclidiana_normalizada` do ID 130 em `resultados/knn/erros.csv`.
6. Gustavo explica o código do algoritmo. Risley abre `resultados/kmeans/cotovelo.png`, `grupos_normalizado.csv`, `resumo_k3.csv` e os centroides. Os caminhos completos por fala estão no roteiro.
7. Evite ler todas as linhas da consulta e todas as inicializações. A saída já tem títulos por etapa; use os CSVs para os quadros mais legíveis.
8. Confira áudio, tamanho da fonte e duração. Oculte notificações e dados pessoais. A forma oficial de envio do vídeo e eventual exigência de câmera ainda precisam ser confirmadas; não foram encontradas nas fontes consultadas.

## Se a execução falhar

1. Leia a primeira mensagem de erro. Se ainda estiver rodando, use **Ctrl+C** no terminal; no Run do VS Code use o botão quadrado **Parar**.
2. Aguarde o processo encerrar. Não abra várias cópias nem encerre todos os processos Java do computador: o VS Code pode ter seu próprio serviço Java.
3. `java`/`javac` não reconhecido: abra um terminal novo e confira `java -version` e `javac -version`. O JDK já existe neste Windows; não instale outro antes de investigar o PATH.
4. Driver não encontrado: confira `lib/h2-2.4.240.jar`. SQL não encontrado: use o iniciador ou o `cwd` da configuração fornecida; não execute a classe em outra pasta.
5. Compilação com erro: corrija a mensagem indicada e execute novamente. Não use classes antigas como se a nova compilação tivesse passado.
6. Após resolver, rode o mesmo comando uma vez. O banco em memória é recriado automaticamente; não há serviço ou arquivo `.db` para apagar.

Os iniciadores limitam a heap do compilador a 256 MiB e a do programa a 128 MiB, informando dois processadores à JVM. O K-Means tem limite de 100 iterações. São limites para essas JVMs, não uma garantia de uso total de RAM/CPU do Windows. Fechar a janela do projeto ao terminar também encerra o serviço Java do editor quando ele não é mais necessário.

O registro dos testes e das limitações está em `docs/VALIDACAO_LOCAL.md`.
