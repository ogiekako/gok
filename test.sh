#!/bin/bash

set -e

function test() {
  input=$1
  expected=$2

  printf "$input" | bazel-bin/src/Main > /tmp/out.s
  spim -file /tmp/out.s | tail -n +2 > /tmp/actual
  printf "$expected" > /tmp/expected
  diff /tmp/actual /tmp/expected || (echo "Failed." && exit 1)
}

bazel build src/Main

test "A" "A"
test "Hello, World!\n" "Hello, World!\n"

echo "OK"
