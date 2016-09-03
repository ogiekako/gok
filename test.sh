#!/bin/bash

set -e

function test() {
  input="$1"
  expected="$2"
  echo "testing: $input" 1>&2

  echo -n "$input" | bazel-bin/src/Main > /tmp/out.s
  spim -file /tmp/out.s 2> /tmp/err | tail -n +2 > /tmp/actual
  if [[ -s /tmp/err ]]; then
    cat /tmp/err 1>&2
    echo "Failed: Invalid MIPS program." && exit 1
  fi
  echo -n "$expected" > /tmp/expected
  diff /tmp/actual /tmp/expected || (echo "Failed: Unexpected output." && exit 1)
}

function test_main() {
  input="func main() $3 {
$1
}"
  test "$input" "$2"
}

bazel build src/Main

test_main "true" "true" bool
test_main "false" "false" bool
test_main '"A"' "A" string
test_main '"Hello, World!\n"' "Hello, World!
" string
test_main 1 1 int
test_main 2147483647 2147483647 int
test_main -2147483648 -2147483648 int

test_main "1<1" false bool
test_main "1<2" true bool
test_main "1+1" 2 int
test_main "1+1+2" 4 int
test_main "1+3  + 2" 6 int

test_main "2*3" 6 int
test_main "1+2*3" 7 int
test_main "2*3+4" 10 int
test_main "(1+2)*3" 9 int
test_main "(-1+ + - +2)*3" -9 int
test_main "-1*-1" 1 int
test_main "-2*-(1+2)" 6 int
test_main "v:=1
v" 1 int
test_main "v:=2
u:=3
u" 3 int
test_main "v:=2
u:=3
v" 2 int
test_main "v:=2
u:=3
u*v" 6 int
test_main "v:=0
if 1 < 2 {
 v:=v+1
}
v" 1 int

test "func f(i int) int {
 i
}
func main() int {
f(42)
}" 42

test "func g(i int) int {
i+1
}
func f(i int) int {
i+g(i*3)
}
func main() int {
f(2)
}" 9

test "func g(i int) int {
i*2
}
func f(i int) int {
g(g(g(i)))
}
func main() int {
f(3)
}" 24

test "func f(i bool) bool {
true
}
func main() bool {
f(false)
}" true

test "func f(i int) bool {
i < 42
}
func main() bool {
f(42)
}" false

test "
func f(n int) int {
  res := n
  if n < 10 {
    res := res * f(n + 1)
  }
  res
}
func main() int {
  f(1)
}" 3628800

echo "OK"
