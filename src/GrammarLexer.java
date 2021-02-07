import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class GrammarLexer implements LexerInterface {
    private Position position;

    public GrammarLexer(String text) {
        this.position = new Position(text);
    }

    public Token getNextToken() {
        if (position.isEOF()) {
            return Token.createEOFToken();
        }

        position = position.skipWhitespaces();
        String tail = position.getTail();

        Position start, end;
        start = new Position(position);

        for (DomainTagGrammar tag : DomainTagGrammar.values()) {
            String match = tag.match(tail);
            if (match != null) {
                position = position.skipSymbols(match.length());
                end = new Position(position);
                return new Token(tag.name(), start, end);
            }
        }

        throw new Error("Ошибка лексического анализа на позиции " + position.toString());
    }

    public static void main(String[] args) {
        String text;
        try {
            text = new String(Files.readAllBytes(Paths.get("resources/inCalc.txt")));
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return;
        }

        CalculatorLexer calculatorLexer = new CalculatorLexer(text);
        Token token = calculatorLexer.getNextToken();
        while (!token.getDomainTagName().equals(DomainTagGrammar.EOF.name())) {
            System.out.println(token.toString());
            token = calculatorLexer.getNextToken();
        }
    }
}
