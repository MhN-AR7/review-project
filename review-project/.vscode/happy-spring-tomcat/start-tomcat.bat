@echo off
chcp 65001 > nul
echo ====================================================
echo Starting Tomcat in DEBUG mode (Port 8000)...
echo HTTP Port: 8080
echo Context Path: ""
echo ====================================================
echo Cleaning up previous Tomcat instances...
powershell -NoProfile -ExecutionPolicy Bypass -File "%~dp0stop-owned-process.ps1" -Ports "8000,8080" -CatalinaBase "/home/falcons/.config/Code/User/workspaceStorage/75525f1fe6141f7fa1b57db5d68c0066/skay138.happy-spring-tomcat/tomcat-base"
if errorlevel 1 exit /b 1
timeout /t 1 /nobreak > nul

set "JAVA_OPTS=-Dfile.encoding=UTF-8 -Dsun.stdout.encoding=UTF-8 -Dsun.stderr.encoding=UTF-8"
set "CATALINA_HOME=/home/falcons/Desktop/Maktab Sharif/java-server/apache-tomcat-11.0.25"
set "CATALINA_BASE=/home/falcons/.config/Code/User/workspaceStorage/75525f1fe6141f7fa1b57db5d68c0066/skay138.happy-spring-tomcat/tomcat-base"
set "JPDA_ADDRESS=127.0.0.1:8000"

echo Tomcat is launching (HTTP Port 8080)...
powershell -NoProfile -ExecutionPolicy Bypass -File "%~dp0colorize-logs.ps1" -CatalinaScript "%CATALINA_HOME%\bin\catalina.bat"
set "HST_CATALINA_EXIT=%ERRORLEVEL%"
if exist "%~dp0restart-requested" (
    del /Q "%~dp0restart-requested" > nul 2>&1
    echo Tomcat start was interrupted by Restart.
    exit /b 0
)
echo Tomcat process exited.
exit /b %HST_CATALINA_EXIT%
