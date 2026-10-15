param(
    [Parameter(Mandatory = $true)][string]$Ports,
    [Parameter(Mandatory = $true)][string]$CatalinaBase
)

$requestedPorts = @($Ports.Split(',') | ForEach-Object { [int]$_.Trim() })
$listenersByProcess = @{}
& "$env:SystemRoot\System32\netstat.exe" -ano -p TCP | ForEach-Object {
    $columns = $_.Trim() -split '\s+'
    if ($columns.Count -ge 5 -and $columns[0] -eq 'TCP' -and $columns[3] -eq 'LISTENING' -and $columns[1] -match ':(\d+)$') {
        $listenerPort = [int]$Matches[1]
        if ($requestedPorts -contains $listenerPort) {
            $processId = [int]$columns[4]
            if (!$listenersByProcess.ContainsKey($processId)) { $listenersByProcess[$processId] = @() }
            $listenersByProcess[$processId] += $listenerPort
        }
    }
}
$foreignFound = $false
$expectedBase = [IO.Path]::GetFullPath($CatalinaBase).TrimEnd([IO.Path]::DirectorySeparatorChar, [IO.Path]::AltDirectorySeparatorChar)
foreach ($owningProcessId in $listenersByProcess.Keys) {
    $listenerPorts = ($listenersByProcess[$owningProcessId] | Sort-Object -Unique) -join ', '
    $process = Get-CimInstance Win32_Process -Filter "ProcessId = $owningProcessId" -ErrorAction SilentlyContinue
    $commandLine = if ($process) { [string]$process.CommandLine } else { "" }
    $baseMatch = [regex]::Match($commandLine, '-Dcatalina\.base=(?:"([^"]+)"|(\S+))', [Text.RegularExpressions.RegexOptions]::IgnoreCase)
    $processBase = if ($baseMatch.Success) {
        $rawBase = if ($baseMatch.Groups[1].Success) { $baseMatch.Groups[1].Value } else { $baseMatch.Groups[2].Value }
        try { [IO.Path]::GetFullPath($rawBase).TrimEnd([IO.Path]::DirectorySeparatorChar, [IO.Path]::AltDirectorySeparatorChar) } catch { "" }
    } else { "" }
    if ([string]::Equals($processBase, $expectedBase, [StringComparison]::OrdinalIgnoreCase)) {
        Write-Host "Stopping Happy Spring Tomcat PID: $owningProcessId (ports $listenerPorts)"
        try {
            Stop-Process -Id $owningProcessId -Force -ErrorAction Stop
        } catch {
            Write-Warning "Failed to stop Happy Spring Tomcat PID $($owningProcessId): $($_.Exception.Message)"
            $foreignFound = $true
        }
    } else {
        Write-Warning "Port(s) $listenerPorts belong to another process (PID $owningProcessId); it was not stopped."
        $foreignFound = $true
    }
}
if ($foreignFound) { exit 2 }
exit 0
