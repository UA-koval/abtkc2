@rem
@rem Copyright 2015 the original author or authors.
@rem
@rem Licensed under the Apache License, Version 2.0 (the "License");
@rem you may not use this file except in compliance with the License.
@rem You may obtain a copy of the License at
@rem
@rem      https://www.apache.org/licenses/LICENSE-2.0
@rem
@rem Unless required by applicable law or agreed to in writing, software
@rem distributed under the License is distributed on an "AS IS" BASIS,
@rem WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
@rem See the License for the specific language governing permissions and
@rem limitations under the License.
@rem

@if "%DEBUG%" == "" @echo off
@rem ##########################################################################
@rem
@rem  abtkc2.0 startup script for Windows
@rem
@rem ##########################################################################

@rem Set local scope for the variables with windows NT shell
if "%OS%"=="Windows_NT" setlocal

set DIRNAME=%~dp0
if "%DIRNAME%" == "" set DIRNAME=.
set APP_BASE_NAME=%~n0
set APP_HOME=%DIRNAME%..

@rem Resolve any "." and ".." in APP_HOME to make it shorter.
for %%i in ("%APP_HOME%") do set APP_HOME=%%~fi

@rem Add default JVM options here. You can also use JAVA_OPTS and ABTKC2_0_OPTS to pass JVM options to this script.
set DEFAULT_JVM_OPTS=

@rem Find java.exe
if defined JAVA_HOME goto findJavaFromJavaHome

set JAVA_EXE=java.exe
%JAVA_EXE% -version >NUL 2>&1
if "%ERRORLEVEL%" == "0" goto execute

echo.
echo ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH.
echo.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation.

goto fail

:findJavaFromJavaHome
set JAVA_HOME=%JAVA_HOME:"=%
set JAVA_EXE=%JAVA_HOME%/bin/java.exe

if exist "%JAVA_EXE%" goto execute

echo.
echo ERROR: JAVA_HOME is set to an invalid directory: %JAVA_HOME%
echo.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation.

goto fail

:execute
@rem Setup the command line

set CLASSPATH=%APP_HOME%\lib\abtkc2.0-2.0.jar;%APP_HOME%\lib\lavaplayer-2.2.1.jar;%APP_HOME%\lib\json-20240303.jar;%APP_HOME%\lib\v2-1.4.0.jar;%APP_HOME%\lib\javacord-core-3.8.0.jar;%APP_HOME%\lib\javacord-api-3.8.0.jar;%APP_HOME%\lib\common-1.4.0.jar;%APP_HOME%\lib\rhino-engine-1.7.15.jar;%APP_HOME%\lib\nanojson-1.7.jar;%APP_HOME%\lib\lavaplayer-natives-2.2.1.jar;%APP_HOME%\lib\lava-common-2.2.1.jar;%APP_HOME%\lib\slf4j-api-2.0.7.jar;%APP_HOME%\lib\commons-io-2.13.0.jar;%APP_HOME%\lib\jsoup-1.16.1.jar;%APP_HOME%\lib\base64-2.3.9.jar;%APP_HOME%\lib\logging-interceptor-4.9.3.jar;%APP_HOME%\lib\okhttp-4.9.3.jar;%APP_HOME%\lib\okio-jvm-2.8.0.jar;%APP_HOME%\lib\kotlin-stdlib-jdk8-1.4.10.jar;%APP_HOME%\lib\kotlin-stdlib-jdk7-1.4.10.jar;%APP_HOME%\lib\kotlin-stdlib-1.4.10.jar;%APP_HOME%\lib\annotations-24.0.0.jar;%APP_HOME%\lib\httpclient-4.5.14.jar;%APP_HOME%\lib\jackson-annotations-2.15.2.jar;%APP_HOME%\lib\jackson-databind-2.15.2.jar;%APP_HOME%\lib\jackson-core-2.15.2.jar;%APP_HOME%\lib\nv-websocket-client-2.14.jar;%APP_HOME%\lib\xsalsa20poly1305-0.11.0.jar;%APP_HOME%\lib\log4j-api-2.17.2.jar;%APP_HOME%\lib\vavr-0.10.4.jar;%APP_HOME%\lib\rhino-1.7.15.jar;%APP_HOME%\lib\httpcore-4.4.16.jar;%APP_HOME%\lib\commons-logging-1.2.jar;%APP_HOME%\lib\commons-codec-1.11.jar;%APP_HOME%\lib\bcprov-jdk15on-1.60.jar;%APP_HOME%\lib\vavr-match-0.10.4.jar;%APP_HOME%\lib\kotlin-stdlib-common-1.4.10.jar


@rem Execute abtkc2.0
"%JAVA_EXE%" %DEFAULT_JVM_OPTS% %JAVA_OPTS% %ABTKC2_0_OPTS%  -classpath "%CLASSPATH%" com.discordbot.Main %*

:end
@rem End local scope for the variables with windows NT shell
if "%ERRORLEVEL%"=="0" goto mainEnd

:fail
rem Set variable ABTKC2_0_EXIT_CONSOLE if you need the _script_ return code instead of
rem the _cmd.exe /c_ return code!
if  not "" == "%ABTKC2_0_EXIT_CONSOLE%" exit 1
exit /b 1

:mainEnd
if "%OS%"=="Windows_NT" endlocal

:omega
