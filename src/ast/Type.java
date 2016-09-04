package ast;

import utils.Err;

public enum Type {
    Bool(" bool"),
    Int(" int"),
    Str(" string"),
    Void(""),
    Unknown(" ???"),;
    String name;

    Type(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    public static Type of(String s) {
        switch (s) {
            case "bool": return Bool;
            case "int": return Int;
            case "string": return Str;
            default: throw Err.format("Unknown type name %s", s);
        }
    }
}
