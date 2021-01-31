import java.util.*;
import java.util.stream.Collectors;

public class GenerateLRMachine {
    private Grammar grammar;
    private List<Map<Symbol, Integer>> relations = new ArrayList<>();
    private Map<Integer, Map<String, TableElementType>> action = new HashMap<>();
    private Map<Integer, Map<String, Integer>> goTo = new HashMap<>();

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
        generateLRMachine.printActionMap();
        generateLRMachine.printGoToMap();
//        System.out.println(generateLRMachine.action);
//        System.out.println(generateLRMachine.goTo);
    }

    public void printActionMap() {
        StringBuilder buffer = new StringBuilder();
        List<Symbol> terminals = grammar.getDeclarations().stream().filter(Symbol::isTerminal).collect(Collectors.toList());
        terminals.add(Symbol.createEndTerminal());
        buffer.append("    ");
        for (Symbol s : terminals) {
            buffer.append(s.getValue()).append("  ");
        }
        buffer.append("\n");
        for (int i = 0; i < action.size(); i++) {
            buffer.append(i > 9 ? i : i + " ").append(": ");
            for (Symbol s: terminals) {
                buffer.append(action.get(i).containsKey(s.getValue()) ? action.get(i).get(s.getValue()).toString() + " " : "   ");
            }
            buffer.append("\n");
        }
        System.out.println(buffer.toString());
    }

    public void printGoToMap() {
        StringBuilder buffer = new StringBuilder();
        List<Symbol> notTerminals = grammar.getDeclarations().stream().filter(Symbol::isNotTerminal).collect(Collectors.toList());
        buffer.append("    ");
        for (Symbol s : notTerminals) {
            buffer.append(s.getValue()).append(s.getValue().length() == 2 ? " " : "  ");
        }
        buffer.append("\n");
        for (int i = 0; i < goTo.size(); i++) {
            buffer.append(i > 9 ? i : i + " ").append(": ");
            for (Symbol s: notTerminals) {
                buffer.append(goTo.get(i).containsKey(s.getValue()) ?
                        (goTo.get(i).get(s.getValue()) > 9 ? goTo.get(i).get(s.getValue()) : goTo.get(i).get(s.getValue()) + " ")
                                + " " : "   ");
            }
            buffer.append("\n");
        }
        System.out.println(buffer.toString());
    }

    public void generateActionAndGoTo() {
        List<SetOfItems> machine = items();
        FirstAndFollowGenerator firstAndFollowGenerator = new FirstAndFollowGenerator(grammar);
        if (ProdOrDebug.isDebug)
            System.out.println(machine);

        for (int i = 0; i < machine.size(); i++) {
            action.put(i, new HashMap<>());
            goTo.put(i, new HashMap<>());
            Set<ProductionWithItem> productions = machine.get(i).getSet();
            // Пункт 2.а
            List<ProductionWithItem> list = productions.stream().filter(production -> production.getSymbolAfterPoint().isTerminal()).collect(Collectors.toList());
            for (ProductionWithItem production : list) {
                Symbol symbolAfterPoint = production.getSymbolAfterPoint();
                TableElementType elementType = TableElementType.createShift(relations.get(i).get(symbolAfterPoint));
                if (action.get(i).containsKey(symbolAfterPoint.getValue()))
                    throw new Error("Ошибка, грамматика не принадлежит классу SLR.");
                action.get(i).put(symbolAfterPoint.getValue(), elementType);
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
                    if (action.get(i).containsKey(symbol.getValue()))
                        throw new Error("Ошибка, грамматика не принадлежит классу SLR.");
                    action.get(i).put(symbol.getValue(), TableElementType.createReduce(reduceProductionIndex));
                }
            }

            // Пункт 2.в
            List<ProductionWithItem> ps =
                    productions.stream().filter(p -> p.getProduction().getLNotTerminal().equals(grammar.getStartSymbol()) && p.itemAtTheEnd())
                    .collect(Collectors.toList());
            if (ps.size() > 0) {
                if (action.get(i).containsKey(Symbol.createEndTerminal().getValue()))
                    throw new Error("Ошибка, грамматика не принадлежит классу SLR.");
                action.get(i).put(Symbol.createEndTerminal().getValue(), TableElementType.createAccept());
            }

            List<Symbol> keySet = relations.get(i).keySet().stream().filter(Symbol::isNotTerminal).collect(Collectors.toList());
            for (Symbol s : keySet) {
                goTo.get(i).put(s.getValue(), relations.get(i).get(s));
            }
        }
    }

    public List<SetOfItems> items() {
        SetOfItems startSetOfItems = new SetOfItems();
        startSetOfItems.addProduction(new ProductionWithItem(grammar.getProductionsByNotTerminal(grammar.getStartSymbol()).get(0)));
        List<SetOfItems> C = new ArrayList<>(List.of(closure(startSetOfItems)));
        relations.add(new HashMap<>());

        Set<SetOfItems> alreadyDone = new HashSet<>();
        int oldSize, currentSize;
        do {
            oldSize = C.size();
            List<SetOfItems> newC = new ArrayList<>(C);
            for (int i = 0; i < C.size(); i++) {
                SetOfItems setOfItems = C.get(i);
                if (alreadyDone.contains(setOfItems)) {
                    continue;
                }

                for (Symbol s : grammar.getDeclarations()) {
                    SetOfItems nextI = goTo(setOfItems, s);
                    if (nextI.getSet().size() != 0 && !newC.contains(nextI)) {
                        relations.get(i).put(s, newC.size());
                        relations.add(new HashMap<>());
                        newC.add(nextI);
                    }
                }

                alreadyDone.add(setOfItems);
            }
            C = newC;
            currentSize = C.size();
        } while (oldSize < currentSize);
        return C;
    }

    public SetOfItems goTo(SetOfItems I, Symbol X) {
        Set<ProductionWithItem> newSet = I.getSet().stream().filter(production -> production.getSymbolAfterPoint().equals(X)).collect(Collectors.toSet());
        return closure(new SetOfItems(newSet.stream().map(ProductionWithItem::incrementItem).collect(Collectors.toSet())));
    }



    public SetOfItems closure(SetOfItems I) {
        SetOfItems setOfItems = new SetOfItems(I);
        Set<ProductionWithItem> alreadyDone = new HashSet<>();
        int oldSize, currentSize;
        do {
            oldSize = setOfItems.getSet().size();
            Set<ProductionWithItem> newSet = new HashSet<>(setOfItems.getSet());
            for (ProductionWithItem elementFromJ : setOfItems.getSet()) {
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
            setOfItems.setSet(newSet);
            currentSize = setOfItems.getSet().size();
        } while (oldSize < currentSize);
        return setOfItems;
    }
}

class SetOfItems {
    private Set<ProductionWithItem> set;

    public SetOfItems() {
        set = new HashSet<>();
    }

    public SetOfItems(Set<ProductionWithItem> set) {
        this.set = set;
    }

    public SetOfItems(SetOfItems setOfItems) {
        this.set = new HashSet<>(setOfItems.getSet());
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
        return "SetOfItems{" +
                "set=" + set +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SetOfItems that = (SetOfItems) o;
        return Objects.equals(set, that.set);
    }

    @Override
    public int hashCode() {
        return Objects.hash(set);
    }
}

class ProductionWithItem {
    private Production production;
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
        stringBuilder.append(production.getLNotTerminal().getValue());
        stringBuilder.append(" -> ");
        for (int i = 0; i < production.getRSymbols().size(); i++) {
            if (item == i) {
                stringBuilder.append(" .");
            }
            stringBuilder.append(" ");
            stringBuilder.append(production.getRSymbols().get(i).getValue());
        }
        if (item == production.getRSymbols().size()) {
            stringBuilder.append(" .");
        }
        return stringBuilder.toString();
    }
}
