param(
    [Parameter(Mandatory = $true)][string]$CatalinaScript
)

$currentColor = "White"
$esc = [char]27
& $CatalinaScript jpda run 2>&1 | ForEach-Object {
    $line = [string]$_
    if ($line.Length -gt 16384) {
        [Console]::Out.WriteLine($line)
        return
    }
    $line = $line -replace "\x1b\[[0-9;]*m",""
    $level = ""
    if ($line -match '(?i)"level"\s*:\s*"(TRACE|DEBUG|INFO|WARN|ERROR|FATAL|SEVERE|CRITICAL)"') {
        $level = $Matches[1].ToUpperInvariant()
    } else {
        # Timestamp layouts vary, so inspect only a bounded header for an uppercase level token.
        # The bound avoids treating level-like words deep in a message body as log metadata.
        $header = $line.Substring(0, [Math]::Min($line.Length, 160))
        if ($header -cmatch '(?<![A-Za-z0-9_])(TRACE|DEBUG|INFO|WARN|WARNING|ERROR|FATAL|SEVERE|CRITICAL)(?![A-Za-z0-9_])') {
            $level = $Matches[1]
        }
    }

    if ($line -match '^\s+at\s+\S+\([^)]*\)\s*$') {
        Write-Host "$esc[2m$line$esc[0m" -ForegroundColor $currentColor
        return
    }
    if ($line -match '^\s*\.\.\.\s+\d+\s+(more|common frames omitted)\s*$') {
        Write-Host "$esc[2m$line$esc[0m" -ForegroundColor DarkCyan
        return
    }

    $matched = $false
    if ($line -match '^\s*Caused by:') {
        $currentColor = "Red"; $matched = $true
    } elseif ($line -match '^\s*Suppressed:') {
        $currentColor = "Yellow"; $matched = $true
    } elseif ($level) {
        switch -Regex ($level) {
            'FATAL|CRITICAL' { $currentColor = "Magenta"; break }
            'ERROR|SEVERE' { $currentColor = "Red"; break }
            'WARN' { $currentColor = "Yellow"; break }
            'INFO' { $currentColor = "Cyan"; break }
            'DEBUG' { $currentColor = "Blue"; break }
            'TRACE' { $currentColor = "DarkCyan"; break }
        }
        $matched = $true
    } else { switch -Regex ($line) {
            '^\s*(Exception in thread|[\w.$]+(?:Exception|Error)(?::|$))|심각' { $currentColor = "Red"; $matched = $true; break }
            '\b(WARN|WARNING|Potential)\b|경고' { $currentColor = "Yellow"; $matched = $true; break }
            '\b(SQL|QUERY|sqltiming|HikariPool)\b|Hibernate:|Preparing:|Parameters:' { $currentColor = "DarkYellow"; $matched = $true; break }
            '\b(HTTP|REQUEST|RESPONSE|Mapping|Dispatching)\b' { $currentColor = "Green"; $matched = $true; break }
            '\b(Started|Initializing)\b|정보' { $currentColor = "Cyan"; $matched = $true; break }
    } }

    if ($matched) {
        Write-Host $line -ForegroundColor $currentColor
    } else {
        Write-Host "$esc[2m$line$esc[0m" -ForegroundColor $currentColor
    }
}
$catalinaExitCode = $LASTEXITCODE
if ($null -eq $catalinaExitCode) { $catalinaExitCode = 1 }
exit $catalinaExitCode