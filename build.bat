@echo off
cd %~dp0
gradlew setupDevWorkspace setupDecompWorkspace assemble
pause