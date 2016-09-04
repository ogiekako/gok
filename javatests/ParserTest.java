import ast.Ast;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class ParserTest {

    @Test
    public void lexParse() throws Exception {
        testLexParseVal("1 < 1", "(1 < 1)");
        testLexParseVal("(1+2)*3", "((1 + 2) * 3)");
        testLexParseVal("true", "true");
        testLexParseVal("1 + 2 * 3 < 3 * 2 + 1", "((1 + (2 * 3)) < ((3 * 2) + 1))");

        testLexParseBody("v:=1\nreturn v", "v := 1\nreturn v\n");
        testLexParseBody("if 1 < 1 {\nreturn 1\n}\nreturn 2", "if (1 < 1) {\nreturn 1\n}\nreturn 2\n");
        testLexParseBody("return 1\n", "return 1\n");

        testLexParseProg("func f() int {}", "func f() int {\n}\n");
        testLexParseProg("func f() bool {}", "func f() bool {\n}\n");
        testLexParseProg("func f() {\n}\n", "func f() {\n}\n");

        testLexParsePkg("package main\nfunc main() {\n}\n", "package main\nfunc main() {\n}\n");
    }

    private void testLexParseVal(String input, String wantAstStr) {
        testLexParseBody("return " + input + "\n", "return " + wantAstStr + "\n");
    }

    private void testLexParseBody(String input, String wantAstStr) {
        testLexParseProg("func main() {" + input + "}", "func main() {\n" + wantAstStr + "}\n");
    }

    private void testLexParseProg(String input, String wantAstStr) {
        testLexParsePkg("package main\n" + input, "package main\n" + wantAstStr);
    }

    private void testLexParsePkg(String input, String wantAstStr) {
        List<Token> tokens = new Lexer().lex(input);
        Ast.Pkg actual = new Parser().parse(tokens);
        String actualStr = actual.toString();
        Assert.assertEquals(wantAstStr, actualStr);
    }
}
