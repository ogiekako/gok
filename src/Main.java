import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        String input = new Scanner(System.in).useDelimiter("\\A").next();
        compile(input);
    }

    private static void compile(String input) {
        input = input.replaceAll("\\n", "\\\\n");
        output(".data",
                "out_string: .asciiz \"" + input + "\"",
                ".text",
                "main:",
                "li $v0, 4",
                "la $a0, out_string",
                "syscall",
                "li $v0, 10",
                "syscall");
    }

    private static void output(String... ss) {
        for (String s : ss) {
            System.out.println(s);
        }
    }
}
