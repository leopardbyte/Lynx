@echo off
set source="C:\Users\jonat\Desktop\forgy\build\libs"
set destination="C:\Users\jonat\AppData\Roaming\.minecraft\mods"

echo Moving files from %source% to %destination%
move /Y %source%\* %destination%

if %errorlevel% equ 0 (
    echo Files moved successfully.
) else (
    echo Error occurred while moving files.
)
pause
