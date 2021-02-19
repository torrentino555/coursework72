import java.util.*;
import java.util.stream.Collectors;

public class GenerateLRMachine {
    private final Grammar grammar;
    private final List<Map<Symbol, Integer>> relations = new ArrayList<>();
    private final Map<Integer, Map<String, TableElementType>> action = new HashMap<>();
    private final Map<Integer, Map<String, Integer>> goTo = new HashMap<>();

    public GenerateLRMachine(Grammar grammar) {
        this.grammar = new Grammar(grammar);

    }

    public void printActionMap() {
        StringBuilder buffer = new StringBuilder();
        List<Symbol> terminals = grammar.getDeclarations().stream().filter(Symbol::isTerminal).collect(Collectors.toList());
        terminals.add(Symbol.createEndTerminal());
        buffer.append("    ");
        List<Integer> sizes = new ArrayList<>();
        for (Symbol s : terminals) {
            Integer size = Math.max(s.toPrettyString().length(), 3);
            sizes.add(size);
            buffer.append(String.format("%-" + size + "s|", s.toPrettyString()));
        }
        buffer.append("\n");
        for (int i = 0; i < action.size(); i++) {
            int startIndex = buffer.length();
            buffer.append(String.format("%-2s", i)).append(": ");
            for (int j = 0; j < terminals.size(); j++) {
                Symbol s = terminals.get(j);
                Map<String, TableElementType> currentAction = action.get(i);
                buffer.append(
                        String.format(
                                "%-" + sizes.get(j) + "s|",
                                currentAction.containsKey(s.toString()) ? currentAction.get(s.toString()).toString() : ""
                        ));
            }
            int endIndex = buffer.length();
            buffer.append("\n");
            buffer.append("-".repeat(Math.max(0, endIndex - startIndex)));
            buffer.append("\n");
        }
        System.out.println(buffer.toString());
    }

    public void printGoToMap() {
        StringBuilder buffer = new StringBuilder();
        List<Symbol> notTerminals = grammar.getDeclarations().stream().filter(Symbol::isNotTerminal).collect(Collectors.toList());
        buffer.append("    ");
        List<Integer> sizes = new ArrayList<>();
        for (Symbol s : notTerminals) {
            Integer size = Math.max(s.toString().length(), 2);
            sizes.add(size);
            buffer.append(String.format("%-" + size + "s|", s.toString()));
        }
        buffer.append("\n");
        for (int i = 0; i < goTo.size(); i++) {
            int startIndex = buffer.length();
            buffer.append(String.format("%-2s", i)).append(": ");
            for (int j = 0; j < notTerminals.size(); j++) {
                Symbol s = notTerminals.get(j);
                Map<String, Integer> currentGoto = goTo.get(i);
                buffer.append(
                        String.format(
                                "%-" + sizes.get(j) + "s|",
                                currentGoto.containsKey(s.toString()) ? currentGoto.get(s.toString()) : ""
                        ));
            }
            int endIndex = buffer.length();
            buffer.append("\n");
            buffer.append("-".repeat(Math.max(0, endIndex - startIndex)));
            buffer.append("\n");
        }
        System.out.println(buffer.toString());
    }

