import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Bootstrapping {
    public static void main(String[] args) {
        // Грамматика, написанная вручную
        Grammar grammar = new Grammar();
        Symbol S = Symbol.createNonTerminal("P'");
        Symbol P = Symbol.createNonTerminal("P");
        Symbol NT = Symbol.createNonTerminal("NT");
        Symbol T = Symbol.createNonTerminal("T");
        Symbol RS = Symbol.createNonTerminal("RS");
        Symbol R = Symbol.createNonTerminal("R");
        Symbol RSR = Symbol.createNonTerminal("RSR");
        Symbol RSR1 = Symbol.createNonTerminal("RSR1");
        Symbol RSR2 = Symbol.createNonTerminal("RSR2");
        Symbol A = Symbol.createNonTerminal("A");
        grammar.setProductions(List.of(
                new Production(S, List.of(P)),
                new Production(P, List.of(
                        Symbol.createTerminal("non-terminal-keyword"),
                        Symbol.createTerminal("non-terminal-value"),
                        NT,
                        Symbol.createTerminal(";"),
                        Symbol.createTerminal("terminal-keyword"),
                        Symbol.createTerminal("terminal-value"),
                        T,
                        Symbol.createTerminal(";"),
                        RS,
                        A
                )),
                new Production(NT, List.of(
                        Symbol.createTerminal(","),
                        Symbol.createTerminal("non-terminal-value"),
                        NT
                )),
                new Production(NT, List.of(Symbol.EPSILON)),
                new Production(T, List.of(
                        Symbol.createTerminal(","),
                        Symbol.createTerminal("terminal-value"),
                        T
                )),
                new Production(T, List.of(Symbol.EPSILON)),
                new Production(RS, List.of(R, RS)),
                new Production(RS, List.of(Symbol.EPSILON)),
                new Production(R, List.of(
                        Symbol.createTerminal("non-terminal-value"),
                        Symbol.createTerminal("::="),
                        RSR
                )),
                new Production(RSR, List.of(RSR1, Symbol.createTerminal(";"))),
                new Production(RSR, List.of(RSR1, Symbol.createTerminal("|"), RSR2, Symbol.createTerminal(";"))),
                new Production(RSR1, List.of(Symbol.createTerminal("terminal-value"), RSR1)),
                new Production(RSR1, List.of(Symbol.createTerminal("non-terminal-value"), RSR1)),
                new Production(RSR1, List.of(Symbol.createTerminal("epsilon-keyword"))),
                new Production(RSR1, List.of(Symbol.EPSILON)),
                new Production(RSR2, List.of(RSR1)),
                new Production(RSR2, List.of(RSR1, Symbol.createTerminal("|"), RSR2)),
                new Production(A, List.of(
                        Symbol.createTerminal("axiom-keyword"),
                        Symbol.createTerminal("non-terminal-value"),
                        Symbol.createTerminal(";")
                ))
        ));
        grammar.setStartSymbol(S);

        // Создание таблицы SLR на основе описанной выше грамматики
        GenerateLRMachine generateLRMachine = new GenerateLRMachine(grammar);
        generateLRMachine.generateActionAndGoTo();

        // Чтение файла, который будем анализировать
        String text;
        try {
            text = new String(Files.readAllBytes(Paths.get("resources/GrammarGrammars.txt")));
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return;
        }

        // Чтение лексем из файла
        List<Lexeme> lexemes = ParseLexemes.parseLexemes("resources/lexemesForGrammar.txt");
        Lexer lexer = new Lexer(text, lexemes);

        // Запуск SLR анализатора
        Node root = LRAnalyse.parse(lexer, generateLRMachine.getAction(), generateLRMachine.getGoTo());

        // Генерация на основе дерева разбора грамматики, которую мы прочитали из файла GrammarGrammars.txt
        GrammarExtractor grammarExtractor = new GrammarExtractor();
        Grammar resultGrammar = grammarExtractor.extract(root);

        // Создание на основе полученной грамматики таблиц ACTION и GOTO
        GenerateLRMachine generateLRMachine1 = new GenerateLRMachine(resultGrammar);
        generateLRMachine1.generateActionAndGoTo();

        // Сериализация таблиц ACTION и GOTO
        SerializeHelper.serializeActionAndGotoInFile("resources/ActionAndGotoGenerateByGrammarOfGrammar.dat", generateLRMachine);
    }
}
