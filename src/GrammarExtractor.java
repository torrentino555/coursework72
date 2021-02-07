import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GrammarExtractor {
    private Grammar grammar = new Grammar();
    private Set<Symbol> nonTerminals = new HashSet<>();
    private Set<Symbol> terminals = new HashSet<>();

    public GrammarExtractor() {
    }

    public  Grammar extract(Node root) {
        parseP(root);
        Symbol newStartSymbol = Symbol.createNonTerminal(grammar.getStartSymbol().getValue() + "'");
        grammar.addProduction(new Production(newStartSymbol, List.of(grammar.getStartSymbol())));
        grammar.setStartSymbol(newStartSymbol);
        return grammar;
    }

    private void parseP(Node node) {
        nonTerminals.add(Symbol.createNonTerminal(node.getChild(1).getValue()));
        parseNT(node.getChild(2));
        terminals.add(Symbol.createTerminal(processTerminal(node.getChild(5).getValue())));
        parseT(node.getChild(6));
        parseRS(node.getChild(8));
        parseA(node.getChild(9));
    }

    private void parseNT(Node node) {
        if (node.getChildren().size() == 3) {
            Symbol nonTerminal = Symbol.createNonTerminal(node.getChild(1).getValue());
            if (nonTerminals.contains(nonTerminal)) {
                throw new Error("В объявлении 'non-terminal' дублируется не терминал: " + nonTerminal.toPrettyString());
            }
            nonTerminals.add(nonTerminal);
            parseNT(node.getChild(2));
        }
    }

    private void parseT(Node node) {
        if (node.getChildren().size() == 3) {
            Symbol terminal = Symbol.createTerminal(processTerminal(node.getChild(1).getValue()));
            if (terminals.contains(terminal)) {
                throw new Error("В объявлении 'terminal' дублируется терминал: " + terminal.toPrettyString());
            }
            terminals.add(terminal);
            parseT(node.getChild(2));
        }
    }

    private void parseRS(Node node) {
        if (node.getChildren().size() == 2) {
            parseR(node.getChild(0));
            parseRS(node.getChild(1));
        }
    }

    private void parseR(Node node) {
        Symbol lNonTerminal = Symbol.createNonTerminal(node.getChild(0).getValue());
        if (!nonTerminals.contains(lNonTerminal)) {
            throw new Error("В левой части правила грамматики используется незнакомый не терминал: " + lNonTerminal.toPrettyString());
        }

        parseRSR(node.getChild(2), lNonTerminal);
    }

    private void parseRSR(Node node, Symbol lNonTerminal) {
        List<Symbol> rSymbols = parseRSR1(node.getChild(0));
        grammar.addProduction(new Production(lNonTerminal, rSymbols));

        if (node.getChildren().size() == 4) {
            parseRSR2(node.getChild(2), lNonTerminal);
        }
    }

    private List<Symbol> parseRSR1(Node node) {
        List<Symbol> symbols = new ArrayList<>();

        if (node.getChildren().size() == 0) {
            return symbols;
        }

        if (node.getChildren().size() == 1) {
            symbols.add(Symbol.EPSILON);
            return symbols;
        }

        // TODO: терминалы с '' будут кривыми
        if (node.getChild(0).isTerminal()) {
            Symbol terminal = Symbol.createTerminal(processTerminal(node.getChild(0).getValue()));
            if (!terminals.contains(terminal)) {
                throw new Error("В объявлении правой части правила был использован незнакомый терминал: " + terminal.toPrettyString());
            }

            symbols.add(terminal);
        } else {
            Symbol nonTerminal = Symbol.createNonTerminal(node.getChild(0).getValue());
            if (!nonTerminals.contains(nonTerminal)) {
                throw new Error("В объявлении правой части правила был использован незнакомый не терминал: " + nonTerminal.toPrettyString());
            }

            symbols.add(nonTerminal);
        }
        symbols.addAll(parseRSR1(node.getChild(1)));
        return symbols;
    }

    public static String processTerminal(String rowTerminalValue) {
        if (rowTerminalValue.startsWith("'") && rowTerminalValue.endsWith("'")) {
            return rowTerminalValue.substring(1, rowTerminalValue.length() - 1);
        }
        return rowTerminalValue;
    }

    private void parseRSR2(Node node, Symbol lNonTerminal) {
        List<Symbol> rSymbols = parseRSR1(node.getChild(0));
        grammar.addProduction(new Production(lNonTerminal, rSymbols));

        if (node.getChildren().size() == 3) {
            parseRSR2(node.getChild(2), lNonTerminal);
        }
    }

    private void parseA(Node node) {
        Symbol startSymbol = Symbol.createNonTerminal(node.getChild(1).getValue());
        if (!nonTerminals.contains(startSymbol)) {
            throw new Error("В объявлении 'axiom' используется незнакомый не терминал: " + startSymbol.toPrettyString());
        }
        grammar.setStartSymbol(startSymbol);
    }
}
