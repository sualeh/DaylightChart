@echo off
set BASEPATH=%~dp0
set VERSION=4.1.0
javaw -cp %BASEPATH%lib\ -jar %BASEPATH%lib\daylightchart-%VERSION%.jar
