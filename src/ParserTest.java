import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class ParserTest {

    @Test
    public void testParse() throws Exception {
        testParse2("(Bool, true)", "true");
        testParse2("(LParen, (); (Int, 1); (Op, +); (Int, 2); (RParen, )); (Op, *); (Int, 3)",
                Ast.opMulInt(Ast.opAddInt(Ast.valInt(1), Ast.valInt(2)), Ast.valInt(3)).toString());
        testParse2("(Id, v); (Assign, :=); (Int, 1); (Id, v)",
                Ast.assignStmt("v", Ast.valInt(1), Ast.valId("v")).toString());
        testParse2("(Keyword, func); (WhiteSpace,  ); (Id, f); (LParen, (); (RParen, )); (WhiteSpace,  ); (Keyword, int); (LBrace, {); (RBrace, })",
                "func f() int {\n}\n");
        testParse2("(Keyword, func); (WhiteSpace,  ); (Id, f); (LParen, (); (RParen, )); (WhiteSpace,  ); (Keyword, bool); (LBrace, {); (RBrace, })",
                "func f() bool {\n}\n");
        testParse2("(Keyword, func); (WhiteSpace,  ); (Id, f); (LParen, (); (RParen, )); (WhiteSpace,  ); (LBrace, {); (RBrace, })",
                "func f() {\n}\n");
    }

    private void testParse2(String tokensStr, String wantAstStr) {
        List<Token> tokens = Token.fromStr(tokensStr);
        Ast actual = new Parser().parse(tokens);
        String actualStr = actual.toString();
        Assert.assertEquals(wantAstStr, actualStr);
    }
}
