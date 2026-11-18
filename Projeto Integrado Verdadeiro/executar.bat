@echo off
setlocal
set "PROJETO_MODO="
set "PROJETO_CLASSE=TrabalhoFinal"
set "PROJETO_TESTES="
if "%~1"=="" set "PROJETO_MODO=todos"
if /i "%~1"=="todos" set "PROJETO_MODO=todos"
if /i "%~1"=="banco" set "PROJETO_MODO=a"
if /i "%~1"=="samuel" set "PROJETO_MODO=a"
if /i "%~1"=="knn" set "PROJETO_MODO=b"
if /i "%~1"=="kmeans" set "PROJETO_MODO=c"
if /i "%~1"=="testes" (
    set "PROJETO_MODO=testes"
    set "PROJETO_CLASSE=TestesProjeto"
    set "PROJETO_TESTES=test\*.java"
)
if not defined PROJETO_MODO (
    echo Use: executar.bat [todos^|banco^|knn^|kmeans^|testes] [horas partidas gasto taxa]
    exit /b 2
)
pushd "%~dp0"
if errorlevel 1 exit /b 1
if not exist bin mkdir bin
javac -J-Xmx256m -J-XX:ActiveProcessorCount=2 -encoding UTF-8 --release 11 -cp lib/h2-2.4.240.jar -d bin src\*.java %PROJETO_TESTES%
if errorlevel 1 goto falha
if "%PROJETO_MODO%"=="testes" goto testes
if not "%~2"=="" goto jogador
java -Xmx128m -XX:ActiveProcessorCount=2 -Djava.awt.headless=true -Dfile.encoding=UTF-8 -cp "bin;lib/h2-2.4.240.jar" TrabalhoFinal %PROJETO_MODO%
goto verificar
:jogador
java -Xmx128m -XX:ActiveProcessorCount=2 -Djava.awt.headless=true -Dfile.encoding=UTF-8 -cp "bin;lib/h2-2.4.240.jar" TrabalhoFinal %PROJETO_MODO% "%~2" "%~3" "%~4" "%~5"
goto verificar
:testes
java -Xmx128m -XX:ActiveProcessorCount=2 -Djava.awt.headless=true -Dfile.encoding=UTF-8 -cp "bin;lib/h2-2.4.240.jar" %PROJETO_CLASSE%
:verificar
if errorlevel 1 goto falha
popd
echo.
echo Execucao concluida.
if "%~1"=="" pause
exit /b 0
:falha
popd
echo Falha. Confira a mensagem acima, o JDK e os arquivos do projeto.
if "%~1"=="" pause
exit /b 1
