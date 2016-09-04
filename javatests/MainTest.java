import org.junit.Test;

public class MainTest {

    void testMain(String input) {
        testNoPkg("func main() string {\n" + input + "\n}");
    }

    void testNoPkg(String input){
        input = "package main\n" + input;
        System.err.println("testing: " + input);
        new Main().compile(input);
    }

    @Test
    public void testCompile() {
        testMain("return true");
        testMain("false");
        testMain("\"A\"");
        testMain("\"Hello, World!\\n\"");
        testMain("1<1");
        testMain("1+1");
        testMain("1");
        testMain("1+1+2");
        testMain("-2+1");
        testMain("-2147483648");
        testMain("(-1+ + - +2)*3");
        testMain("v:=1\nv");
        testMain("v:=0\nif 1 < 2 {\nv:=v+1\n}\nv");
        testNoPkg("func f() {\n}\nfunc main(){}");
        testNoPkg("func f(i int) int {\ni\n}\nfunc main() int {\nf(42)\n}");
        testNoPkg("func f(i bool) bool {\ni\n}\nfunc main() bool {\nf(true)\n}");
    }
}
