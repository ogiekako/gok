import java.util.ArrayList;
import java.util.List;

public class Parser {

    Token[] ts;
    int p;
    public Ast parse(List<Token> tokens) {
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
        Ast res = Prog();
        if (ts[p].c != Cls.EOF) {
            throw new IllegalArgumentException(String.format("%d-th token is remaining.\nTokens:\n%s\nAst:\n%s", p, normalized, res));
        }
        return res;
    }

    /*
    Prog      -> Body | func id Signature { Body? }; Prog
    Signature -> ( id "int" ) "int"
    Body      -> E | id := E; Body
    E -> str | T + E | T
    T -> U | U * T
    U -> + U | - U | (E) | int | id | id(E)
     */

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
                Err.checkIn(tk.s, "int", "string");
                params.add(new Param(param, tk.s.equals("int") ? Type.Int : Type.Str));
            }
            checkRead(Cls.RParen);
            Type t = Type.Void;
            if (ts[p].c == Cls.Keyword) {
                Token tk = checkRead(Cls.Keyword);
                Err.checkIn(tk.s, "int", "string");
                t = tk.s.equals("int") ? Type.Int : Type.Str;
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
        }
        return E();
    }

    private Ast E() {
        if (ts[p].c == Cls.Str) {
            String s = ts[p++].s;
            return Ast.valStr(s.substring(1, s.length() - 1));
        }
        Ast fst = T();
        if (ts[p].c == Cls.Op){
            if (ts[p].s.equals("+")) {
                p++;
                return Ast.opAddInt(fst, E());
            }
        }
        return fst;
    }

    private Ast T() {
        Ast fst = U();
        if (ts[p].c == Cls.Op) {
            if (ts[p].s.equals("*")) {
                p++;
                return Ast.opMulInt(fst, T());
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
        throw new IllegalArgumentException(String.format("AstGen: %s is unexpected for I.", ts[p]));
    }
}
