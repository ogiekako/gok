import java.util.List;
import java.util.stream.Collectors;

public class Ast {
    Type t;
    Kind kind;
    Ast fst;
    Ast snd;
    Object value; // Excludes '"' for Str.
    List<Param> params; // for funcDecl.
    Ast cond; // for ifStmt.
    String id;

    private Ast(Type t, Kind kind, Ast fst, Ast snd, Object value) {
        this.t=t;
        this.kind=kind;
        this.fst=fst;
        this.snd=snd;
        this.value = value;
        this.id = kind.toString() + "_" + (global_id++);
    }

    public static Ast opLT(Ast fst, Ast snd) {return new Ast(Type.Bool, Kind.OpLTInt, fst, snd, null);}
    public static Ast opAddInt(Ast fst, Ast snd) {
        return new Ast(Type.Int, Kind.OpAddInt, fst, snd, null);
    }
    public static Ast opMulInt(Ast fst, Ast snd) {
        return new Ast(Type.Int, Kind.OpMulInt, fst, snd, null);
    }
    public static Ast valBool(boolean b) {
        return new Ast(Type.Bool, Kind.ValBool, null, null, b);
    }
    public static Ast valInt(int n) {
        return new Ast(Type.Int, Kind.ValInt, null, null, n);
    }
    public static Ast valStr(String s) {
        return new Ast(Type.Str, Kind.ValStr, null, null, s);
    }
    public static Ast unMinusInt(Ast fst) {
        return new Ast(Type.Int, Kind.UnMinusInt, fst, null, null);
        // irrelevant
    }
    public static Ast assignStmt(String id, Ast fst, Ast snd) {
        return new Ast(fst.t, Kind.AssignStmt, fst, snd, id);
    }
    public static Ast ifStmt(Ast cond, Ast fst, Ast snd) {
        Ast res = new Ast(fst.t, Kind.IfStmt, fst, snd, null);
        res.cond = cond;
        return res;
    }
    public static Ast retStmt(Ast fst) {
        return new Ast(fst.t, Kind.RetStmt, fst, null, null);
    }
    public static Ast valId(String s) {
        return new Ast(Type.Unknown, Kind.ValId, null, null, s);
    }
    public static Ast funcDecl(String f, List<Param> params, Type t, Ast fst, Ast snd) {
        Ast res = new Ast(t, Kind.FuncDecl, fst, snd, f);
        res.params = params;
        return res;
    }

    private static int global_id = 1;

    @Override
    public String toString() {
        switch (kind) {
            case ValBool:
            case ValInt:
            case ValStr:
            case ValId:
                return value.toString();
            case OpLTInt:
                return String.format("(%s < %s)", fst, snd);
            case OpAddInt:
                return String.format("(%s + %s)", fst, snd);
            case OpMulInt:
                return String.format("(%s * %s)", fst, snd);
            case UnMinusInt:
                return String.format("-(%s)", fst);
            case AssignStmt:
                return String.format("%s := %s\n%s", value, fst, snd);
            case IfStmt:
                return String.format("if %s {\n%s\n}\n%s", cond, fst, snd);
            case RetStmt:
                return String.format("return %s\n", fst);
            case FuncDecl:
                return String.format("func %s(%s)%s {\n%s}\n%s", value, str(params), t, str(fst), str(snd));
        }
        throw new IllegalArgumentException("Unexpected kind: " + kind);
    }

    private String str(Ast a) {
        return a == null ? "" : a.toString();
    }

    private String str(List<Param> params) {
        return params.stream().map(p -> p.id + " " + p.t).collect(Collectors.joining(", "));
    }

    public static Ast funcCall(String id, Ast fst) {
        return new Ast(Type.Unknown, Kind.FuncCall, fst, null, id);
    }

    static class Pkg {
        String name;
        Ast prog;

        Pkg(String name, Ast prog) {
            this.name = name;
            this.prog = prog;
        }

        @Override
        public String toString() {
            return "package " + name + "\n" + prog;
        }
    }
}

enum Type {
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

    static Type of(String s) {
        switch (s) {
            case "bool": return Bool;
            case "int": return Int;
            case "string": return Str;
            default: throw Err.format("Unknown type name %s", s);
        }
    }
}

enum Kind {
    ValBool,
    ValInt,
    ValStr,
    ValId,
    OpLTInt,
    OpAddInt,
    OpMulInt,
    UnMinusInt,
    AssignStmt,
    RetStmt,
    IfStmt,
    FuncDecl,
    FuncCall,
}
