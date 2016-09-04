import java.util.ArrayList;
import java.util.List;

public class Parser {

    Token[] ts;
    int p;
    public Ast.Pkg parse(List<Token> tokens) {
        List<Token> normalized = new ArrayList<>();
        for(Token t : tokens) {
            if (t.c == Cls.WhiteSpace) continue;
            normalized.add(t);
        }
        Token last = new Token();
        last.c = Cls.EOF;
        last.s = "";
        normalized.add(last);
        ts = normalized.toArray(new Token[0]);
        p = 0;
        Ast.Pkg res = Pkg();
        if (ts[p].c != Cls.EOF) {
            throw new IllegalArgumentException(String.format("%d-th token is remaining.\nTokens:\n%s\nAst:\n%s", p, normalized, res));
        }
        return res;
    }

    private Token checkRead(Cls expectedCls) {
        if (ts[p].c == expectedCls) {
            return ts[p++];
        }
        throw new IllegalArgumentException(String.format(
                "Expected %d-th token to be %s, but was %s.", p, expectedCls, ts[p]));
    }

    private Token checkRead(Cls expectedCls, String s) {
        if (ts[p].c == expectedCls && ts[p].s.equals(s)) {
            return ts[p++];
        }
        throw new IllegalArgumentException(String.format(
                "Expected %d-th token to be (%s, %s), but was %s.", p, expectedCls, s, ts[p]));
    }

    /*
    Pkg       -> package id; Prog
    Prog      -> Body | func id Signature { Body? }; Prog
    Signature -> ( id "int" ) "int"
    Body      -> E | id := E; Body | if E { Body } Body | return E
    E -> str | Plus < E | Plus
    Plus -> Mul | Mul + Plus
    Mul -> U | U * Mul
    U -> + U | - U | (E) | int | bool | id | id(E)
     */
    Ast.Pkg Pkg() {
        checkRead(Cls.Keyword, "package");
        String packageName = checkRead(Cls.Id).s;
        return new Ast.Pkg(packageName, Prog());
    }

    private Ast Prog() {
        if (ts[p].c == Cls.EOF) return null;
        if (ts[p].c == Cls.Keyword && ts[p].s.equals("func")) {
            p++;
            String funcName = checkRead(Cls.Id).s;
            checkRead(Cls.LParen);
            List<Param> params = new ArrayList<>();
            while (ts[p].c != Cls.RParen) {
                String param = checkRead(Cls.Id).s;
                Token tk = checkRead(Cls.Keyword);
                params.add(new Param(param, Type.of(tk.s)));
            }
            checkRead(Cls.RParen);
            Type t = Type.Void;
            if (ts[p].c == Cls.Keyword) {
                Token tk = checkRead(Cls.Keyword);
                t = Type.of(tk.s);
            }
            checkRead(Cls.LBrace);
            Ast fst = Body();
            checkRead(Cls.RBrace);
            Ast snd = Prog();
            return Ast.funcDecl(funcName, params, t, fst, snd);
        } else {
            return Body();
        }
    }

    private Ast Body() {
        if (ts[p].c == Cls.RBrace) return null;
        if (p+1<ts.length && ts[p+1].c == Cls.Assign) {
            if (ts[p].c != Cls.Id) {
                throw new IllegalArgumentException("Left side of := was not id, but " + ts[p]);
            }
            p += 2;
            return Ast.assignStmt(ts[p-2].s, E(), Body());
        } else if (ts[p].c == Cls.Keyword && ts[p].s.equals("if")) {
            p++;
            Ast cond = E();
            checkRead(Cls.LBrace);
            Ast fst = Body();
            checkRead(Cls.RBrace);
            return Ast.ifStmt(cond, fst, Body());
        } else if (ts[p].c == Cls.Keyword && ts[p].s.equals("return")) {
            p++;
            return Ast.retStmt(E());
        }
        return E();
    }

    private Ast E() {
        if (ts[p].c == Cls.Str) {
            String s = ts[p++].s;
            return Ast.valStr(s.substring(1, s.length() - 1));
        }
        Ast fst = Plus();
        if (ts[p].c == Cls.Op){
            if (ts[p].s.equals("<")) {
                p++;
                return Ast.opLT(fst, E());
            }
        }
        return fst;
    }

    private Ast Plus() {
        Ast fst = Mul();
        if (ts[p].c == Cls.Op){
            if (ts[p].s.equals("+")) {
                p++;
                return Ast.opAddInt(fst, Plus());
            }
        }
        return fst;
    }

    private Ast Mul() {
        Ast fst = U();
        if (ts[p].c == Cls.Op) {
            if (ts[p].s.equals("*")) {
                p++;
                return Ast.opMulInt(fst, Mul());
            }
        }
        return fst;
    }

    private Ast U() {
        if (ts[p].c == Cls.Op) {
            if (ts[p].s.equals("+")) {
                // Do nothing.
                p++;
                return U();
            } else if (ts[p].s.equals("-")){
                p++;
                if (ts[p].c == Cls.Int) {
                    return Ast.valInt(Integer.valueOf("-" + ts[p++].s));
                } else {
                    return Ast.unMinusInt(U());
                }
            }
        } else if (ts[p].c == Cls.Bool) {
            return Ast.valBool(Boolean.valueOf(ts[p++].s));
        } else if (ts[p].c == Cls.Int) {
            return Ast.valInt(Integer.valueOf(ts[p++].s));
        } else if (ts[p].c == Cls.LParen) {
            p++;
            Ast res = E();
            checkRead(Cls.RParen);
            return res;
        } else if (ts[p].c == Cls.Id) {
            String id = ts[p++].s;
            if (ts[p].c == Cls.LParen) {
                p++;
                Ast fst = E();
                checkRead(Cls.RParen);
                return Ast.funcCall(id, fst);
            } else {
                return Ast.valId(id);
            }
        }
        throw new IllegalArgumentException(String.format("AstGen: %s is unexpected for I\n%s.", ts[p], genStatus()));
    }

    private String genStatus() {
        StringBuilder b = new StringBuilder();
        for(int i=0;i<p;i++){
            b.append(ts[i].s + " ");
        }
        b.append(">>>" + ts[p].s + "<<<");
        for(int i=p+1;i<ts.length;i++){
            b.append(" " + ts[i].s);
        }
        return b.toString();
    }
}
