param(
    [ValidateSet('todos', 'banco', 'knn', 'kmeans', 'samuel', 'testes')]
    [string]$Modo = 'todos',
    [string[]]$Caracteristicas = @()
)
$ErrorActionPreference = 'Stop'
Push-Location $PSScriptRoot
try {
    New-Item -ItemType Directory -Path 'bin' -Force | Out-Null
    $fontes = @(Get-ChildItem -LiteralPath 'src' -Filter '*.java' | ForEach-Object { $_.FullName })
    if ($Modo -eq 'testes') {
        $fontes += @(Get-ChildItem -LiteralPath 'test' -Filter '*.java' | ForEach-Object { $_.FullName })
    }
    & javac '-J-Xmx256m' '-J-XX:ActiveProcessorCount=2' -encoding UTF-8 --release 11 -cp 'lib/h2-2.4.240.jar' -d bin @fontes
    if ($LASTEXITCODE -ne 0) { throw 'Falha na compilação.' }
    $classe = if ($Modo -eq 'testes') { 'TestesProjeto' } else { 'TrabalhoFinal' }
    $argumentos = switch ($Modo) {
        'banco' { @('a') }
        'samuel' { @('a') }
        'knn' { @('b') }
        'kmeans' { @('c') }
        'todos' { @('todos') }
        default { @() }
    }
    & java '-Xmx128m' '-XX:ActiveProcessorCount=2' '-Djava.awt.headless=true' '-Dfile.encoding=UTF-8' -cp 'bin;lib/h2-2.4.240.jar' $classe @argumentos @Caracteristicas
    if ($LASTEXITCODE -ne 0) { throw 'Falha na execução.' }
} finally {
    Pop-Location
}
