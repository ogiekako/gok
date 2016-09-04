java_binary(
    name="Main",
    srcs=["Main.java"],
    main_class="Main",
    deps=["All"],
    visibility=["//:__pkg__"],
)

java_library(
    name="All",
    srcs = glob(["**/*.java"]),
    visibility = ["//javatests:__pkg__"],
)
