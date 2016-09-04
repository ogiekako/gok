java_library(
    name="test",
    srcs=glob(["*.java"]),
    deps=["@junit//jar", "//src:All"],
)

java_test(
    name="LexerTest",
    size="small",
    runtime_deps=["test"],
    test_class="LexerTest",
)

java_test(
    name="MainTest",
    size="small",
    runtime_deps=["test"],
    test_class="MainTest",
)

java_test(
    name="ParserTest",
    size="small",
    runtime_deps=["test"],
    test_class="ParserTest",
)
