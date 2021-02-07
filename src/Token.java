import java.util.Objects;

public class Token {
    private final String domainTagName;
    private Fragment coords;
    private String value;

    public Token(String domainTagName, String value) {
        this.domainTagName = domainTagName;
        this.value = value;
    }

    public Token(String domainTagName, Position start, Position follow) {
        this.domainTagName = domainTagName;
        this.coords = new Fragment(start, follow);
        this.value = this.coords.getFragmentValue();
    }

    public static Token createEOFToken() {
        return new Token(DomainTagCalculator.EOF.name(), DomainTagCalculator.EOF.name());
    }

    public String getDomainTagName() {
        return domainTagName;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return domainTagName + " " + coords.toString() + ": " + value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Token token = (Token) o;
        return domainTagName.equals(token.domainTagName) && Objects.equals(value, token.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(domainTagName, value);
    }
}
