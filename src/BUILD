java_binary(
    name="Main",
    srcs=["Main.java"],
    main_class="Main",
    deps=["All"],
    visibility=["//:__pkg__"],
)

java_library(
    name="All",
    srcs=["Debug.java", "Token.java", "AstGen.java", "Ast.java", "Emit.java"],
)

java_library(
    name="test",
    srcs=glob(["*.java"]),
    deps=["@junit//jar"],
)

java_test(
    name="TokenTest",
    size="small",
    runtime_deps=["test"],
    test_class="TokenTest",
)

java_test(
    name="MainTest",
    size="small",
    runtime_deps=["test"],
    test_class="MainTest",
)

java_test(
    name="AstGenTest",
    size="small",
    runtime_deps=["test"],
    test_class="AstGenTest",
)
