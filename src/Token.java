import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Token {
    Cls c;
    String s; // lexeme. Includes '"' for Str.

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

    public static String str(List<Token> tokens) {
        return tokens.stream().map(tk -> "(" + tk.c + ", " + tk.s + ")").collect(Collectors.joining("; "));
    }
}

enum Cls {
    WhiteSpace,
    Op,
    Bool,
    Int,
    Str,
    LParen,
    RParen,
    LBrace,
    RBrace,
    Assign,
    Keyword,
    Id,
    EOF,
}
