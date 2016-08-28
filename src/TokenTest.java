import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;

public class TokenTest {

    @Test
    public void testTokenize() throws Exception {
        testTokenize2("1+1", "(Int, 1); (Op, +); (Int, 1)");
        testTokenize2("1  + 1*2", "(Int, 1); (WhiteSpace,   ); (Op, +); (WhiteSpace,  ); (Int, 1); (Op, *); (Int, 2)");
        testTokenize2("\"Hello, world!\n\"", "(Str, \"Hello, world!\n\")");
    }

    private void testTokenize2(String input, String tokensStr) {
        String[] ss = tokensStr.replaceAll("\\(|\\)","").split("; ");
        String[][] exp = new String[ss.length][2];
        for (int i = 0; i < ss.length; i++) {
            exp[i][0] = ss[i].split(", ")[0];
            exp[i][1] = ss[i].substring(exp[i][0].length() + 2);
        }

        List<Token> actual = Token.tokenize(input);
        String err = String.format("Want:\n%s\nGot:\n%s", tokensStr, str(actual));
        Assert.assertEquals("Different size.  " + err,
                exp.length, actual.size());
        for (int i = 0; i < exp.length; i++) {
            Assert.assertEquals(String.format("Different %d-th class.  %s", i, err),
                    exp[i][0], actual.get(i).c.toString());
            Assert.assertEquals(String.format("Different %d-th value.  %s", i, err),
                    exp[i][1], actual.get(i).s);
        }
    }

    private String str(List<Token> tokens) {
        return tokens.stream().map(Token::toString).collect(Collectors.joining("; "));
    }
}