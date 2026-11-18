# Validação local da preparação da apresentação

Data: **18/11/2026**. Repositório `roquerisley-hash/KNN-K-Means`, branch `codex/integracao-trabalho-final-grupo4`, partindo de `61aa379`. Estes testes foram executados no computador Windows do usuário por comandos locais. Não são apenas resultados de um ambiente remoto.

## Ambiente encontrado

- JDK Eclipse Adoptium 17.0.19 com `java` e `javac` disponíveis; compilação com `--release 11`.
- Git instalado; clone correto e sincronizado antes das alterações desta etapa.
- VS Code 1.146.0 instalado, aberto na raiz correta. Extensões Red Hat Java, Java Debugger e Java Extension Pack já instaladas. Não foram feitas instalações.
- Driver H2 2.4.240 presente em `Projeto Integrado Verdadeiro/lib`.
- PowerShell 7.6.9 disponível no ambiente de execução; Windows PowerShell 5.1 presente, mas com bloqueio de `.ps1`. Nenhuma política de execução foi alterada.

## Testes e execuções

| Verificação | Resultado observado | Local ou limite |
|---|---|---|
| `executar.ps1 -Modo testes` | 171 verificações de KNN e 797 de K-Means passaram; teste integrado encerrado com sucesso | Windows local |
| `executar.ps1` sem modo, após correção | Código de saída 0; consultas A, KNN e K-Means completos | Windows local, PowerShell 7 |
| `executar.bat todos` | Código de saída 0; mesmos resultados A/B/C e arquivos de saída | Windows local |
| `executar.bat knn 15 35 80 52` | Código de saída 0; novo jogador REGULAR, vizinhos 75/62/37/122/88 | Windows local |
| `executar.bat testes` | Código de saída 0; 171 verificações de KNN e 797 de K-Means passaram | Windows local, novo iniciador validado |
| Inicialização JDBC | 150 linhas; DAO com 150 objetos; 50 de cada perfil nas consultas | H2 embarcado em memória |
| KNN principal | Treino 120, teste 30, semente 42; 29/30, 96,67%; erro ID 130, RANQUEADO → REGULAR | Execução local real |
| Comparações KNN K=5 | Euclidiana original 26/30; Manhattan normalizada 28/30; Manhattan original 26/30 | Execução local real |
| Variação de K normalizada | K=1/3/5/7/9/11: 30/30, 30/30, 29/30, 29/30, 30/30, 29/30 | Mesma divisão local |
| K-Means K=3, min-max | Semente 84, inércia 10,5548371364, pureza 0,8, seis iterações | Execução local real |
| K-Means K=3, original | Semente 84, inércia 256433,1911940642, pureza 0,5266666667, nove iterações | Execução local real |
| Experimentos K-Means | 35 inicializações, todas convergiram; máximo observado de 15 iterações; cotovelo K=1..6 | CSVs gerados localmente |
| Resultados publicados antes | Novos CSVs e PNG comparados com os snapshots da integração | Não é nova execução remota |
| Configurações `.vscode` | JSON válido e caminhos relativos existentes; serviço Java do editor iniciou com heap 256 MiB e dois processadores | Configuração e processo conferidos, não prova de Run |
| Botão Run / Ctrl+F5 e terminal integrado | Não verificados pela interface | Controle do computador recusou acesso ao VS Code, inclusive após o usuário informar que autorizaria |
| Duplo clique no `.bat` | Não automatizado pela interface | Mesmo modo `todos` validado por comando; sem argumento acrescenta pausa |
| Gravação e envio do vídeo | Não realizados | Foram preparados roteiro e instruções; destino/formato oficial não confirmado |

A matriz principal foi `[[9,0,0],[0,5,0],[0,1,15]]`, com linhas reais e colunas previstas. Os grupos normalizados tiveram tamanhos 42/57/51 e maiores contagens 31/39/50, somando 120/150 = 80%. As inércias das duas escalas não devem ser subtraídas ou comparadas diretamente como se fossem a mesma unidade.

## Problemas encontrados e alterações

1. **Repasse de modo no PowerShell:** o `switch` retornava um escalar para os modos com um argumento, e o repasse com splatting não preservava a lista esperada pelo Java. A chamada completa falhava em `TrabalhoFinal.main` antes de abrir o H2. A captura foi alterada para `@(switch (...) { ... })`, preservando um array também com um ou zero elementos. A execução completa passou após a correção.
2. **Bloqueio de `.ps1` no Windows PowerShell:** foi constatado ao testar a alternativa 5.1. Criado `executar.bat`, sem mudar proteção global ou política da sessão. O novo iniciador compila e executa com os mesmos limites e diretório relativo.
3. **Run do editor não confirmado:** foram criados `settings.json`, `launch.json` e recomendações de extensões portáveis. O acesso à janela foi negado pela ferramenta de controle do computador. O caminho recomendado no guia usa o `.bat` validado; o botão está explicitamente identificado como pendente de teste visual.

Não houve necessidade de modificar algoritmos, consultas, estrutura do SQL ou dependências. Os documentos explicam o código já integrado, sem atribuir autoria individual falsa à base.

## Recursos e arquivos

Compilações e execuções ocorreram em sequência, sem teste de estresse. Compilador: heap 256 MiB; programa: 128 MiB; dois processadores informados às JVMs. O K-Means mantém limite de 100 iterações. Todas as execuções da aplicação terminaram, sem servidor H2 persistente. Restou o serviço Java necessário ao projeto aberto no VS Code, já com a configuração reduzida; ele não é uma aplicação de experimentos abandonada.

Fontes, JAR e SQL permanecem no repositório. `bin` e resultados de execução continuam ignorados pelo Git; os snapshots anteriores em `docs/resultados` não foram substituídos sem necessidade. Não são publicados logs com caminhos pessoais, dados de autenticação ou configurações privadas.

Pendências fora desta validação: conferir o botão Run quando o acesso ao editor estiver liberado; ensaiar/gravar; confirmar instruções oficiais adicionais e destino do vídeo; concluir o relatório e as análises do grupo. A pesquisa que exige 20 divisões, média e desvio padrão continua fora dos experimentos desta integração, como já registrado no README.
