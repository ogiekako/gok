import org.junit.Test;

public class MainTest {

    void testMain(String input) {
        test("func main() string {\n" + input + "\n}");
    }

    void test(String input){
        System.err.println("testing: " + input);
        new Main().compile(input);
    }

    @Test
    public void testCompile() {
        testMain("true");
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
        test("func f() {\n}\nfunc main(){}");
        test("func f(i int) int {\ni\n}\nfunc main() int {\nf(42)\n}");
        test("func f(i bool) bool {\ni\n}\nfunc main() bool {\nf(true)\n}");
    }
}
