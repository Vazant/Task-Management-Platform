@echo off
echo Running Java Code Quality Checks...
echo.

echo 1. Running PMD code analysis...
mvn pmd:check
if %ERRORLEVEL% NEQ 0 (
    echo.
    echo ❌ PMD check failed! Please fix the issues above.
    echo.
    pause
    exit /b 1
)

echo.
echo 2. Running Checkstyle code style check...
mvn checkstyle:check
if %ERRORLEVEL% NEQ 0 (
    echo.
    echo ❌ Checkstyle check failed! Please fix the issues above.
    echo.
    pause
    exit /b 1
)

echo.
echo ✅ All code quality checks completed successfully!
echo.
pause
