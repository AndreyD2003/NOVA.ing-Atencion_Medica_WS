@echo off
echo Iniciando todos los microservicios de NOVA...

start "NOVA - Medico (8080)" cmd /k "cd /d %~dp0msvc-medico && mvn spring-boot:run"
timeout /t 5
start "NOVA - Cita (8081)" cmd /k "cd /d %~dp0msvc-cita && mvn spring-boot:run"
start "NOVA - Diagnostico (8082)" cmd /k "cd /d %~dp0msvc-diagnostico && mvn spring-boot:run"
start "NOVA - Paciente (8083)" cmd /k "cd /d %~dp0msvc-paciente && mvn spring-boot:run"
start "NOVA - Web Semantica (8084)" cmd /k "cd /d %~dp0msvc-web-semantica && mvn spring-boot:run"

echo.
echo Todos los microservicios se estan iniciando en ventanas separadas.
echo Por favor espera a que todos esten listos antes de usar el frontend.
echo.
pause
