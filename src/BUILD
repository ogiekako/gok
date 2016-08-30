java_binary(
    name="Main",
    srcs=["Main.java"],
    main_class="Main",
    deps=["All"],
    visibility=["//:__pkg__"],
)

java_library(
    name="All",
    srcs=["Debug.java", "Token.java", "Parser.java", "Ast.java", "Lexer.java", "Param.java", "Emit.java", "Err.java"],
)

java_library(
    name="test",
    srcs=glob(["*.java"]),
    deps=["@junit//jar"],
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
