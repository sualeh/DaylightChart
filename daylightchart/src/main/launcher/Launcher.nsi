; Java Launcher
;--------------

;You want to change the next four lines
Name "Daylight Chart"
Icon "daylightchart.ico"
OutFile "DaylightChart.exe"

RequestExecutionLevel user
SilentInstall silent
AutoCloseWindow true
ShowInstDetails nevershow

;You want to change the next two lines too
!define CLASSPATH "./lib/daylightchart-3.2.jar"
!define MAINCLASS "daylightchart.LauncherMain"
!define ARGS "daylightchart.Main --addclasspath ./lib"

Section ""
  Call GetJRE
  Pop $R0

  StrCpy $0 '"$R0" -classpath "${CLASSPATH}" ${MAINCLASS} ${ARGS}'

  SetOutPath $EXEDIR
  ExecWait $0
SectionEnd

Function GetJRE
;
;  Find JRE (javaw.exe)
;  1 - in .\jre directory (JRE Installed with application)
;  2 - in JAVA_HOME environment variable
;  3 - in the registry
;  4 - assume javaw.exe in current dir or PATH

  Push $R0
  Push $R1

  ClearErrors
  StrCpy $R0 "$EXEDIR\jre\bin\javaw.exe"
  IfFileExists $R0 JreFound
  StrCpy $R0 ""

  ClearErrors
  ReadEnvStr $R0 "JAVA_HOME"
  StrCpy $R0 "$R0\bin\javaw.exe"
  IfErrors 0 JreFound

  ClearErrors
  ReadRegStr $R1 HKLM "SOFTWARE\JavaSoft\Java Runtime Environment" "CurrentVersion"
  ReadRegStr $R0 HKLM "SOFTWARE\JavaSoft\Java Runtime Environment\$R1" "JavaHome"
  StrCpy $R0 "$R0\bin\javaw.exe"

  IfErrors 0 JreFound
  StrCpy $R0 "javaw.exe"

 JreFound:
  Pop $R1
  Exch $R0
FunctionEnd