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
        gen(ast);
    }

    private void gen(Ast a) {
        output(".data");
        genData(a);
        output(".text",
                "main:");
        genProg(a);
        int print_int = 1, print_string = 4, exit = 10;
        if (a.t == Type.Str) {
            genSyscall(print_string);
        } else if (a.t == Type.Int) {
            genSyscall(print_int);
        }
        genSyscall(exit);
    }

    private void genSyscall(int type) {
        output("li $v0, " + type,
                "syscall");
    }

    private void genData(Ast a) {
        if (a.kind == Kind.ValStr) {
            output(a.id + ": .asciiz\"" + a.value + "\"");
        }
        if (a.fst != null) genData(a.fst);
        if (a.snd != null) genData(a.snd);
    }

    private void genProg(Ast a) {
        switch (a.kind) {
            case ValInt:
                output("li $a0, " + a.value);
                return;
            case ValStr:
                output("la $a0, " + a.id);
                return;
            case OpAddInt:
                genProg(a.fst);
                // Push $a0 to stack.
                output(
                        "sw $a0, 0($sp)",
                        "addi $sp, $sp, -4"
                );
                genProg(a.snd);
                output(
                        "lw $a1, 4($sp)",
                        "addi $sp, $sp, 4",

                        "add $a0, $a0, $a1"
                );
                return;
            case OpMulInt:
                genProg(a.fst);
                // Push $a0 to stack.
                output(
                        "sw $a0, 0($sp)",
                        "addi $sp, $sp, -4"
                );
                genProg(a.snd);
                output(
                        "lw $a1, 4($sp)",
                        "addi $sp, $sp, 4",

                        "mult $a0, $a1", // LO = (($s * $t) << 32) >> 32; mflo
                        "mflo $a0"
                );
                return;
            case UnMinusInt:
                genProg(a.fst);
                output("sub $a0, $zero, $a0");
                return;
            default:
                throw new IllegalArgumentException("Unknown kind: " + a.kind);
        }
    }

    void output(String... ss) {
        for (String s : ss) {
            System.out.println(s);
        }
    }
}
