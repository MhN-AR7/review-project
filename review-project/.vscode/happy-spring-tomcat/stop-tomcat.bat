@echo off
chcp 65001 > nul
echo ====================================================
echo Stopping Tomcat...
echo ====================================================

powershell -NoProfile -ExecutionPolicy Bypass -File "%~dp0stop-owned-process.ps1" -Ports "8000,8080" -CatalinaBase "/home/falcons/.config/Code/User/workspaceStorage/75525f1fe6141f7fa1b57db5d68c0066/skay138.happy-spring-tomcat/tomcat-base"
if errorlevel 1 goto :stop_failed
echo Tomcat stopped cleanly.
exit /b 0

:stop_failed
echo WARNING: Tomcat was not stopped.
exit /b 1
