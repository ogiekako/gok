import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class ParserTest {

    @Test
    public void parse() throws Exception {
        testParseOld("(Bool, true)", "true");
        testParseOld("(Int, 1); (Op, <); (Int, 1)", "(1 < 1)");
        testParseOld("(LParen, (); (Int, 1); (Op, +); (Int, 2); (RParen, )); (Op, *); (Int, 3)",
                "((1 + 2) * 3)");
        testParseOld("(Id, v); (Assign, :=); (Int, 1); (Id, v)",
                Ast.assignStmt("v", Ast.valInt(1), Ast.valId("v")).toString());
        testParseOld("(Keyword, func); (WhiteSpace,  ); (Id, f); (LParen, (); (RParen, )); (WhiteSpace,  ); (Keyword, int); (LBrace, {); (RBrace, })",
                "func f() int {\n}\n");
        testParseOld("(Keyword, func); (WhiteSpace,  ); (Id, f); (LParen, (); (RParen, )); (WhiteSpace,  ); (Keyword, bool); (LBrace, {); (RBrace, })",
                "func f() bool {\n}\n");
        testParseOld("(Keyword, func); (WhiteSpace,  ); (Id, f); (LParen, (); (RParen, )); (WhiteSpace,  ); (LBrace, {); (RBrace, })",
                "func f() {\n}\n");
    }

    @Test
    public void lexParse() throws Exception {
        textLexParseOld("true", "true");
        textLexParseOld("1 + 2 * 3 < 3 * 2 + 1", "((1 + (2 * 3)) < ((3 * 2) + 1))");
        textLexParseOld("if 1 < 1 {\n1\n}\n2", "if (1 < 1) {\n1\n}\n2");
        textLexParseOld("return 1\n", "return 1\n");
    }

    private void textLexParseOld(String input, String wantAstStr) {
        List<Token> tokens = new Lexer().lex(input);
        testParseOld(Token.str(tokens), wantAstStr);
    }

    private void testParseOld(String tokensStr, String wantAstStr) {
        tokensStr = "(Keyword, package); (Id, main); " + tokensStr;
        wantAstStr = "package main\n" + wantAstStr;
        List<Token> tokens = Token.fromStr(tokensStr);
        Ast.Pkg actual = new Parser().parse(tokens);
        String actualStr = actual.toString();
        Assert.assertEquals(wantAstStr, actualStr);
    }
}
