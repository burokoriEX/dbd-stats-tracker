# DbD 統計トラッカー（Java Swing）

Dead by Daylight のキラー戦績を記録・管理するための Java Swing アプリです。  
試合結果の記録、キラーごとの平均キル数の可視化、データ管理を簡単に行えます。

---

## 📌 主な機能

- キラー別の戦績記録（キル数・試合数）
- 日付ごとの平均キル数グラフ表示（JFreeChart）
- キラー一覧の追加・削除
- データの自動保存（dbd_history.txt / dbd_killers.txt）
- シンプルな GUI（Java Swing）

---

## 📁 プロジェクト構成

DBD/
├─ run.bat             # プログラム起動
├─ src/                # ソースコード
├─ libs/               # 使用ライブラリ（JFreeChart / JCommon）
└─ （データファイルは .gitignore により除外）

---

run.batをダブルクリックすることで実行できます。

## ▶ 実行方法

Java 17 以上で動作します。

1. リポジトリをクローン  

git clone https://github.com/burokoriEX/dbd-stats-tracker.git (github.com in Bing)

2. `libs` の jar をクラスパスに追加してコンパイル
javac -cp "./libs/*" ./src/DBDdataKAI3.java

3. 実行  
java -cp "./libs/*:./src" DBDdataKAI3

---

## 📦 使用ライブラリ

- **JFreeChart 1.0.19**  
- **JCommon 1.0.23**

---

## 📝 ライセンス

このプロジェクトは **MIT License** のもとで公開されています。

---

## 💬 作者

- **burokoriEX（雅人）**
- Java / Swing / JFreeChart を使用した個人開発ツール
