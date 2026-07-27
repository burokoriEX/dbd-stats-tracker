@echo off
chcp 65001 > nul

echo --------------------------------------------------
echo  DbD統計トラッカー を起動しています...
echo --------------------------------------------------

echo 【1/2】 プログラムをコンパイル中...

rem ★ src 内の全 Java ファイルをコンパイル
javac -cp "./libs/*" ./src/*.java

if %errorlevel% neq 0 (
    echo.
    echo [エラー] コンパイルに失敗しました。
    echo コードにエラーがないか確認してください。
    echo Java(JDK 17以上)がインストールされているかも確認してください。
    echo.
    pause
    exit /b
)

echo 【2/2】 アプリケーションを起動中...

rem ★ libs と src をクラスパスに追加
java -cp "./libs/*;./src" DBDStats

if %errorlevel% neq 0 (
    echo.
    echo [エラー] 起動に失敗しました。
    echo.
    pause
)
