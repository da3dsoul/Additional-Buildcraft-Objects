@ECHO OFF

set MODDIR=%~dp0
set MCPDIR=%MODDIR%\..

CALL :COMPILE
CALL :REOBFUSCATE
CALL :PACKAGE

REM ---------------------------------------------------------------------------

:COMPILE
	pushd %MCPDIR%
	cmd /C recompile.bat
	IF ERRORLEVEL 1 GOTO ERROR
	popd
GOTO :EOF

REM ---------------------------------------------------------------------------

:REOBFUSCATE
	pushd %MCPDIR%
	rmdir /S /Q reobf
	cmd /C reobfuscate.bat
	IF ERRORLEVEL 1 GOTO ERROR
	popd
GOTO :EOF

REM ---------------------------------------------------------------------------

:PACKAGE
	del package.zip
	
	SET PATH=%MODDIR%\bin;%PATH%
	
	pushd %MODDIR%
	SETLOCAL enabledelayedexpansion
	
	copy /Y README.txt %MCPDIR%\reobf\minecraft
	
	SET list=README.TXT
	
	for /f %%i in ('find src -type f') do (
		set file=%%i
		echo %%i
	
		if /I "!file:~-5!" EQU ".java" (
			set list=!file:~4,-5!.class !list!
		)
	)
	pushd %MCPDIR%\reobf\minecraft
	rar a %MODDIR%\package.zip %list%
	IF ERRORLEVEL 1 GOTO ERROR
	popd
	
	pause
	
GOTO :EOF

REM ---------------------------------------------------------------------------

:ERROR
pause
popd
EXIT 1
