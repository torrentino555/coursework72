import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum DomainTagGrammar {
    NonTerminal("non_terminal"),
    Terminal("terminal"),
    Axiom("axiom"),
    Epsilon("epsilon"),
    Equal("::="),
    Semicolon(";"),
    OpOr("\\|"),
    Term("'[^']+'"),
    Comma(","),
    Ident("\\d"),
    EOF;

    private Pattern pattern;

    DomainTagGrammar() {
    }

    DomainTagGrammar(String regExp) {
        this.pattern = Pattern.compile("^" + regExp);
    }

    public String match(String s) {
        if (pattern == null) {
            return null;
        }

        Matcher matcher = pattern.matcher(s);
        return matcher.find() ? matcher.group() : null;
    }

    public static String getTerminalViewByTagName(String name) {
        // TODO: написать
        return "";
    }
}
