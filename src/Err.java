import java.util.Arrays;

public class Err {
    public static void checkIn(String s, String... set) {
        for(String t:set) {
            if(s.equals(t)) return;
        }
        throw Err.format("Expected to be in %s but was %s.", Arrays.asList(set), s);
    }

    public static IllegalArgumentException format(String tmpl, Object... os) {
        return new IllegalArgumentException(String.format(tmpl, os));
    }
}