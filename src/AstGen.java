import java.util.ArrayList;
import java.util.List;

public class AstGen {

    Token[] ts;
    int p;
    public Ast gen(List<Token> tokens) {
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
        Ast res = E();
        if (ts[p].c != Cls.EOF) {
            throw new IllegalArgumentException(String.format("%d-th token is remaining.\nTokens:\n%s\nAst:\n%s", p, normalized, res));
        }
        return res;
    }

    /*
    E -> str | T + E | T
    T -> U | U * T
    U -> + U | - U | (E) | int
     */
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
            int left = p;
            p++;
            Ast res = E();
            if (ts[p].c != Cls.RParen)
                throw new IllegalArgumentException("AstGen: unmatched: " + ts[left]);
            p++;
            return res;
        }
        throw new IllegalArgumentException(String.format("AstGen: %s is unexpected for I.", ts[p]));
    }
}
