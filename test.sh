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

bazel build src/Main

test '"A"' "A"
test '"Hello, World!\n"' "Hello, World!
"
test 1 1
test 2147483647 2147483647
test -2147483648 -2147483648

test "1+1" 2
test "1+1+2" 4
test "1+3  + 2" 6

echo "OK"
