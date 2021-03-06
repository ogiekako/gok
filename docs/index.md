# Gok

Go Kompaira.

## Links

- [Compiler video lecture](https://www.youtube.com/playlist?list=PLFB9EC7B8FE963EB8)
- [MIPS instruction](http://logos.cs.uic.edu/366/notes/mips%20quick%20tutorial.htm)
- [MIPS assebmly language](https://en.wikipedia.org/wiki/MIPS_instruction_set#MIPS_assembly_language)
    - [Register](https://en.wikipedia.org/wiki/MIPS_instruction_set#Compiler_register_usage)
    - Instructions
        - [Integer](https://en.wikipedia.org/wiki/MIPS_instruction_set#Integer)
        - [Floating point](https://en.wikipedia.org/wiki/MIPS_instruction_set#Floating_point)
        - [Pseudo instructions](https://en.wikipedia.org/wiki/MIPS_instruction_set#Pseudo_instructions)
        - [Other instructions](https://en.wikipedia.org/wiki/MIPS_instruction_set#Other_instructions)
- Go language
    - [Language Specification](https://golang.org/ref/spec)

## 日記

### 2016-09-03

型を、実行時に持たせるのってどうやるんだろう。

`return` 文をサポート。

### 2016-09-02

if 文 をサポートした。(else はまだ)

```
func f(n int) int {
  res := n
  if n < 10 {
    res := res * f(n + 1)
  }
  res
}
func main() int {
  f(1)
}```
を食わせると、10! = 3628800 を出力する。

一段落したので、いろいろクリーンアップしよう。
`return` のサポート、引数を任意個にする、`-` のサポートあたりがあるとテストを書くときに嬉しい。

大きな TODO としては、ポインタ、reflection, Struct あたりかな。
あと、go routine はどうやって実装するのかわかってない。

### 2016-09-01

false, true の追加。

`<` をサポート。

### 2016-08-31
1変数の関数呼び出しができた。

```
func g(i int) int {
i*2
}
func f(i int) int {
g(g(g(i)))
}
func main() int {
f(3)
}
```
みたいなのを食わせると動く。次は if 文をサポートしたいが、そのまえに bool type を入れないと。とりあえず `<` だけ入れよう。

### 2016-08-30

Lecture 8 までみて、SLR(1) 文法を理解した。
与えられた文字列を左から見ていって、Shift/Reduce を決める。[8-5](https://www.youtube.com/watch?v=t4p3au3dsz0&list=PLFB9EC7B8FE963EB8&index=38)
 から 8-7 が Examples で、これだけみればわかるかもしれない。

関数定義を入れた。呼び出しはまだ。

### 2016-08-29
handle という概念がなかなか難しい。
LR 文法を理解するのに必要っぽい。https://www.youtube.com/watch?v=UeRyF72ObXo&index=35&list=PLFB9EC7B8FE963EB8

それはともかくとして、変数への代入をサポートした。以下が動く。最後の行だけ非代入文にすると、その結果を出力する感じ。

```
v:=2
u:=3
u*v   // -> 6
```

いまは、例えば、上の2行めに、u の仕様が入ってもエラーにならないので、これをエラーにする必要がある。
Go だとタプルへの代入もできるけど、まあこれはテキトーにやればなんとかなりそう。
最後を特別扱いしているのが気持ち悪いので、fmt.Println をとりあえずビルトインとしてサポートしようかな。

### 2016-08-28

AST 作るようにした。
\+ はサポートしたけど、流石に \* とカッコは入れて、電卓にしたい。
電卓はLL(0) だからテキトーに実装する。

その次の目標は関数呼び出し。
if 文が作れると、fib とかできるはずだからそこまでやりたい気分。

`-2147483648` とか面倒だなと思ったらそれだけじゃなくて、Go言語では定数畳み込みまでするので、以下の式は valid らしい。
`fmt.Println(-(2147483649/2*2))`.
`fmt.Println(-(2147483649*20000000000000000000000000/30000000000000000000000000))` ですら通る。まあこれはTODOとしておこう。
とりあえず、`-2147483648` だけアドホックに対処した。

トークナイズしてからAST作るようにした。
カッコ、+、* をサポートした電卓ができた。あと単項 -, + も使える。
型を意識して、"A" + 1 がコンパイルエラーになると良いな。
一段落したから、このドキュメントをサーブするのと、テストを自動化するのをやろう。

### 2016-08-27

rui314 さんの影響をうけてコンパイラを作ろうと思った。
スタンフォードの Open Course Online のコンパイラの授業を見つけたので最初の 6 講くらい見てみた。最後まで見よう。
https://www.youtube.com/watch?v=i1teQEY5SFY&list=PLFB9EC7B8FE963EB8&index=23

この授業で扱われてるし、アセンブリは MIPS にした。spim を入れるひつようがある。`brew install spim` 
ビルドは bazel で。`brew install bazel`
とりあえず、文字列を食わせるとそれを出力するプログラムを吐くものを作った。
test.sh でテストする。

数字も入れた。結構大きい数でも即値に出来てるけどいいのかな、と思ったけど、li というのは real instruction じゃないのか。
https://en.wikipedia.org/wiki/MIPS_instruction_set#Pseudo_instructions
`li $rd, IMMED[31:0]`
は、
```
lui $rd, IMMED[31:16]
 ori $rd,$rd, IMMED[15:0]
```
に変換される。
