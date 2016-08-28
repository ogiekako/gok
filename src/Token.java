import java.util.ArrayList;
import java.util.List;

public class Token {
    Cls c;
    String s; // lexeme. Includes '"' for Str.

    static char[] cs;
    static int p;
    static List<Token> res;

    public static List<Token> tokenize(String input) {
        cs = new char[input.length() + 1];
        for(int i=0;i<input.length();i++)cs[i] = input.charAt(i);
        cs[input.length()] = '$';
        p = 0;
        res = new ArrayList<Token>();
        while (p < cs.length - 1) {
            res.add(next());
        }
        return res;
    }

    private static Token next() {
        if (cs[p] == '(') {
            return tok(Cls.LParen, p, p + 1);
        } else if (cs[p] == ')') {
            return tok(Cls.RParen, p, p + 1);
        } else if (cs[p] == '"') {
            int from = p++;
            while (cs[p++] != '"') ;
            return tok(Cls.Str, from, p);
        } else if (in(cs[p], '+', '-', '*')) {
            return tok(Cls.Op, p, p + 1);
        } else if (isDigit(cs[p])) {
            int from = p;
            while (isDigit(cs[++p])) ;
            return tok(Cls.Int, from, p);
        } else if (isWhiteSpace(cs[p])) {
            int from = p;
            while(isWhiteSpace(cs[++p]));
            return tok(Cls.WhiteSpace, from, p);
        }
        throw new IllegalArgumentException(new String(cs, p, cs.length - p));
    }

    private static boolean in(char c, char... cs) {
        for(char d:cs)if(c==d)return true;
        return false;
    }

    private static boolean isWhiteSpace(char c) {
        return c == ' ' || c == '\t' || c == '\n';
    }

    private static Token tok(Cls c, int from, int to) {
        Token tok = new Token();
        tok.c = c;
        tok.s = new String(cs, from, to - from);
        p = to;
        return tok;
    }

    private static boolean isDigit(char c) {
        return '0' <= c && c <= '9';
    }

    @Override
    public String toString() {
        return String.format("(%s, %s)", c, s);
    }

    // Example: "(Int, 1); (Op, +); (Int, 1)"
    public static List<Token> fromStr(String tokensStr) {
        List<Token> res = new ArrayList<>();
        for (String s : tokensStr.split("; ")) {
            s = s.substring(1, s.length() - 1);
            String cls = s.split(", ")[0];
            Token t = new Token();
            t.c = Cls.valueOf(cls);
            t.s = s.substring(cls.length() + 2);
            res.add(t);
        }
        return res;
    }
}

enum Cls {
    WhiteSpace,
    Op,
    Int,
    Str,
    LParen,
    RParen,
    EOF,
}