@echo off
chcp 65001 > nul
echo --------------------------------------------------
echo  DbD統計トラッカー を起動しています...
echo --------------------------------------------------

echo 【1/2】 プログラムをコンパイル中...
javac -cp "./libs/*" ./src/DBDstats.java

if %errorlevel% neq 0 (
    echo.
    echo [エラー] コンパイルに失敗しました。
    echo パソコンに Java (JDK 17以上) がインストールされているか確認してください。
    echo.
    pause
    exit /b
)

echo 【2/2】 アプリケーションを起動中...
java -cp "./libs/*;./src" DBDstats3

if %errorlevel% neq 0 (
    echo.
    echo [エラー] 起動に失敗しました。
    echo.
    pause
)
