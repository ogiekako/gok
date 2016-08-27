import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        String input = new Scanner(System.in).useDelimiter("\\A").next();
        new Main().compile(input);
    }

    void compile(String input) {
        Ast ast = ast(input);
        gen(ast);
    }

    private Ast ast(String input) {
        if (input.charAt(0) == '"') {
            return Ast.valueString(input.substring(1, input.length() - 1));
        } else if (input.contains("+")) {
            String[] ss = input.split("\\+");
            int a = Integer.valueOf(ss[0]);
            int b = Integer.valueOf(ss[1]);
            return Ast.opAddInt(Ast.valueInt(a), Ast.valueInt(b));
        } else {
            return Ast.valueInt(Integer.valueOf(input));
        }
    }

    private void gen(Ast a) {
        output(".data");
        genData(a);
        output(".text",
                "main:");
        genProg(a);
        int print_int = 1, print_string = 4, exit = 10;
        if (a.t == Type.String) {
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
        if (a.kind == Kind.ValueString) {
            output(a.id + ": .asciiz\"" + a.value + "\"");
        }
        if (a.fst != null) genData(a.fst);
        if (a.snd != null) genData(a.snd);
    }

    private void genProg(Ast a) {
        switch (a.kind) {
            case ValueInt:
                output("li $a0, " + a.value);
                return;
            case ValueString:
                output("la $a0, " + a.id);
                break;
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
                break;
        }
    }

    void output(String... ss) {
        for (String s : ss) {
            System.out.println(s);
        }
    }
}
