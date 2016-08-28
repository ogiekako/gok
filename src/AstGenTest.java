import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class AstGenTest {

    @Test
    public void testGen() throws Exception {
        testGen2("(LParen, (); (Int, 1); (Op, +); (Int, 2); (RParen, )); (Op, *); (Int, 3)",
                Ast.opMulInt(Ast.opAddInt(Ast.valInt(1), Ast.valInt(2)), Ast.valInt(3)));
    }

    private void testGen2(String tokensStr, Ast want) {
        List<Token> tokens = Token.fromStr(tokensStr);
        Ast actual = new AstGen().gen(tokens);
        String wantStr = want.toString();
        String actualStr = actual.toString();
        Assert.assertEquals(wantStr, actualStr);
    }
}
