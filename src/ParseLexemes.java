import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ParseLexemes {
    public static List<Lexeme> parseLexemes(String path) {
        String text;
        try {
            text = new String(Files.readAllBytes(Paths.get(path)));
        } catch (IOException e) {
            System.out.println("Не удалось прочитать файл лексем: " + path);
            return new ArrayList<>();
        }

        List<Lexeme> lexemes = new ArrayList<>();
        String[] lines = text.split("\n|\r\n");
        for (String line: lines) {
            String[] cols = line.split(" ");
            if (cols.length == 1) {
                lexemes.add(new Lexeme(cols[0], cols[0]));
            } else if (cols.length == 2) {
                lexemes.add(new Lexeme(cols[0], cols[1]));
            } else {
                System.out.println("Строка имеет неверный синтаксис:\n" + line + "\n");
            }
        }
        System.out.format("Генерация лексем из файла %s успешно завершена.\n", path);
        return lexemes;
    }
}
