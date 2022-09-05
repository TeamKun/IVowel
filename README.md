# IVowel
母音しか喋れないプラグイン  

発言すると自動的に平仮名になり母音のみに変換されます  
コンフィグで小文字や記号を残したまま変換や、平仮名のみ入力可能へ変更可能です

# 仕様

コマンド
* vowel
    * error 変換できない文字列(エラー)を無視して変換するかどうか切り替え(デフォルトは無効)
    * lowercase 小文字を残して変換するかどうか切り替え(デフォルトは有効)
    * symbol 記号を残して変換するかどうか切り替え(デフォルトは有効)
    * hover 変換済みのチャットでカーソルを合わせると元の文章等が表示されるか切り替え(デフォルトは無効)
    * hiragana 平仮名以外を受け付けないかどうか(デフォルトは有効)
    * state コンフィグの現在の状態を表示します

チャット以外の金床での名前変更や、看板、本と羽ペンも変換されます
# 例
![KatyouBroken](https://cdn.discordapp.com/attachments/358878159615164416/948839340350513162/2022-03-03_16h08_28.png)

# 利用ライブラリ
以下のライブラリはjarファイル内に同梱されているので別途でダウンロードする必要はありません。

* [Kuromoji](https://github.com/atilika/kuromoji) (漢字読み取得) ([Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0))
* [Ikisugi Logger](https://github.com/TeamKun/IkisugiLogger) (イキスギ起動ログ）
* [ICU4J](https://mvnrepository.com/artifact/com.ibm.icu/icu4j) (ローマ字変換) ([UNICODE, INC. LICENSE](https://raw.githubusercontent.com/unicode-org/icu/main/icu4c/LICENSE))
* [Kanatools](https://github.com/mariten/kanatools-java) (ひらがな、カタカナ変換) ([MIT](https://github.com/mariten/kanatools-java/blob/master/LICENSE))
