import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Lexer {
    static Set<String> keywords = new HashSet<>(Arrays.asList(
            "func",
            "bool",
            "int",
            "string"
    ));
    char[] cs;
    int p;
    List<Token> res;

    public List<Token> tokenize(String input) {
        cs = new char[input.length() + 1];
        for (int i = 0; i < input.length(); i++) cs[i] = input.charAt(i);
        cs[input.length()] = '$';
        p = 0;
        res = new ArrayList<>();
        while (p < cs.length - 1) {
            res.add(next());
        }
        return res;
    }

    private Token next() {
        if (cs[p] == '(') {
            return tok(Cls.LParen, p, p + 1);
        } else if (cs[p] == ')') {
            return tok(Cls.RParen, p, p + 1);
        } else if (cs[p] == '{') {
            return tok(Cls.LBrace, p, p + 1);
        } else if (cs[p] == '}') {
            return tok(Cls.RBrace, p, p + 1);
        } if (cs[p] == '"') {
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
            while (isWhiteSpace(cs[++p])) ;
            return tok(Cls.WhiteSpace, from, p);
        } else if (startsWith(":=")) {
            return tok(Cls.Assign, p, p + 2);
        } else if (isAlphabet(cs[p])) {
            int from = p++;
            while (isDigit(cs[p]) || isAlphabet(cs[p])) p++;
            String s = new String(cs, from, p - from);
            if (keywords.contains(s)) {
                return tok(Cls.Keyword, from, p);
            }
            if (s.equals("true") || s.equals("false")) {
                return tok(Cls.Bool, from, p);
            }
            return tok(Cls.Id, from, p);
        }
        throw new IllegalArgumentException(new String(cs, p, cs.length - p));
    }

    private Token tok(Cls c, int from, int to) {
        Token tok = new Token();
        tok.c = c;
        tok.s = new String(cs, from, to - from);
        p = to;
        return tok;
    }

    private boolean startsWith(String s) {
        for (int i = 0; i < s.length(); i++) {
            if (p + i >= cs.length) return false;
            if (cs[p + i] != s.charAt(i)) return false;
        }
        return true;
    }

    private static boolean isAlphabet(char c) {
        return 'a' <= c && c <= 'z' || 'A' <= c && c <= 'Z';
    }

    private static boolean in(char c, char... cs) {
        for (char d : cs) if (c == d) return true;
        return false;
    }

    private static boolean isWhiteSpace(char c) {
        return c == ' ' || c == '\t' || c == '\n';
    }


    private static boolean isDigit(char c) {
        return '0' <= c && c <= '9';
    }
}
