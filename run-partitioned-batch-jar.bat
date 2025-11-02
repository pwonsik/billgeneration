@echo off
setlocal enabledelayedexpansion

REM Partitioner 기반 배치 JAR 실행 스크립트 (Windows)

echo === Partitioner 기반 배치 JAR 실행 스크립트 ===
echo.

REM 기본 파라미터 설정
set DEFAULT_START_DATE=2025-03-01
set DEFAULT_END_DATE=2025-03-31
set DEFAULT_THREAD_COUNT=8
set DEFAULT_BILLING_CALCULATION_TYPE=B0
set DEFAULT_BILLING_CALCULATION_PERIOD=0
set DEFAULT_INV_OPER_CYCL_CD=01
set JOB_NAME=billOperNumAssignJob
set THREAD_COUNT=4
set INV_OPER_CYCL_CD=01


echo.
echo === Partitioner 배치 파라미터 ===
echo Job 이름: %JOB_NAME%
echo 파티션 수 (쓰레드 수): %THREAD_COUNT% (application property 오버라이드)
echo 청구 작업 주기 코드: %INV_OPER_CYCL_CD%

REM JAR 파일 빌드
echo JAR 파일을 빌드합니다...
call gradlew.bat :batch:bootJar -x test

if errorlevel 1 (
    echo JAR 빌드에 실패했습니다.
    exit /b 1
)

REM JAR 파일 위치
set JAR_FILE=batch\build\libs\batch-0.0.1-SNAPSHOT.jar

if not exist "%JAR_FILE%" (
    echo JAR 파일을 찾을 수 없습니다: %JAR_FILE%
    exit /b 1
)

REM 중복 실행 방지를 위한 현재 시각 타임스탬프
for /f "tokens=2 delims==" %%I in ('wmic os get localdatetime /value') do set datetime=%%I
set TIMESTAMP=%datetime:~0,14%

REM 배치 실행 명령어 생성
set BATCH_COMMAND=java -jar %JAR_FILE% --spring.batch.job.names=%JOB_NAME% batch.thread-count=%THREAD_COUNT% invOperCyclCd=%INV_OPER_CYCL_CD%

echo 실행 명령어: !BATCH_COMMAND!
echo.

REM 배치 실행
echo === Partitioner 배치 실행 시작 ===
echo 각 파티션이 독립적으로 처리됩니다...
echo.

!BATCH_COMMAND!

echo.
echo === Partitioner 배치 실행 완료 ===

endlocal
