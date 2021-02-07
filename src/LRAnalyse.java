import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class LRAnalyse {
    public static void main(String[] args) {
        String text;
        try {
            text = new String(Files.readAllBytes(Paths.get("resources/in.txt")));
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return;
        }

        Grammar grammar1 = new Grammar();
//        Symbol S = Symbol.createNotTerminal("S");
//        Symbol E = Symbol.createNotTerminal("E");
//        Symbol E1 = Symbol.createNotTerminal("E1");
//        Symbol T = Symbol.createNotTerminal("T");
//        Symbol T1 = Symbol.createNotTerminal("T1");
//        Symbol F = Symbol.createNotTerminal("F");
//        Symbol N = Symbol.createNotTerminal("N");
//        grammar1.setProductions(List.of(
//                new Production(S, List.of(E)),
//                new Production(E, List.of(T, E1)),
//                new Production(E1, List.of(Symbol.createTerminal(DomainTagCalculator.OpAdd.name()), T, E1)),
//                new Production(E1, List.of(Symbol.createTerminal(DomainTagCalculator.OpSub.name()), T, E1)),
//                new Production(E1, List.of(Symbol.EPSILON)),
//                new Production(T, List.of(F, T1)),
//                new Production(T1, List.of(Symbol.createTerminal(DomainTagCalculator.OpMul.name()), F, T1)),
//                new Production(T1, List.of(Symbol.createTerminal(DomainTagCalculator.OpDiv.name()), F, T1)),
//                new Production(T1, List.of(Symbol.EPSILON)),
//                new Production(F, List.of(N)),
//                new Production(F, List.of(Symbol.createTerminal(DomainTagCalculator.LBracket.name()), E, Symbol.createTerminal(DomainTagCalculator.RBracket.name()))),
//                new Production(N, List.of(Symbol.createTerminal(DomainTagCalculator.IntegerVal.name())))
//        ));
//        grammar1.setStartSymbol(S);
//        grammar1.calculateDeclarations();
        Symbol E1 = Symbol.createNotTerminal("E'");
        Symbol E = Symbol.createNotTerminal("E");
        Symbol T = Symbol.createNotTerminal("T");
        Symbol F = Symbol.createNotTerminal("F");
        grammar1.setProductions(List.of(
                new Production(E1, List.of(E)),
                new Production(E, List.of(E, Symbol.createTerminal(DomainTagCalculator.OpAdd.name()), T)),
                new Production(E, List.of(E, Symbol.createTerminal(DomainTagCalculator.OpSub.name()), T)),
                new Production(E, List.of(T)),
                new Production(T, List.of(T, Symbol.createTerminal(DomainTagCalculator.OpMul.name()), F)),
                new Production(T, List.of(T, Symbol.createTerminal(DomainTagCalculator.OpDiv.name()), F)),
                new Production(T, List.of(F)),
                new Production(F, List.of(Symbol.createTerminal(DomainTagCalculator.LBracket.name()), E, Symbol.createTerminal(DomainTagCalculator.RBracket.name()))),
                new Production(F, List.of(Symbol.createTerminal(DomainTagCalculator.IntegerVal.name())))
        ));
        grammar1.setStartSymbol(E1);
        grammar1.calculateDeclarations();

        GenerateLRMachine generateLRMachine = new GenerateLRMachine(grammar1);
        generateLRMachine.generateActionAndGoTo();

        Lexer lexer = new Lexer(text);
        Node root = LRAnalyse.start(lexer, generateLRMachine.getAction(), generateLRMachine.getGoTo());
        System.out.println("Результат калькулятора: " + Calculator.calculate(root));
    }

    public static Node start(Lexer lexer, Map<Integer, Map<String, TableElementType>> action, Map<Integer, Map<String, Integer>> goTo) {
        Stack<Integer> stack = new Stack<>();
        Stack<Node> nodes = new Stack<>();
        stack.push(0);
        Token currentToken = lexer.getNextToken();
        try {
            while (true) {
                Integer state = stack.peek();
                TableElementType element = action.get(state).get(currentToken.getDomainTag().name());
                if (element == null) {
                    // TODO: улучшить вывод ошибок
                    throw new Error("Ошибка синтаксического разбора!");
                }

                if (element.isShift()) {
                    stack.push(element.getState());
                    nodes.push(new Node(currentToken));
                    currentToken = lexer.getNextToken();
                } else if (element.isReducer()) {
                    Production reducerProduction = element.getProduction();
                    Node parentNode = new Node(reducerProduction.getLNotTerminal().getValue());
                    for (int i = 0; i < reducerProduction.getRSymbols().size(); i++) {
                        parentNode.addChildren(nodes.pop());
                        stack.pop();
                    }
                    Integer t = stack.peek();
                    nodes.push(parentNode);
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

            System.out.println("Синтаксический анализ успешно завершен.");
            return nodes.pop();
        } catch (Error e) {
            e.printStackTrace();
            return null;
        }
    }
}

