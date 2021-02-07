import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class Program2 {
    public static void main(String[] args) {
        if (args.length != 3) {
            System.out.println("Ваши аргументы: " + Arrays.toString(args));
            System.out.println("Программа запущена с неверным кол-вом аргументов, пример аргументов: grammar.txt lexemesForGrammar.txt ActionAndGotoGenerateByGrammarOfGrammar.dat");
            return;
        }

        // Чтение сгенерированных таблиц ACTION и GOTO из файла
        ActionAndGoto actionAndGoto = SerializeHelper.deserializeAndGetActionAndGotoFromFile("resources/ActionAndGotoGenerateByGrammarOfGrammar.dat");

        // Чтение файла, который будем анализировать
        String text;
        try {
            text = new String(Files.readAllBytes(Paths.get(args[0])));
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return;
        }

        // Чтение лексем из файла
        List<Lexeme> lexemes = ParseLexemes.parseLexemes(args[1]);
        Lexer lexer = new Lexer(text, lexemes);

        // Запуск SLR анализатора
        Node root = LRAnalyse.parse(lexer, actionAndGoto.getAction(), actionAndGoto.getGoTo());

        // Генерация на основе дерева разбора грамматики, которую мы прочитали из файла GrammarGrammars.txt
        GrammarExtractor grammarExtractor = new GrammarExtractor();
        Grammar resultGrammar = grammarExtractor.extract(root);

        // Создание на основе полученной грамматики таблиц ACTION и GOTO
        GenerateLRMachine generateLRMachine = new GenerateLRMachine(resultGrammar);
        generateLRMachine.generateActionAndGoTo();

        // Сериализация таблиц ACTION и GOTO
        SerializeHelper.serializeActionAndGotoInFile(args[2], generateLRMachine);
    }
}
