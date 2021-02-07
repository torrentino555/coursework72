import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum DomainTagGrammar {
    NonTerminalKeyword("non-terminal"),
    TerminalKeyword("terminal"),
    AxiomKeyword("axiom"),
    EpsilonKeyword("epsilon"),
    Equal("::="),
    Semicolon(";"),
    OpOr("\\|"),
    Comma(","),
    NonTerminalVal("[A-Z][A-Z0-9]*"),
    TerminalVal("('[^']+')|([a-z][a-zA-Z]*)"),
    EOF;

    private Pattern pattern;
    private String regExp;

    DomainTagGrammar() {
    }

    DomainTagGrammar(String regExp) {
        this.regExp = regExp;
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
        if (name.equals(NonTerminalKeyword.name()))
            return NonTerminalKeyword.regExp;
        if (name.equals(TerminalKeyword.name()))
            return TerminalKeyword.regExp;
        if (name.equals(AxiomKeyword.name()))
            return AxiomKeyword.regExp;
        if (name.equals(EpsilonKeyword.name()))
            return EpsilonKeyword.regExp;
        if (name.equals(Equal.name()))
            return Equal.regExp;
        if (name.equals(Semicolon.name()))
            return Semicolon.regExp;
        if (name.equals(OpOr.name()))
            return "|";
        if (name.equals(Comma.name()))
            return Comma.regExp;
        if (name.equals(NonTerminalVal.name()))
            return "nonTerminalVal";
        if (name.equals(TerminalVal.name()))
            return "terminalVal";
        if (name.equals(EOF.name()))
            return "$";
        return "?";
    }
}
