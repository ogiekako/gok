public class Ast {
    Type t;
    Kind kind;
    Ast fst;
    Ast snd;
    Object value; // Excludes '"' for Str.
    String id;

    private Ast(Type t, Kind kind, Ast fst, Ast snd, Object value) {
        this.t=t;
        this.kind=kind;
        this.fst=fst;
        this.snd=snd;
        this.value = value;
        this.id = kind.toString() + "_" + (global_id++);
    }

    public static Ast opAddInt(Ast fst, Ast snd) {
        return new Ast(Type.Int, Kind.OpAddInt, fst, snd, null);
    }
    public static Ast opMulInt(Ast fst, Ast snd) {
        return new Ast(Type.Int, Kind.OpMulInt, fst, snd, null);
    }
    public static Ast valInt(int n) {
        return new Ast(Type.Int, Kind.ValInt, null, null, n);
    }
    public static Ast valStr(String s) {
        return new Ast(Type.Str, Kind.ValStr, null, null, s);
    }
    public static Ast unMinusInt(Ast fst) {
        return new Ast(Type.Int, Kind.UnMinusInt, fst, null, null);
    }

    private static int global_id = 1;

    @Override
    public String toString() {
        switch (kind) {
            case ValInt:
            case ValStr:
                return value.toString();
            case OpAddInt:
                return String.format("(%s + %s)", fst, snd);
            case OpMulInt:
                return String.format("(%s * %s)", fst, snd);
            case UnMinusInt:
                return String.format("-(%s)", fst);
        }
        throw new IllegalArgumentException("Unexpected kind: " + kind);
    }
}

enum Type {
    Int,
    Str,
}

enum Kind {
    ValInt,
    ValStr,
    OpAddInt,
    OpMulInt,
    UnMinusInt,
}
