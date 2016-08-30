import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        String input = new Scanner(System.in).useDelimiter("\\A").next();
        new Main().compile(input);
    }

    public void compile(String input) {
        List<Token> tokens = new Lexer().tokenize(input);
        Ast ast = new Parser().parse(tokens);
        new Emit().emit(ast);
    }

}