    public String printItems(List<ItemsSet> itemsSets) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < itemsSets.size(); i++) {
            stringBuilder.append(i).append(":").append(itemsSets.get(i));
        }
        return stringBuilder.toString();
    }

    public void generateActionAndGoTo() {
        List<ItemsSet> machine = generateItems();
        FirstAndFollowGenerator firstAndFollowGenerator = new FirstAndFollowGenerator(grammar);
        if (ProdOrDebug.isDebug)
            System.out.println(printItems(machine));

        for (int i = 0; i < machine.size(); i++) {
            action.put(i, new HashMap<>());
            goTo.put(i, new HashMap<>());
            Set<ProductionWithItem> productions = machine.get(i).getSet();

            for (ProductionWithItem production : productions) {
                if (production.getSymbolAfterPoint().isTerminal()) {
                    Symbol symbolAfterPoint = production.getSymbolAfterPoint();
                    TableElementType elementType = TableElementType.createShift(relations.get(i).get(symbolAfterPoint));
                    if (action.get(i).containsKey(symbolAfterPoint.toString()))
                        throw new Error("Ошибка, грамматика не принадлежит классу SLR.");
                    action.get(i).put(symbolAfterPoint.toString(), elementType);
                }

                if ((production.itemAtTheEnd() || production.isEpsilonProduction()) &&
                        !production.getProduction().getLNotTerminal().equals(grammar.getStartSymbol())) {
                    Integer reduceProductionIndex = grammar.getProductionIndex(production.getProduction());
                    Set<Symbol> follow = firstAndFollowGenerator.calcFollow(production.getProduction().getLNotTerminal());
                    for (Symbol symbol : follow) {
                        if (action.get(i).containsKey(symbol.toString()))
                            throw new Error("Ошибка, грамматика не принадлежит классу SLR.");
                        action.get(i).put(symbol.toString(), TableElementType.createReduce(reduceProductionIndex, production.getProduction()));
                    }
                }

                if (production.getProduction().getLNotTerminal().equals(grammar.getStartSymbol()) && production.itemAtTheEnd()) {
                    if (action.get(i).containsKey(Symbol.createEndTerminal().toString()))
                        throw new Error("Ошибка, грамматика не принадлежит классу SLR.");
                    action.get(i).put(Symbol.createEndTerminal().toString(), TableElementType.createAccept());
                }
            }

            List<Symbol> keySet = relations.get(i).keySet().stream().filter(Symbol::isNotTerminal).collect(Collectors.toList());
            for (Symbol s : keySet) {
                goTo.get(i).put(s.toString(), relations.get(i).get(s));
            }
        }

        if (ProdOrDebug.isDebug) {
            printActionMap();
            printGoToMap();
        }
        System.out.println("Формирование таблиц ACTION и GOTO успешно завершено.");
    }

    public List<ItemsSet> generateItems() {
        ItemsSet basicState = new ItemsSet(grammar.getStartProduction());
        List<ItemsSet> states = new ArrayList<>(List.of(closure(basicState)));

        Set<ItemsSet> alreadyDone = new HashSet<>();
        int oldSize, currentSize;
        do {
            oldSize = states.size();
            List<ItemsSet> newStates = new ArrayList<>(states);
            for (int i = 0; i < states.size(); i++) {
                ItemsSet state = states.get(i);
                if (alreadyDone.contains(state)) {
                    continue;
                }

                for (Symbol s : grammar.getDeclarations()) {
                    ItemsSet nextI = goTo(state, s);
                    if (nextI.getSet().size() != 0) {
                        while (i >= relations.size()) {
                            relations.add(new HashMap<>());
                        }
                        if (!newStates.contains(nextI)) {
                            relations.get(i).put(s, newStates.size());
                            relations.add(new HashMap<>());
                            newStates.add(nextI);
                        } else {
                            relations.get(i).put(s, newStates.indexOf(nextI));
                        }
                    }
                }

                alreadyDone.add(state);
            }
            states = newStates;
            currentSize = states.size();
        } while (oldSize < currentSize);
        return states;
    }

    public ItemsSet goTo(ItemsSet I, Symbol X) {
        Set<ProductionWithItem> newSet = I.getSet().stream().filter(production -> production.getSymbolAfterPoint().equals(X)).collect(Collectors.toSet());
        return closure(new ItemsSet(newSet.stream().map(ProductionWithItem::incrementItem).collect(Collectors.toSet())));
    }



    public ItemsSet closure(ItemsSet I) {
        ItemsSet itemsSet = new ItemsSet(I);
        Set<ProductionWithItem> alreadyDone = new HashSet<>();
        int oldSize, currentSize;
        do {
            oldSize = itemsSet.getSet().size();
            Set<ProductionWithItem> newSet = new HashSet<>(itemsSet.getSet());
            for (ProductionWithItem elementFromJ : itemsSet.getSet()) {
                if (alreadyDone.contains(elementFromJ)) {
                    continue;
                }

                if (elementFromJ.getSymbolAfterPoint().isNotTerminal()) {
                    for (Production production : grammar.getProductionsByNotTerminal(elementFromJ.getSymbolAfterPoint())) {
                        newSet.add(new ProductionWithItem(production));
                    }
                }

                alreadyDone.add(elementFromJ);
            }
            itemsSet.setSet(newSet);
            currentSize = itemsSet.getSet().size();
        } while (oldSize < currentSize);
        return itemsSet;
    }

    public Map<Integer, Map<String, TableElementType>> getAction() {
        return action;
    }

    public Map<Integer, Map<String, Integer>> getGoTo() {
        return goTo;
    }
}
