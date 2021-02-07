import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Lexeme {
    public static final String EOF_LEXEME_NAME = "EOF";

    private final String name;
    private final Pattern pattern;

    public Lexeme(String name, String regExp) {
        this.name = name;
        this.pattern = Pattern.compile("^" + regExp);
    }

    public String match(String s) {
        if (pattern == null) {
            return null;
        }

        Matcher matcher = pattern.matcher(s);
        return matcher.find() ? matcher.group() : null;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Лексема: " + name + " -> " + pattern.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Lexeme lexeme = (Lexeme) o;
        return Objects.equals(name, lexeme.name) && Objects.equals(pattern, lexeme.pattern);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, pattern);
    }
}
