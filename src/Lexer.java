import java.util.List;

public class Lexer {
    private Position position;
    private final List<Lexeme> lexemes;

    public Lexer(String text, List<Lexeme> lexemes) {
        this.position = new Position(text);
        this.lexemes = lexemes;
    }

    public Token getNextToken() {
        if (position.isEOF()) {
            return Token.createEOFToken();
        }

        position = Position.skipWhitespaces(position);
        String tail = position.getTail();

        Position start, end;
        start = new Position(position);

        for (Lexeme lexeme : lexemes) {
            String match = lexeme.match(tail);
            if (match != null) {
                position = position.skipSymbols(match.length());
                end = new Position(position);
                return new Token(lexeme.getName(), start, end);
            }
        }

        throw new Error("Ошибка лексического анализа на позиции " + position.toString());
    }
}
