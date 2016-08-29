import java.util.HashMap;

public class Emit {


    public void emit(Ast a) {
        output(".data");
        genData(a);
        output(".text",
                "main:");
        genFunc(a);
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
            output(a.id + ": .asciiz \"" + a.value + "\"");
        }
        if (a.fst != null) genData(a.fst);
        if (a.snd != null) genData(a.snd);
    }

    int sp;
    HashMap<String, Integer> offset = new HashMap<>();
    private void genFunc(Ast a) {
        sp = 0;
        offset.clear();
        genFuncPre(a);

        output("add $fp, $zero, $sp");
        output("add $sp, $sp, " + sp);
        genProg(a);
        output("add $sp, $sp, " + (-sp));
    }

    private void genFuncPre(Ast a) {
        if (a.kind == Kind.AssignStmt) {
            if (!offset.containsKey(a.value.toString())) {
                offset.put(a.value.toString(), sp);
                sp -= 4;
            }
        }
        if (a.snd != null) genFuncPre(a.snd);
    }

    private void genProg(Ast a) {
        switch (a.kind) {
            case ValInt:
                output("li $a0, " + a.value);
                return;
            case ValStr:
                output("la $a0, " + a.id);
                return;
            case ValId:
                if (!offset.containsKey(a.value.toString())) {
                    throw new IllegalArgumentException(a.value + " is not defined.");
                }
                int k = this.offset.get(a.value.toString());
                output("lw $a0, " + k + "($fp)");
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
            case AssignStmt:
                genProg(a.fst);
                int o = offset.get(a.value.toString());
                output("sw $a0, " + o + "($fp)");
                genProg(a.snd);
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
