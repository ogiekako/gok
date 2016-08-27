import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        String input = new Scanner(System.in).useDelimiter("\\A").next();
        compile(input);
    }

    private static void compile(String input) {
        if (input.charAt(0) == '"') {
            input = input.substring(1,input.length()-1).replaceAll("\\n", "\\\\n");
            output(".data",
                    "out_string: .asciiz \"" + input + "\"",
                    ".text",
                    "main:",
                    "li $v0, 4",            // load immediate
                    "la $a0, out_string",   // load address of string
                    "syscall",
                    "li $v0, 10",
                    "syscall");
        } else if (input.contains("+")) {
            String[] ss = input.split("\\+");
            int a = Integer.parseInt(ss[0]);
            int b = Integer.parseInt(ss[1]);

            output(".data",
                    "out_string: .asciiz \"" + input + "\"",
                    ".text",
                    "main:",
                    "li $v0, 1",            // syscall 1: print_int
                    "li $a0, " + a,
                    "li $a1, " + b,
                    "add $a0, $a0, $a1",
                    "syscall",
                    "li $v0, 10",
                    "syscall");
        } else {
            int n = Integer.parseInt(input);
            output(".data",
                    "out_string: .asciiz \"" + input + "\"",
                    ".text",
                    "main:",
                    "li $v0, 1",            // syscall 1: print_int
                    "li $a0, " + n,         // load immediate
                    "syscall",
                    "li $v0, 10",
                    "syscall");
        }
    }

    private static void output(String... ss) {
        for (String s : ss) {
            System.out.println(s);
        }
    }
}
