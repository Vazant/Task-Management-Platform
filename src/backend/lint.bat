@echo off
echo Running Java Linter (PMD)...
mvn pmd:check
if %ERRORLEVEL% EQU 0 (
    echo.
    echo ✅ All code passes linting!
    echo.
) else (
    echo.
    echo ❌ Linting failed! Please fix the issues above.
    echo.
)
pause
