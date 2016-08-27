public class Ast {
    Type t;
    Kind kind;
    Ast fst;
    Ast snd;
    Object value;
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
    public static Ast valueInt(int n) {
        return new Ast(Type.Int, Kind.ValueInt, null, null, n);
    }
    public static Ast valueString(String s) {
        return new Ast(Type.String, Kind.ValueString, null, null, s);
    }

    private static int global_id = 1;
}

enum Type {
    Int,
    String,
}

enum Kind {
    ValueInt,
    ValueString,
    OpAddInt,
}
