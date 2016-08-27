import java.util.Arrays;

public class Debug {
    public static void print(Object... os) {
        System.err.println(Arrays.deepToString(os));
    }
}
