# Links

- [Compiler video lecture](https://www.youtube.com/playlist?list=PLFB9EC7B8FE963EB8)
- [MIPS instruction](http://logos.cs.uic.edu/366/notes/mips%20quick%20tutorial.htm)
- [MIPS assebmly language](https://en.wikipedia.org/wiki/MIPS_instruction_set#MIPS_assembly_language)
  - [Register](https://en.wikipedia.org/wiki/MIPS_instruction_set#Compiler_register_usage)
  - Instructions
    - [Integer](https://en.wikipedia.org/wiki/MIPS_instruction_set#Integer)
    - [Floating point](https://en.wikipedia.org/wiki/MIPS_instruction_set#Floating_point)
    - [Pseudo instructions](https://en.wikipedia.org/wiki/MIPS_instruction_set#Pseudo_instructions)
    - [Other instructions](https://en.wikipedia.org/wiki/MIPS_instruction_set#Other_instructions)

# 日記

## 2016-08-27

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