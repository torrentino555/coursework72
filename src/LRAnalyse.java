import java.util.*;
import java.util.stream.Collectors;

public class LRAnalyse {
    // Это грамматика, по которой мы разбираем текст на входном языке
    private Grammar grammar;

    public LRAnalyse(Grammar grammar) {
        this.grammar = grammar;
    }

    public void start(List<Token> tokenList, Map<Integer, Map<String, TableElementType>> action, Map<Integer, Map<String, Integer>> goTo) {
        Stack<Integer> stack = new Stack<>();
        // Пусть нулевое состояние будет начальным
        stack.push(0);
        int tokenIndex = 0;
        String a = tokenList.get(tokenIndex++).toString();
        try {
            while (true) {
                Integer state = stack.peek();
                TableElementType element = action.get(state).get(a);
                if (element.isShift()) {
                    stack.push(element.getState());
                    a = tokenList.get(tokenIndex++).toString();
                } else if (element.isReducer()) {
                    Production reducerProduction = grammar.getProduction(element.getState());
                    for (int i = 0; i < reducerProduction.getRSymbols().size(); i++) {
                        stack.pop();
                    }
                    Integer t = stack.peek();
                    stack.push(goTo.get(t).get(reducerProduction.getLNotTerminal().getValue()));

                    if (ProdOrDebug.isDebug) {
                        System.out.println(reducerProduction);
                    }
                } else if (element.isAccept()) {
                    break;
                } else {
                    // TODO: улучшить вывод ошибок
                    throw new Error("Ошибка синтаксического разбора!");
                }
            }
        } catch (Error e) {
            e.printStackTrace();
        }

        System.out.println("Синтаксический анализ успешно завершен.");
    }
}

