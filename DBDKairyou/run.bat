@echo off
chcp 65001 > nul

rem ★ run.bat の場所をカレントにする（重要）
cd /d "%~dp0"

echo ================================
echo   DbD 統計トラッカー 起動
echo ================================

echo --- Java の動作チェック ---
javac -version
if %errorlevel% neq 0 (
    echo [エラー] javac が実行できません。
    pause
    exit /b
)

java -version
if %errorlevel% neq 0 (
    echo [エラー] java が実行できません。
    pause
    exit /b
)

echo --- コンパイル開始 ---
javac -cp "libs/*" src/*.java
if %errorlevel% neq 0 (
    echo [エラー] コンパイルに失敗しました。
    pause
    exit /b
)

echo --- アプリケーション起動 ---
java -cp "libs/*;src" DBDstats
if %errorlevel% neq 0 (
    echo [エラー] アプリケーション起動に失敗しました。
    pause
    exit /b
)

rem ★ 正常終了時は止めない
exit /b
