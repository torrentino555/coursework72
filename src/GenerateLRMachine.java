import java.util.*;
import java.util.stream.Collectors;

public class GenerateLRMachine {
    private final Grammar grammar;
    private final List<Map<Symbol, Integer>> relations = new ArrayList<>();
    private final Map<Integer, Map<String, TableElementType>> action = new HashMap<>();
    private final Map<Integer, Map<String, Integer>> goTo = new HashMap<>();

    // На вход подается расширенная грамматика
    public GenerateLRMachine(Grammar grammar) {
        this.grammar = new Grammar(grammar);

    }

    public static void main(String[] args) {
        Grammar grammar1 = new Grammar();
        Symbol E1 = Symbol.createNotTerminal("E'");
        Symbol E = Symbol.createNotTerminal("E");
        Symbol T = Symbol.createNotTerminal("T");
        Symbol F = Symbol.createNotTerminal("F");
        grammar1.setProductions(List.of(
                new Production(E1, List.of(E)),
                new Production(E, List.of(E, Symbol.createTerminal("+"), T)),
                new Production(E, List.of(T)),
                new Production(T, List.of(T, Symbol.createTerminal("*"), F)),
                new Production(T, List.of(F)),
                new Production(F, List.of(Symbol.createTerminal("("), E, Symbol.createTerminal(")"))),
                new Production(F, List.of(Symbol.createTerminal("id")))
        ));
        grammar1.setStartSymbol(E1);
        grammar1.calculateDeclarations();

        GenerateLRMachine generateLRMachine = new GenerateLRMachine(grammar1);
        generateLRMachine.generateActionAndGoTo();

    }

    public void printActionMap() {
        StringBuilder buffer = new StringBuilder();
        List<Symbol> terminals = grammar.getDeclarations().stream().filter(Symbol::isTerminal).collect(Collectors.toList());
//        // TODO: удалить, это для отладки
//        List<Symbol> terminals = new ArrayList<>(List.of(Symbol.createTerminal("id"), Symbol.createTerminal("+"), Symbol.createTerminal("*"), Symbol.createTerminal("("), Symbol.createTerminal(")")));
//        List<Integer> reverseMap = new ArrayList<>(List.of(0, 4, 3, 5, 1, 2, 8, 7, 6, 11, 10, 9));
//        //
        terminals.add(Symbol.createEndTerminal());
        buffer.append("    ");
        for (Symbol s : terminals) {
            buffer.append(s.toString()).append("  ");
        }
        buffer.append("\n");
        for (int i = 0; i < action.size(); i++) {
            buffer.append(i > 9 ? i : i + " ").append(": ");
            for (Symbol s: terminals) {
                buffer.append(action.get(i).containsKey(s.toString()) ? action.get(i).get(s.toString()).toString() + " " : "   ");
//                // TODO: удалить, это для отладки
//                Integer j = reverseMap.get(i);
//                buffer.append(action.get(j).containsKey(s.toString()) ? action.get(j).get(s.toString()).toString() + " " : "   ");
//                //
            }
            buffer.append("\n");
        }
        System.out.println(buffer.toString());
    }

