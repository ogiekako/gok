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

test_main '"A"' "A" string
test_main '"Hello, World!\n"' "Hello, World!
" string
test_main 1 1 int
test_main 2147483647 2147483647 int
test_main -2147483648 -2147483648 int

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

test "func f(i int) int {
 i+1
}
func main() int {
 i := 1
 i+1
}" 2

echo "OK"
