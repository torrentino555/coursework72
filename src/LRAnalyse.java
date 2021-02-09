import java.util.Map;
import java.util.Stack;

public class LRAnalyse {
    public static Node parse(Lexer lexer, Map<Integer, Map<String, TableElementType>> action, Map<Integer, Map<String, Integer>> goTo) {
        Stack<Integer> stack = new Stack<>();
        Stack<Node> nodes = new Stack<>();
        stack.push(0);
        Token currentToken = lexer.getNextToken();
        try {
            while (true) {
                Integer state = stack.peek();
                TableElementType element = action.get(state).get(currentToken.getLexemeName());
                if (element == null) {
                    throw new Error("Ошибка синтаксического разбора! %s\nОжидались токены: %s"
                            .formatted(currentToken.toString(), String.join(", ", action.get(state).keySet())));
                }

                if (element.isShift()) {
                    stack.push(element.getState());
                    nodes.push(new Node(currentToken.getLexemeName(), currentToken.getValue()));
                    currentToken = lexer.getNextToken();
                } else if (element.isReducer()) {
                    Production reducerProduction = element.getProduction();
                    Node parentNode = new Node(reducerProduction.getLNotTerminal().getValue());
                    if (!reducerProduction.isEpsilonProduction()) {
                        for (int i = 0; i < reducerProduction.getRSymbols().size(); i++) {
                            parentNode.addChildren(nodes.pop());
                            stack.pop();
                        }
                    }
                    Integer lastState = stack.peek();
                    parentNode.reverseChildren();
                    nodes.push(parentNode);
                    stack.push(goTo.get(lastState).get(reducerProduction.getLNotTerminal().getValue()));

                    if (ProdOrDebug.isDebug) {
                        System.out.println(reducerProduction);
                    }
                } else if (element.isAccept()) {
                    break;
                }
            }

            System.out.println("Синтаксический анализ успешно завершен.");
            return nodes.pop();
        } catch (Error e) {
            e.printStackTrace();
            return null;
        }
    }
}