    public void printGoToMap() {
        StringBuilder buffer = new StringBuilder();
        List<Symbol> notTerminals = grammar.getDeclarations().stream().filter(Symbol::isNotTerminal).collect(Collectors.toList());
//        List<Symbol> notTerminals = new ArrayList<>(List.of(Symbol.createNotTerminal("E"), Symbol.createNotTerminal("T"), Symbol.createNotTerminal("F")));
        buffer.append("    ");
        for (Symbol s : notTerminals) {
            buffer.append(s.toString()).append(s.toString().length() == 2 ? " " : "  ");
        }
        buffer.append("\n");
        for (int i = 0; i < goTo.size(); i++) {
            buffer.append(i > 9 ? i : i + " ").append(": ");
            for (Symbol s: notTerminals) {
                buffer.append(goTo.get(i).containsKey(s.toString()) ?
                        (goTo.get(i).get(s.toString()) > 9 ? goTo.get(i).get(s.toString()) : goTo.get(i).get(s.toString()) + " ")
                                + " " : "   ");
            }
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
        List<ItemsSet> machine = items();
        FirstAndFollowGenerator firstAndFollowGenerator = new FirstAndFollowGenerator(grammar);
        if (ProdOrDebug.isDebug)
            System.out.println(printItems(machine));

        for (int i = 0; i < machine.size(); i++) {
            action.put(i, new HashMap<>());
            goTo.put(i, new HashMap<>());
            Set<ProductionWithItem> productions = machine.get(i).getSet();
            // Пункт 2.а
            List<ProductionWithItem> list = productions.stream().filter(production -> production.getSymbolAfterPoint().isTerminal()).collect(Collectors.toList());
            for (ProductionWithItem production : list) {
                Symbol symbolAfterPoint = production.getSymbolAfterPoint();
                TableElementType elementType = TableElementType.createShift(relations.get(i).get(symbolAfterPoint));
                if (action.get(i).containsKey(symbolAfterPoint.toString()))
                    throw new Error("Ошибка, грамматика не принадлежит классу SLR.");
                action.get(i).put(symbolAfterPoint.toString(), elementType);
            }

            // Пункт 2.б
            list = productions.stream().filter(production ->
                    production.itemAtTheEnd() &&
                            !production.getProduction().getLNotTerminal().equals(grammar.getStartSymbol()))
                    .collect(Collectors.toList());
            for (ProductionWithItem production : list) {
                Integer reduceProductionIndex = grammar.getProductionIndex(production.getProduction());
                Set<Symbol> follow = firstAndFollowGenerator.calcFollow(production.getProduction().getLNotTerminal());
                for (Symbol symbol : follow) {
                    if (action.get(i).containsKey(symbol.toString()))
                        throw new Error("Ошибка, грамматика не принадлежит классу SLR.");
                    action.get(i).put(symbol.toString(), TableElementType.createReduce(reduceProductionIndex));
                }
            }

            // Пункт 2.в
            List<ProductionWithItem> ps =
                    productions.stream().filter(p -> p.getProduction().getLNotTerminal().equals(grammar.getStartSymbol()) && p.itemAtTheEnd())
                    .collect(Collectors.toList());
            if (ps.size() > 0) {
                if (action.get(i).containsKey(Symbol.createEndTerminal().toString()))
                    throw new Error("Ошибка, грамматика не принадлежит классу SLR.");
                action.get(i).put(Symbol.createEndTerminal().toString(), TableElementType.createAccept());
            }

            List<Symbol> keySet = relations.get(i).keySet().stream().filter(Symbol::isNotTerminal).collect(Collectors.toList());
            for (Symbol s : keySet) {
                goTo.get(i).put(s.toString(), relations.get(i).get(s));
            }
        }
    }

    public List<ItemsSet> items() {
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

class ItemsSet {
    private Set<ProductionWithItem> set;

    public ItemsSet() {
        set = new HashSet<>();
    }

    public ItemsSet(Production production) {
        set = new HashSet<>(List.of(new ProductionWithItem(production)));
    }

    public ItemsSet(Set<ProductionWithItem> set) {
        this.set = set;
    }

    public ItemsSet(ItemsSet itemsSet) {
        this.set = new HashSet<>(itemsSet.getSet());
    }

    public Set<ProductionWithItem> getSet() {
        return set;
    }

    public void setSet(Set<ProductionWithItem> set) {
        this.set = set;
    }

    public void addProduction(ProductionWithItem production) {
        this.set.add(production);
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("\nСостояние автомата {\n");
        set.stream()
                .sorted(Comparator.comparing(production -> production.getProduction().getLNotTerminal().toString()))
                .forEach(production -> result.append("\t").append(production).append("\n"));
        result.append("}\n");
        return result.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemsSet that = (ItemsSet) o;
        return Objects.equals(set, that.set);
    }

    @Override
    public int hashCode() {
        return Objects.hash(set);
    }
}

class ProductionWithItem {
    private final Production production;
    private int item = 0;

    public ProductionWithItem(Production production) {
        this.production = production;
    }

    public ProductionWithItem(Production production, int item) {
        this.production = production;
        this.item = item;
    }

    public Production getProduction() {
        return production;
    }

    public Symbol getSymbolAfterPoint() {
        return itemAtTheEnd() ? Symbol.EPSILON : production.getRSymbols().get(item);
    }

    public ProductionWithItem incrementItem() {
        if (!itemAtTheEnd()) {
            return new ProductionWithItem(production, item + 1);
        }
        return this;
    }

    public boolean itemAtTheEnd() {
        return item == production.getRSymbols().size();
    }

    public boolean isEpsilonProduction() {
        return production.getRSymbols().size() != 0 && production.getRSymbols().get(0).isEpsilon();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductionWithItem that = (ProductionWithItem) o;
        return item == that.item && Objects.equals(production, that.production);
    }

    @Override
    public int hashCode() {
        return Objects.hash(production, item);
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(production.getLNotTerminal().toString());
        stringBuilder.append(" -> ");
        for (int i = 0; i < production.getRSymbols().size(); i++) {
            if (item == i) {
                stringBuilder.append(" .");
            }
            stringBuilder.append(" ");
            stringBuilder.append(production.getRSymbols().get(i).toString());
        }
        if (item == production.getRSymbols().size()) {
            stringBuilder.append(" .");
        }
        return stringBuilder.toString();
    }
}
