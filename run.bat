@echo off
setlocal
echo Starting Dream Fly Application (Portable Mode)...
echo.

set "PROJECT_DIR=%~dp0"
set "MAVEN_HOME=%PROJECT_DIR%apache-maven-3.9.6"
set "JAVA_HOME=%PROJECT_DIR%jdk-17.0.2"

REM Update PATH to include local Java and Maven
set "PATH=%JAVA_HOME%\bin;%MAVEN_HOME%\bin;%PATH%"

echo Environment Configured:
echo JAVA_HOME: %JAVA_HOME%
echo MAVEN_HOME: %MAVEN_HOME%

REM Verification check
java -version >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo Error: Embedded Java not found at %JAVA_HOME%
    pause
    exit /b
)

call mvn -version >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo Error: Embedded Maven not found at %MAVEN_HOME%
    pause
    exit /b
)

echo.
echo Compiling...
call mvn clean package -DskipTests
if %ERRORLEVEL% NEQ 0 (
    echo.
    echo BUILD FAILED!
    echo Please check the error messages above.
    pause
    exit /b
)

echo.
echo Running Application...
call mvn javafx:run

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo Application crashed or failed to start.
    pause
)
