java_binary(
    name="Main",
    srcs=["Main.java"],
    main_class="Main",
    deps=["Debug","Ast", "AstGen", "Token"],
)
java_library(
    name="Debug",
    srcs=["Debug.java"],
)
java_library(
    name="Ast",
    srcs=["Ast.java"],
)
java_library(
    name="Token",
    srcs=["Token.java"],
)

java_library(
    name="AstGen",
    srcs=["AstGen.java"],
    deps = ["Ast", "Token"],
)
