import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;

public class LexerTest {

    @Test
    public void testTokenize() throws Exception {
        testTokenize2("true", "(Bool, true)");
        testTokenize2("false", "(Bool, false)");
        testTokenize2("1<1", "(Int, 1); (Op, <); (Int, 1)");
        testTokenize2("1+1", "(Int, 1); (Op, +); (Int, 1)");
        testTokenize2("1  + 1*2", "(Int, 1); (WhiteSpace,   ); (Op, +); (WhiteSpace,  ); (Int, 1); (Op, *); (Int, 2)");
        testTokenize2("\"Hello, world!\n\"", "(Str, \"Hello, world!\n\")");
        testTokenize2("(1+2)*3", "(LParen, (); (Int, 1); (Op, +); (Int, 2); (RParen, )); (Op, *); (Int, 3)");
        testTokenize2("v:=1", "(Id, v); (Assign, :=); (Int, 1)");
        testTokenize2("func f(v int) {}",
                "(Keyword, func); (WhiteSpace,  ); (Id, f); (LParen, (); (Id, v); (WhiteSpace,  ); (Keyword, int); (RParen, )); (WhiteSpace,  ); (LBrace, {); (RBrace, })");
        testTokenize2("func f() string {}",
                "(Keyword, func); (WhiteSpace,  ); (Id, f); (LParen, (); (RParen, )); (WhiteSpace,  ); (Keyword, string); (WhiteSpace,  ); (LBrace, {); (RBrace, })");
        testTokenize2("func f() bool {}",
                "(Keyword, func); (WhiteSpace,  ); (Id, f); (LParen, (); (RParen, )); (WhiteSpace,  ); (Keyword, bool); (WhiteSpace,  ); (LBrace, {); (RBrace, })");
    }

    private void testTokenize2(String input, String tokensStr) {
        List<Token> want = Token.fromStr(tokensStr);

        List<Token> actual = new Lexer().lex(input);
        String err = String.format("Want:\n%s\nGot:\n%s", tokensStr, str(actual));
        Assert.assertEquals("Different size.  " + err,
                want.size(), actual.size());
        for (int i = 0; i < want.size(); i++) {
            Assert.assertEquals(String.format("Different %d-th class.  %s", i, err),
                    want.get(i).c, actual.get(i).c);
            Assert.assertEquals(String.format("Different %d-th value.  %s", i, err),
                    want.get(i).s, actual.get(i).s);
        }
    }

    private String str(List<Token> tokens) {
        return tokens.stream().map(Token::toString).collect(Collectors.joining("; "));
    }
}
