param(
    [ValidateSet('knn', 'samuel')]
    [string]$Modo = 'knn',
    [string[]]$Caracteristicas = @()
)
$ErrorActionPreference = 'Stop'
Push-Location $PSScriptRoot
try {
    New-Item -ItemType Directory -Path 'bin' -Force | Out-Null
    $fontes = @(Get-ChildItem -LiteralPath 'src' -Filter '*.java' | ForEach-Object { $_.FullName })
    & javac -encoding UTF-8 --release 11 -cp 'lib/h2-2.4.240.jar' -d bin @fontes
    if ($LASTEXITCODE -ne 0) { throw 'Falha na compilação.' }
    $classe = if ($Modo -eq 'samuel') { 'App' } else { 'AppKNN' }
    & java '-Dfile.encoding=UTF-8' -cp 'bin;lib/h2-2.4.240.jar' $classe @Caracteristicas
    if ($LASTEXITCODE -ne 0) { throw 'Falha na execução.' }
} finally {
    Pop-Location
}
