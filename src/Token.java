import java.text.MessageFormat;
import java.util.Objects;

public class Token {
    private final String lexemeName;
    private Fragment coords;
    private final String value;

    public Token(String lexemeName, String value) {
        this.lexemeName = lexemeName;
        this.value = value;
    }

    public Token(String lexemeName, Position start, Position follow) {
        this.lexemeName = lexemeName;
        this.coords = new Fragment(start, follow);
        this.value = this.coords.getFragmentValue();
    }

    public static Token createEOFToken() {
        return new Token(Lexeme.EOF_LEXEME_NAME, Lexeme.EOF_LEXEME_NAME);
    }

    public String getLexemeName() {
        return lexemeName;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return MessageFormat.format("Токен ''{0}'' {1}: {2}", lexemeName, coords.toString(), value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Token token = (Token) o;
        return lexemeName.equals(token.lexemeName) && Objects.equals(value, token.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lexemeName, value);
    }
}
