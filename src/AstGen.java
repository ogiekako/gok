import java.util.ArrayList;
import java.util.List;

public class AstGen {

    Token[] ts;
    int p;
    public Ast gen(List<Token> tokens) {
        List<Token> normalized = new ArrayList<Token>();
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
        return E();
    }

    /*
    E -> str | T X
    X -> + E  | ε
    T -> (E) | I Y
    Y -> * T | ε
    I -> - I | + I | int
     */
    private Ast E() {
        if (ts[p].c == Cls.Str) {
            String s = ts[p++].s;
            return Ast.valStr(s.substring(1, s.length() - 1));
        }
        Ast fst = T();
        return X(fst);
    }

    private Ast X(Ast fst) {
        if (ts[p].s.equals("+")) {
            p++;
            return Ast.opAddInt(fst, E());
        } else {
            return fst;
        }
    }

    private Ast T() {
        if (ts[p].c == Cls.LeftParen) {
            int left=p;
            p++;
            Ast res = E();
            if (ts[p++].c != Cls.RightParen) {
                throw new IllegalArgumentException("AstGen: unmatched: " + ts[left]);
            }
            return res;
        } else {
            Ast fst = I();
            return Y(fst);
        }
    }

    private Ast I() {
        if (ts[p].c == Cls.Op) {
            if (ts[p].s.equals("+")) {
                p++;
                // Do nothing
                return I();
            } else if (ts[p].s.equals("-")){
                p++;
                if (ts[p].c == Cls.Int) {
                    return Ast.valInt(Integer.valueOf("-" + ts[p++].s));
                } else {
                    return Ast.unMinusInt(I());
                }
            }
        } else if (ts[p].c == Cls.Int) {
            return Ast.valInt(Integer.valueOf(ts[p++].s));
        }
        throw new IllegalArgumentException(String.format("AstGen: %s is unexpected for I.", ts[p]));
    }

    private Ast Y(Ast fst) {
        if (ts[p].s.equals("*")) {
            p++;
            return Ast.opMulInt(fst, T());
        } else {
            return fst;
        }
    }
}
