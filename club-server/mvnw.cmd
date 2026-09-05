@echo off
setlocal

rem ============================================================
rem Maven Wrapper: first run downloads Apache Maven automatically
rem ============================================================
set "MAVEN_VERSION=3.8.8"
set "MAVEN_HOME=%USERPROFILE%\.m2\wrapper\dists\apache-maven-%MAVEN_VERSION%"
set "MVN_CMD=%MAVEN_HOME%\bin\mvn.cmd"

if exist "%MVN_CMD%" goto run

echo Downloading Apache Maven %MAVEN_VERSION% ...
set "ZIP_URL=https://maven.aliyun.com/repository/central/org/apache/maven/apache-maven/%MAVEN_VERSION%/apache-maven-%MAVEN_VERSION%-bin.zip"
set "ZIP_FILE=%TEMP%\apache-maven-%MAVEN_VERSION%-bin.zip"
set "TMP_DIR=%TEMP%\apache-maven-extract-%RANDOM%"

powershell -NoProfile -ExecutionPolicy Bypass -Command "Invoke-WebRequest -Uri '%ZIP_URL%' -OutFile '%ZIP_FILE%'; Expand-Archive -Force -Path '%ZIP_FILE%' -DestinationPath '%TMP_DIR%'"
if errorlevel 1 (
    echo Failed to download Maven. Please check your network.
    exit /b 1
)

if not exist "%MAVEN_HOME%" mkdir "%MAVEN_HOME%"
robocopy "%TMP_DIR%\apache-maven-%MAVEN_VERSION%" "%MAVEN_HOME%" /E /NFL /NDL /NJH /NJS >nul
del /q "%ZIP_FILE%" >nul 2>&1
rd /s /q "%TMP_DIR%" >nul 2>&1
echo Maven %MAVEN_VERSION% installed to %MAVEN_HOME%

:run
"%MVN_CMD%" %*
exit /b %errorlevel%