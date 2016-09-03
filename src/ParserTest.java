import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class ParserTest {

    @Test
    public void parse() throws Exception {
        testParse("(Bool, true)", "true");
        testParse("(Int, 1); (Op, <); (Int, 1)", "(1 < 1)");
        testParse("(LParen, (); (Int, 1); (Op, +); (Int, 2); (RParen, )); (Op, *); (Int, 3)",
                "((1 + 2) * 3)");
        testParse("(Id, v); (Assign, :=); (Int, 1); (Id, v)",
                Ast.assignStmt("v", Ast.valInt(1), Ast.valId("v")).toString());
        testParse("(Keyword, func); (WhiteSpace,  ); (Id, f); (LParen, (); (RParen, )); (WhiteSpace,  ); (Keyword, int); (LBrace, {); (RBrace, })",
                "func f() int {\n}\n");
        testParse("(Keyword, func); (WhiteSpace,  ); (Id, f); (LParen, (); (RParen, )); (WhiteSpace,  ); (Keyword, bool); (LBrace, {); (RBrace, })",
                "func f() bool {\n}\n");
        testParse("(Keyword, func); (WhiteSpace,  ); (Id, f); (LParen, (); (RParen, )); (WhiteSpace,  ); (LBrace, {); (RBrace, })",
                "func f() {\n}\n");
    }

    @Test
    public void lexParse() throws Exception {
        textLexParse("true", "true");
        textLexParse("1 + 2 * 3 < 3 * 2 + 1", "((1 + (2 * 3)) < ((3 * 2) + 1))");
        textLexParse("if 1 < 1 {\n1\n}\n2", "if (1 < 1) {\n1\n}\n2");
    }

    private void textLexParse(String input, String wantAstStr) {
        List<Token> tokens = new Lexer().lex(input);
        testParse(Token.str(tokens), wantAstStr);
    }

    private void testParse(String tokensStr, String wantAstStr) {
        List<Token> tokens = Token.fromStr(tokensStr);
        Ast actual = new Parser().parse(tokens);
        String actualStr = actual.toString();
        Assert.assertEquals(wantAstStr, actualStr);
    }
}
