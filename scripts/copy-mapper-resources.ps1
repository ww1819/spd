# Copy MyBatis/XML resources into target/classes (IDE often compiles .class only).
$root = Split-Path $PSScriptRoot -Parent
if (-not (Test-Path (Join-Path $root 'spd-biz'))) { $root = 'e:\workspace\spd' }
$mods = @('spd-biz','spd-system','spd-quartz','spd-framework','spd-common','spd-generator','spd-admin')
foreach ($m in $mods) {
  $src = Join-Path $root "$m\src\main\resources"
  $dst = Join-Path $root "$m\target\classes"
  if (-not (Test-Path $src)) { Write-Host "skip $m"; continue }
  if (-not (Test-Path $dst)) { New-Item -ItemType Directory -Path $dst -Force | Out-Null }
  & robocopy $src $dst /E /IS /IT /NFL /NDL /NJH /NJS /nc /ns /np | Out-Null
  Write-Host ("copied {0} (robocopy={1})" -f $m, $LASTEXITCODE)
}
$checks = @(
  'spd-system\target\classes\mapper\system\SysUserMapper.xml',
  'spd-quartz\target\classes\mapper\quartz\SysJobMapper.xml',
  'spd-biz\target\classes\mapper\foundation\FdSupplierMapper.xml'
)
foreach ($c in $checks) {
  $p = Join-Path $root $c
  Write-Host ("{0} {1}" -f ($(if (Test-Path $p) {'OK'} else {'MISSING'})), $c)
}
