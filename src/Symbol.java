import java.util.Objects;

// TODO: написать equals и hashCode(для использования в map)
public class Symbol {
    private static final String TERMINAL_TYPE = "terminal";
    private static final String NOT_TERMINAL_TYPE = "not_terminal";
    private static final String EPSILON_TYPE = "epsilon";
    public static final Symbol EPSILON = createEpsilon();

    private final String type;
    private final String value;

    private Symbol(String type) {
        this.type = type;
        this.value = null;
    }

    public Symbol(String type, String value) {
        this.type = type;
        this.value = value;
    }

    public static Symbol createTerminal(String value) {
        return new Symbol(TERMINAL_TYPE, value);
    }

    public static Symbol createNonTerminal(String value) {
        return new Symbol(NOT_TERMINAL_TYPE, value);
    }

    public static Symbol createEpsilon() {
        return new Symbol(EPSILON_TYPE);
    }

    public static Symbol createEndTerminal() {
        return createTerminal(DomainTagCalculator.EOF.name());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Symbol symbol = (Symbol) o;
        return Objects.equals(type, symbol.type) && Objects.equals(value, symbol.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, value);
    }

    // Для использования в генераторе
    @Override
    public String toString() {
        return type.equals(EPSILON_TYPE) ? "EPSILON" : value;
    }

    // Для красивого вывода
    public String toPrettyString() {
        return isEpsilon() ? "EPSILON" : isTerminal() ? DomainTagGrammar.getTerminalViewByTagName(value) : value;
    }

    public boolean isTerminal() {
        return type.equals(TERMINAL_TYPE);
    }

    public boolean isNotTerminal() {
        return type.equals(NOT_TERMINAL_TYPE);
    }

    public boolean isEpsilon() {
        return type.equals(EPSILON_TYPE);
    }

    public String getValue() {
        return value;
    }
}
