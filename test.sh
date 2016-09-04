#!/bin/bash

set -e

function usage_exit() {
        echo "Usage: $0 [-d]" 1>&2
        exit 1
}

debug=0
while getopts d OPT; do
    case $OPT in
        d)  debug=1
            ;;
        h)  usage_exit
            ;;
        \?) usage_exit
            ;;
    esac
done

shift $((OPTIND - 1))

function test() {
  input="$1"
  expected="$2"
  if [[ $debug -gt 0 ]]; then
    echo "testing: $input" 1>&2
  fi

  echo -n "$input" | bazel-bin/src/Main > /tmp/out.s
  spim -file /tmp/out.s 2> /tmp/err | tail -n +2 > /tmp/actual
  if [[ -s /tmp/err ]]; then
    cat /tmp/err 1>&2
    echo "Failed: Invalid MIPS program." && exit 1
  fi
  echo -n "$expected" > /tmp/expected
  diff /tmp/actual /tmp/expected || (echo "Failed: Unexpected output." && exit 1)
}

function test_single() {
  input="package main
func main() $3 {
  return $1
}"
  test "$input" "$2"
}

function test_main() {
  input="package main
  func main() $3 {
    $1
  }"
  test "$input" "$2"
}

bazel build src/Main

test_single "true" "true" bool
test_single "false" "false" bool
test_single '"A"' "A" string
test_single '"Hello, World!\n"' "Hello, World!
" string
test_single 1 1 int
test_single 2147483647 2147483647 int
test_single -2147483648 -2147483648 int

test_single "1<1" false bool
test_single "1<2" true bool
test_single "1+1" 2 int
test_single "1+1+2" 4 int
test_single "1+3  + 2" 6 int

test_single "2*3" 6 int
test_single "1+2*3" 7 int
test_single "2*3+4" 10 int
test_single "(1+2)*3" 9 int
test_single "(-1+ + - +2)*3" -9 int
test_single "-1*-1" 1 int
test_single "-2*-(1+2)" 6 int
test_main "v:=1
return v" 1 int
test_main "v:=2
u:=3
return u" 3 int
test_main "v:=2
u:=3
return v" 2 int
test_main "v:=2
u:=3
return u*v" 6 int
test_main "v:=0
if 1 < 2 {
 v:=v+1
}
return v" 1 int

test "package main
func f(i int) int {
 return i
}
func main() int {
return f(42)
}" 42

test "package main
func g(i int) int {
return i+1
}
func f(i int) int {
return i+g(i*3)
}
func main() int {
return f(2)
}" 9

test "package main
func g(i int) int {
return i*2
}
func f(i int) int {
return g(g(g(i)))
}
func main() int {
return f(3)
}" 24

test "package main
func f(i bool) bool {
return true
}
func main() bool {
return f(false)
}" true

test "package main
func f(i int) bool {
return i < 42
}
func main() bool {
return f(42)
}" false

test "package main
func f(n int) int {
  res := n
  if n < 10 {
    return res * f(n + 1)
  }
  return res
}
func main() int {
  return f(1)
}" 3628800

echo "OK"
