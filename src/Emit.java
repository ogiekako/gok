import java.util.HashMap;
import java.util.List;

public class Emit {

    public void emit(Ast a) {
        output(".data");
        output("true: .asciiz \"true\"");
        output("false: .asciiz \"false\"");
        genData(a);
        output(".text");
        genFunc(a);
        int print_int = 1, print_string = 4, exit = 10;
        switch (a.t) {
            case Void:
                break;
            case Bool:
                output(
                        "bgt $a0 $zero set_true",
                        "la $a0 false",
                        "b set_done",
                        "set_true:",
                        "la $a0 true",
                        "set_done:"
                );
                genSyscall(print_string);
                break;
            case Int:
                genSyscall(print_int);
                break;
            case Str:
                genSyscall(print_string);
                break;
            default:
                throw  Err.format("Unknown type: %s", a.t);
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
    HashMap<String, Integer> paramOffset = new HashMap<>();
    HashMap<String, Integer> tempOffset = new HashMap<>();
    private void genFunc(Ast a) {
        if (a.kind != Kind.FuncDecl) {
            throw new IllegalArgumentException(String.format("Expected %s, but was %s. Ast:\n%s", Kind.FuncDecl, a.kind, a));
        }
        if (a.value.equals("main")) {
            output("main:");
        } else {
            output(a.value + ":");
        }

        genParamOffset(a.params);

        output("add $fp, $zero, $sp");

        output("sw $ra, 0($sp)");
        sp = -4;
        tempOffset.clear();
        genFuncPre(a.fst);

        output("add $sp, $sp, " + sp);

        genBody(a.fst);
        output("add $sp, $sp, " + (-sp));
        output("lw $ra, 0($sp)");

        if (!a.value.equals("main")) {
            output("jr $ra");
        }

        if (a.snd != null) {
            genFunc(a.snd);
        }
    }

    // (a, b int)  a: $fp + 12,  b: $fp + 8   (old fp: $fp + 4)
    private void genParamOffset(List<Param> params) {
        int t = 8;
        for(int i=params.size() - 1;i>=0; i--) {
            paramOffset.put(params.get(i).id, t);
            t += 4;
        }
    }

    private void genFuncPre(Ast a) {
        if (a == null) return;
        if (a.kind == Kind.AssignStmt) {
            if (paramOffset.containsKey(a.value.toString())) {
                throw Err.format("Redeclaration of %s.", a.value);
            }
            if (!tempOffset.containsKey(a.value.toString())) {
                tempOffset.put(a.value.toString(), sp);
                sp -= 4;
            }
        }
        if (a.snd != null) genFuncPre(a.snd);
    }

    private void genBody(Ast a) {
        if (a == null) return;
        switch (a.kind) {
            case ValBool:
                output("li $a0, " + ((Boolean)(a.value) ? 1 : 0));
                return;
            case ValInt:
                output("li $a0, " + a.value);
                return;
            case ValStr:
                output("la $a0, " + a.id);
                return;
            case ValId:
                if (!tempOffset.containsKey(a.value.toString()) && !paramOffset.containsKey(a.value.toString())) {
                    throw new IllegalArgumentException(a.value + " is not defined.");
                }
                if (tempOffset.containsKey(a.value.toString())) {
                    int k = this.tempOffset.get(a.value.toString());
                    output("lw $a0, " + k + "($fp)");
                } else {
                    int k = this.paramOffset.get(a.value.toString());
                    output("lw $a0, " + k + "($fp)");
                }
                return;
            case OpLTInt:
            case OpAddInt:
            case OpMulInt:
                genBody(a.fst);
                // Push $a0 to stack.
                output(
                        "sw $a0, 0($sp)",
                        "addi $sp, $sp, -4"
                );
                genBody(a.snd);
                output(
                        "lw $a1, 4($sp)",
                        "addi $sp, $sp, 4"
                );
                switch (a.kind) {
                    case OpAddInt:
                        output(
                                "add $a0, $a0, $a1"
                        );
                        break;
                    case OpMulInt:
                        output(
                                "mult $a0, $a1", // LO = (($s * $t) << 32) >> 32; mflo
                                "mflo $a0"
                        );
                        break;
                    case OpLTInt:  // $a1 < $a0
                        output(
                                "slt $a0, $a1, $a0"
                        );
                        break;
                    default:
                        throw Err.format("Unknown kind " + a.kind);
                }
                return;
            case UnMinusInt:
                genBody(a.fst);
                output("sub $a0, $zero, $a0");
                return;
            case AssignStmt:
                genBody(a.fst);
                int o = tempOffset.get(a.value.toString());
                output("sw $a0, " + o + "($fp)");
                genBody(a.snd);
                return;
            case IfStmt:
                genBody(a.cond);
                String elseId = a.id + "_else";
                output("beq $a0, $zero, " + elseId);
                genBody(a.fst);
                output(elseId + ":");
                genBody(a.snd);
                return;
            case FuncCall:
                genBody(a.fst);
                output(
                        "sw $a0, 0($sp)",
                        "addi $sp, $sp, -4",

                        "sw $fp, 0($sp)",
                        "addi $sp, $sp, -4",

                        "jal " + a.value, // $ra is set.

                        "lw $fp, 4($sp)",
                        "addi $sp, $sp, 8"
                );
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
