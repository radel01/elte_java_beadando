:<<"::NONWINDOWSSCRIPT"
@ECHO OFF
GOTO :WINDOWSSCRIPT

::NONWINDOWSSCRIPT
minjavavsn=25
recjavavsn=25

# check Java version
javavsn=`java -fullversion 2>&1 | cut -d'"' -f2 | sed '/^1\./s///' | cut -d'.' -f1 | cut -d'+' -f1`
if [[ "$javavsn" -lt "$minjavavsn" ]]; then
    echo "Exiting    : Java ${minjavavsn}+ required, Java ${javavsn} found"
    echo "Recommended: Remove Java ${javavsn}, install Java ${recjavavsn} from https://www.azul.com/downloads/"
    exit 1
fi

# check required files
missing=0
[ ! -f "junit6all.jar"  ] && echo "Missing file: junit6all.jar" && missing=1
[ ! -f "checkthat6.jar" ] && echo "Missing file: checkthat6.jar" && missing=1
[ ! -f "checkagent6.jar" ] && echo "Missing file: checkagent6.jar" && missing=1
[[ "$missing" -eq "1" ]] && exit 1

if [ $# -ge 2 ]; then
    # check arg file exists
    [ ! -f "$1" ] && echo Missing argument file: "$name" && exit 1

    [[ "$2" == *"/"* ]] && echo "Argument #2 contains invalid text: $2" && exit 1


    TARGET1=$1
    TARGET2=$2
    PROPS=${3-}
    OPTS=${4-}
    MODULEOPTS="-p junit6all.jar --add-modules org.junit.jupiter"

    [[ "$javavsn" -ge 26 ]] && PROPS="--enable-final-field-mutation=ALL-UNNAMED $PROPS"

    if [[ "$TARGET2" == *"StructureTest" ]]; then
        echo "Structural tester: compiling and running $TARGET2"
    elif [[ "$TARGET2" == *"Suite" ]]; then
        echo "Test suite: compiling and running $TARGET2"
    elif [[ "$TARGET2" != *"Test" ]]; then
        echo "This script is only useable with structural/functional/suite testers, not $TARGET2"
        exit 1
    else
        echo "Functional tester: compiling and running $TARGET2"
    fi

    echo javac ${MODULEOPTS} -cp ".:junit6all.jar:checkthat6.jar" "${TARGET1}"
    javac -p junit6all.jar --add-modules org.junit.jupiter -cp ".:junit6all.jar:checkthat6.jar" "${TARGET1}"
    retVal=$?
    [[ $retVal -ne 0 ]] && echo "Exiting: Java compilation failed with error code $retVal" && exit $retVal

    echo java ${PROPS} -javaagent:checkagent6.jar -jar junit6all.jar execute ${OPTS} -E junit-vintage --disable-banner -cp . -cp checkthat6.jar -c "${TARGET2}"
    java ${PROPS} -p junit6all.jar -javaagent:checkagent6.jar -jar junit6all.jar execute ${OPTS} -E junit-vintage --disable-banner -cp . -cp checkthat6.jar -c "${TARGET2}"
    retVal=$?

    exit $retVal
fi

# usage
echo "Usage: check.bat <path of tester file> <tester class> [CheckThat options] [JUnit properties]"
echo "Useful CheckThat options:"
echo "   -Dlang=HU or -Dlang=EN  For structural tester error messages."
echo "   -Dsummary=time/full     Use either time or full to see more JUnit summary details."
echo "   -Derrors=verbose        If you really, REALLY want to see the full stack trace."
echo "Useful JUnit properties:"
echo "   --disable-ansi-colors   For monochrome terminals."
echo
echo "Note: in PowerShell terminals, options have to be surrounded using this very silly syntax:"
echo "   '\"-Dlang=EN\"'"
echo
echo "Note: passing more than one option can be tricky."
echo "Suggested: see the executed java and javac commands and manually modify them."
exit 1


:WINDOWSSCRIPT

set minjavavsn=25
set recjavavsn=25

set "arg1=%1"
if defined arg1 set "arg1=%arg1:"=%"

set "arg2=%2"
if defined arg2 set "arg2=%arg2:"=%"

set "arg3=%3"
if defined arg3 set "arg3=%arg3:"=%"

set "arg4=%4"
if defined arg4 set "arg4=%arg4:"=%"

if "%arg1%"=="" goto USAGE

REM check Java version
for /f tokens^=2^ delims^=.-_+^" %%j in ('java -fullversion 2^>^&1') do set "javavsn=%%j"
if %javavsn% LSS %minjavavsn% (
    echo Exiting    : Java %minjavavsn%+ required, Java %javavsn% found
    echo Recommended: Remove Java %javavsn% (and possible other old versions^), install Java %recjavavsn% from https://www.azul.com/downloads/
    goto END
)

REM check required files
set missing=0
for %%i in ("checkthat6.jar" "checkagent6.jar" "junit6all.jar" "%arg1%") do (
    if not exist "%%i" (
        echo Missing required file: %%i
        set missing=1
    )
)

if "%missing%"=="1" goto END
if "%arg2%"=="" goto USAGE

REM check arg file exists
if not exist "%arg1%" (
    echo Missing argument file: %arg1%
    goto END
)

set inspected=%arg2%
if NOT "%inspected%"=="%inspected:/=%" (
    echo Argument #2 contains invalid text: %arg2%
    goto END
)

if NOT "%inspected%"=="%inspected:\=%" (
    echo Argument #2 contains invalid text: %arg2%
    goto END
)

set TARGET1="%arg1%"
set TARGET2="%arg2%"
set PROPS=
set OPTS=--config=junit.platform.stacktrace.pruning.enabled=true
set MODULEOPTS=-p junit6all.jar --add-modules org.junit.jupiter
if not "%arg3%"=="" (
    set PROPS="%arg3%"
)
if not "%arg4%"=="" (
    set OPTS="%arg4%"
)

if %javavsn% GEQ 26 (
    set PROPS="--enable-final-field-mutation=ALL-UNNAMED %PROPS%"
)


if not "x%TARGET2:StructureTest=%"=="x%TARGET2%" (
    echo Structural tester: compiling and running %TARGET2%
    set ENGINE="junit-jupiter"
) else (
    if not "x%TARGET2:Suite=%"=="x%TARGET2%" (
        echo Test suite: compiling and running %TARGET2%
        set ENGINE="junit-platform-suite"
    ) else (
        if "x%TARGET2:Test=%"=="x%TARGET2%" (
            echo This script is only useable with structural/functional/suite testers, not %arg2%
            goto END
        ) else (
            echo Functional tester: compiling and running %TARGET2%
            set ENGINE="junit-jupiter"
        )
    )
)


echo ^> javac %MODULEOPTS% -cp ".;junit6all.jar;checkthat6.jar" %TARGET1%
javac %MODULEOPTS% -cp ".;junit6all.jar;checkthat6.jar" %TARGET1%

: Stops the batch file if there was a compilation error
if %ERRORLEVEL% NEQ 0 (
    echo Exiting: Java compilation failed with error code %ERRORLEVEL%
    exit /b %ERRORLEVEL%
)

: The java command requires mode.exe, this handles if it is missing from the PATH
echo %PATH% | find /I "C:\Windows\System32" >nul || \
    set PATH=%PATH%;C:\Windows\System32;C:\Windows\SysWOW64

@del /f solution.zip 2>NUL >NUL

if exist "C:\Program Files\7-Zip\7z.exe" (
    @"C:\Program Files\7-Zip\7z.exe" a -bso0 -bsp0 -r -tzip -xr^!"*.jar" -xr^!"*.class" -xr^!"*.cmd" -xr^!"*.zip" -xr^!"*-docs*" -xr^!"CheckThat.txt" solution.zip *.* 2^>^&1 ^>NUL
) else (
    @powershell Compress-Archive -DestinationPath solution.zip -Force True -Path . 2^>^&1 ^>NUL
)


set "FILETARGET=%TARGET2:.=\%.class"
set "FILETARGET=%FILETARGET:"=%"
if not exist "%FILETARGET%" (
    echo Cannot find compiled class for fully qualified name: %TARGET2%
    set missing=1
)

if "%missing%"=="1" goto END


echo ^> java %PROPS% -javaagent:checkagent6.jar -p junit6all.jar -jar junit6all.jar execute %OPTS% -e %ENGINE% --disable-banner -cp . -cp checkthat6.jar -c %TARGET2%
java %PROPS% -javaagent:checkagent6.jar -p junit6all.jar -jar junit6all.jar execute %OPTS% -e %ENGINE% --disable-banner -cp . -cp checkthat6.jar -c %TARGET2%

@ECHO OFF
goto END


:USAGE
echo Usage: check.bat ^<path of tester file^> ^<tester class^> [CheckThat options] [JUnit properties]
echo Useful CheckThat options:
echo    -Dlang=HU or -Dlang=EN  For structural tester error messages.
echo    -Dsummary=time/full     Use either time or full to see more JUnit summary details.
echo    -Derrors=verbose        If you really, REALLY want to see the full stack trace.
echo Useful JUnit properties:
echo    --disable-ansi-colors   For monochrome terminals.
echo[
echo Note: in PowerShell terminals, options have to be surrounded using this very silly syntax:
echo    '"-Dlang=EN"'
echo[
echo Note: passing more than one option can be tricky.
echo Suggested: see the executed java and javac commands and manually modify them.

:END
