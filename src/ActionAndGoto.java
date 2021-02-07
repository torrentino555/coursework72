import java.util.Map;

public class ActionAndGoto {
    private final Map<Integer, Map<String, TableElementType>> action;
    private final Map<Integer, Map<String, Integer>> goTo;

    public ActionAndGoto(Map<Integer, Map<String, TableElementType>> action, Map<Integer, Map<String, Integer>> goTo) {
        this.action = action;
        this.goTo = goTo;
    }

    public Map<Integer, Map<String, TableElementType>> getAction() {
        return action;
    }

    public Map<Integer, Map<String, Integer>> getGoTo() {
        return goTo;
    }
}
