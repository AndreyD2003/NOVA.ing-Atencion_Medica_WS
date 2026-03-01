@echo off
setlocal

cd /d "%~dp0nova-frontend"

echo Iniciando frontend con Vite en http://localhost:5173/ ...
echo Asegurate de que msvc-web-semantica este corriendo en http://localhost:8084
echo.

npm run dev -- --host

endlocal
