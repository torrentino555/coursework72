import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Calculator {
    public static void main(String[] args) {
        // Чтение сгенерированных таблиц ACTION и GOTO из файла
        ActionAndGoto actionAndGoto = SerializeHelper.deserializeAndGetActionAndGotoFromFile("resources/ActionAndGotoGeneratedByCalculatorGrammar.dat");

        // Чтение файла, который будем анализировать
        String text;
        try {
            text = new String(Files.readAllBytes(Paths.get("resources/inCalc.txt")));
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return;
        }

        // Чтение лексем из файла
        List<Lexeme> lexemes = ParseLexemes.parseLexemes("resources/lexemesForCalculator.txt");
        Lexer lexer = new Lexer(text, lexemes);

        // Запуск SLR анализатора
        Node root = LRAnalyse.parse(lexer, actionAndGoto.getAction(), actionAndGoto.getGoTo());

        // Вычисление результата калькулятором
        double result = CalculatorExecutor.calculate(root);
        System.out.println("Результат вычислений: " + result);
    }
}
