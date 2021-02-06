import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum DomainTagCalculator {
    LBracket("\\("),
    RBracket("\\)"),
    OpAdd("\\+"),
    OpSub("-"),
    OpMul("\\*"),
    OpDiv("/"),
    IntegerVal("\\d+"),
    EOF;

    private Pattern pattern;

    DomainTagCalculator() {
    }

    DomainTagCalculator(String regExp) {
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
        if (name.equals(LBracket.name()))
            return "(";
        if (name.equals(RBracket.name()))
            return ")";
        if (name.equals(OpAdd.name()))
            return "+";
        if (name.equals(OpMul.name()))
            return "*";
        if (name.equals(IntegerVal.name()))
            return "IntValue";
        if (name.equals(EOF.name()))
            return "$";
        return "";
    }
}
