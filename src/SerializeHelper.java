import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Map;

public class SerializeHelper {
    public static void serializeActionAndGotoInFile(String path, GenerateLRMachine generateLRMachine) {
        try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path)))
        {
            oos.writeObject(generateLRMachine.getAction());
            oos.writeObject(generateLRMachine.getGoTo());
            System.out.format("Файл %s успешно записан.", path);
        }
        catch(Exception ex){
            System.out.println(ex.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public static ActionAndGoto deserializeAndGetActionAndGotoFromFile(String path) {
        try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path)))
        {
            return new ActionAndGoto((Map<Integer, Map<String, TableElementType>>)ois.readObject(), (Map<Integer, Map<String, Integer>>)ois.readObject());
        }
        catch(Exception ex){
            System.out.println(ex.getMessage());
            throw new Error("Не удалось прочитать action и goto из файла " + path);
        }
    }
}
