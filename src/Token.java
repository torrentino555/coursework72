import java.util.Objects;

public class Token {
    private final DomainTagCalculator domainTag;
    private Fragment coords;
    private String value;

    public Token(DomainTagCalculator domainTag, String value) {
        this.domainTag = domainTag;
        this.value = value;
    }

    public Token(DomainTagCalculator domainTag, Position start, Position follow) {
        this.domainTag = domainTag;
        this.coords = new Fragment(start, follow);
        this.value = this.coords.getFragmentValue();
    }

    public static Token createEOFToken() {
        return new Token(DomainTagCalculator.EOF, DomainTagCalculator.EOF.name());
    }

    public DomainTagCalculator getDomainTag() {
        return domainTag;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return domainTag.toString() + " " + coords.toString() + ": " + value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Token token = (Token) o;
        return domainTag == token.domainTag && Objects.equals(value, token.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(domainTag, value);
    }
}
