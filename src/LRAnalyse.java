import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class LRAnalyse {
    public static void main(String[] args) {
        String text;
        try {
//            text = new String(Files.readAllBytes(Paths.get("resources/inCalc.txt")));
            text = new String(Files.readAllBytes(Paths.get("resources/inGrammar.txt")));
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return;
        }

        Grammar grammar1 = new Grammar();
        // Рабочая грамматика калькулятора
//        Symbol E1 = Symbol.createNotTerminal("E'");
//        Symbol E = Symbol.createNotTerminal("E");
//        Symbol T = Symbol.createNotTerminal("T");
//        Symbol F = Symbol.createNotTerminal("F");
//        grammar1.setProductions(List.of(
//                new Production(E1, List.of(E)),
//                new Production(E, List.of(E, Symbol.createTerminal(DomainTagCalculator.OpAdd.name()), T)),
//                new Production(E, List.of(E, Symbol.createTerminal(DomainTagCalculator.OpSub.name()), T)),
//                new Production(E, List.of(T)),
//                new Production(T, List.of(T, Symbol.createTerminal(DomainTagCalculator.OpMul.name()), F)),
//                new Production(T, List.of(T, Symbol.createTerminal(DomainTagCalculator.OpDiv.name()), F)),
//                new Production(T, List.of(F)),
//                new Production(F, List.of(Symbol.createTerminal(DomainTagCalculator.LBracket.name()), E, Symbol.createTerminal(DomainTagCalculator.RBracket.name()))),
//                new Production(F, List.of(Symbol.createTerminal(DomainTagCalculator.IntegerVal.name())))
//        ));
//        grammar1.setStartSymbol(E1);
        // Грамматика распознавания грамматики
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
        grammar1.setProductions(List.of(
                new Production(S, List.of(P)),
                new Production(P, List.of(
                        Symbol.createTerminal(DomainTagGrammar.NonTerminalKeyword.name()),
                        Symbol.createTerminal(DomainTagGrammar.NonTerminalVal.name()),
                        NT,
                        Symbol.createTerminal(DomainTagGrammar.Semicolon.name()),
                        Symbol.createTerminal(DomainTagGrammar.TerminalKeyword.name()),
                        Symbol.createTerminal(DomainTagGrammar.TerminalVal.name()),
                        T,
                        Symbol.createTerminal(DomainTagGrammar.Semicolon.name()),
                        RS,
                        A
                )),
                new Production(NT, List.of(
                        Symbol.createTerminal(DomainTagGrammar.Comma.name()),
                        Symbol.createTerminal(DomainTagGrammar.NonTerminalVal.name()),
                        NT
                )),
                new Production(NT, List.of(Symbol.EPSILON)),
                new Production(T, List.of(
                        Symbol.createTerminal(DomainTagGrammar.Comma.name()),
                        Symbol.createTerminal(DomainTagGrammar.TerminalVal.name()),
                        T
                )),
                new Production(T, List.of(Symbol.EPSILON)),
                new Production(RS, List.of(R, RS)),
                new Production(RS, List.of(Symbol.EPSILON)),
                new Production(R, List.of(
                        Symbol.createTerminal(DomainTagGrammar.NonTerminalVal.name()),
                        Symbol.createTerminal(DomainTagGrammar.Equal.name()),
                        RSR
                )),
                new Production(RSR, List.of(RSR1, Symbol.createTerminal(DomainTagGrammar.Semicolon.name()))),
                new Production(RSR, List.of(RSR1, Symbol.createTerminal(DomainTagGrammar.OpOr.name()), RSR2, Symbol.createTerminal(DomainTagGrammar.Semicolon.name()))),
                new Production(RSR1, List.of(Symbol.createTerminal(DomainTagGrammar.TerminalVal.name()), RSR1)),
                new Production(RSR1, List.of(Symbol.createTerminal(DomainTagGrammar.NonTerminalVal.name()), RSR1)),
                new Production(RSR1, List.of(Symbol.createTerminal(DomainTagGrammar.EpsilonKeyword.name()))),
                new Production(RSR1, List.of(Symbol.EPSILON)),
                new Production(RSR2, List.of(RSR1)),
                new Production(RSR2, List.of(RSR1, Symbol.createTerminal(DomainTagGrammar.OpOr.name()), RSR2)),
                new Production(A, List.of(
                        Symbol.createTerminal(DomainTagGrammar.AxiomKeyword.name()),
                        Symbol.createTerminal(DomainTagGrammar.NonTerminalVal.name()),
                        Symbol.createTerminal(DomainTagGrammar.Semicolon.name())
                ))
        ));
        grammar1.setStartSymbol(S);


        GenerateLRMachine generateLRMachine = new GenerateLRMachine(grammar1);
        generateLRMachine.generateActionAndGoTo();
        generateLRMachine.printActionMap();
        generateLRMachine.printGoToMap();

//        CalculatorLexer lexer = new CalculatorLexer(text);
        GrammarLexer lexer = new GrammarLexer(text);
        Node root = LRAnalyse.parse(lexer, generateLRMachine.getAction(), generateLRMachine.getGoTo());
//        System.out.println("Результат калькулятора: " + Calculator.calculate(root));
        GrammarExtractor grammarExtractor = new GrammarExtractor();
        Grammar resultGrammar = grammarExtractor.extract(root);
    }

    public static Node parse(LexerInterface lexer, Map<Integer, Map<String, TableElementType>> action, Map<Integer, Map<String, Integer>> goTo) {
        Stack<Integer> stack = new Stack<>();
        Stack<Node> nodes = new Stack<>();
        stack.push(0);
        Token currentToken = lexer.getNextToken();
        try {
            while (true) {
                Integer state = stack.peek();
                TableElementType element = action.get(state).get(currentToken.getDomainTagName());
                if (element == null) {
                    // TODO: улучшить вывод ошибок
                    throw new Error("Ошибка синтаксического разбора!");
                }

                if (element.isShift()) {
                    stack.push(element.getState());
                    nodes.push(new Node(currentToken.getDomainTagName(), currentToken.getValue()));
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

