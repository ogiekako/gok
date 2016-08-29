import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        String input = new Scanner(System.in).useDelimiter("\\A").next();
        new Main().compile(input);
    }

    public void compile(String input) {
        List<Token> tokens = Token.tokenize(input);
        Ast ast = new AstGen().gen(tokens);
        new Emit().emit(ast);
    }

}
