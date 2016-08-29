import org.junit.Test;

public class MainTest {

    void test(String input){
        System.err.println("testing: " + input);
        new Main().compile(input);
    }

    @Test
    public void testCompile() {
        test("\"A\"");
        test("\"Hello, World!\\n\"");
        test("1+1");
        test("1");
        test("1+1+2");
        test("-2+1");
        test("-2147483648");
        test("(-1+ + - +2)*3");
        test("v:=1\nv");
    }
}
