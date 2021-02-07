import java.util.*;
import java.util.stream.Collectors;

public class FirstAndFollowGenerator {
    private final Map<Symbol, Set<Symbol>> mapSymbolToFirstSet = new HashMap<>();
    private final Map<Symbol, Set<Symbol>> mapSymbolToFollowSet = new HashMap<>();
    private final Grammar grammar;
    private Symbol followRootOfRecursion;

    public static void main(String[] args) {
        // Инициализация грамматики
        Grammar grammar1 = new Grammar();


        Symbol S = Symbol.createNotTerminal("S");
        Symbol E = Symbol.createNotTerminal("E");
        Symbol E1 = Symbol.createNotTerminal("E1");
        Symbol N = Symbol.createNotTerminal("N");
        grammar1.setProductions(List.of(
                new Production(S, List.of(E)),
                new Production(E, List.of(N, E1)),
                new Production(E1, List.of(Symbol.createTerminal(DomainTagCalculator.OpAdd.name()), E)),
                new Production(N, List.of(Symbol.createTerminal(DomainTagCalculator.IntegerVal.name()))),
                new Production(E1, List.of(Symbol.EPSILON))
        ));
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
        grammar1.setStartSymbol(S);
        grammar1.calculateDeclarations();

        FirstAndFollowGenerator generator = new FirstAndFollowGenerator(grammar1);
        System.out.println("FIRST");
        System.out.println("S: " + generator.calcFirst(S));
        System.out.println("E: " + generator.calcFirst(E));
        System.out.println("E1: " + generator.calcFirst(E1));
        System.out.println("N: " + generator.calcFirst(N));
//        System.out.println("E: " + generator.calcFirst(E));
//        System.out.println("E1: " + generator.calcFirst(E1));
//        System.out.println("T: " + generator.calcFirst(T));
//        System.out.println("T1: " + generator.calcFirst(T1));
//        System.out.println("F: " + generator.calcFirst(F));
        System.out.println("\nFOLLOW");
        System.out.println("S: " + generator.calcFollow(S));
        System.out.println("E: " + generator.calcFollow(E));
        System.out.println("E1: " + generator.calcFollow(E1));
        System.out.println("N: " + generator.calcFollow(N));
//        System.out.println("E: " + generator.calcFollow(E));
//        System.out.println("E1: " + generator.calcFollow(E1));
//        System.out.println("T: " + generator.calcFollow(T));
//        System.out.println("T1: " + generator.calcFollow(T1));
//        System.out.println("F: " + generator.calcFollow(F));
    }

    public FirstAndFollowGenerator(Grammar grammar) {
        this.grammar = grammar;
    }

    public Set<Symbol> calcFirst(Symbol X) {
        if (mapSymbolToFirstSet.containsKey(X)) {
            return mapSymbolToFirstSet.get(X);
        }

        if (X.isTerminal() || X.isEpsilon()) {
            mapSymbolToFirstSet.put(X, new HashSet<>(Collections.singletonList(X)));
            return mapSymbolToFirstSet.get(X);
        }

        Set<Symbol> firstResult = new HashSet<>();
        for (Production production : grammar.getProductionsByNotTerminal(X)) {
            if (production.isEpsilonProduction()) {
                firstResult.add(Symbol.EPSILON);
            }

            for (int i = 0; i < production.getRSymbols().size(); i++) {
                Symbol symbol = production.getRSymbols().get(i);
                Set<Symbol> first = calcFirst(symbol);
                Set<Symbol> firstWithoutEpsilon = first.stream().filter(symbol1 -> !symbol1.isEpsilon()).collect(Collectors.toSet());
                firstResult.addAll(firstWithoutEpsilon);

                if (i + 1 == production.getRSymbols().size() && first.contains(Symbol.EPSILON)) {
                    firstResult.add(Symbol.EPSILON);
                }

                if (!first.contains(Symbol.EPSILON)) {
                    break;
                }
            }
        }
        mapSymbolToFirstSet.put(X, firstResult);
        return firstResult;
    }

    public Set<Symbol> calcFirstForSymbolList(List<Symbol> symbols) {
        Set<Symbol> firstResult = new HashSet<>();
        for (int i = 0; i < symbols.size(); i++) {
            Symbol symbol = symbols.get(i);
            Set<Symbol> first = calcFirst(symbol);
            Set<Symbol> firstWithoutEpsilon = first.stream().filter(symbol1 -> !symbol1.isEpsilon()).collect(Collectors.toSet());
            firstResult.addAll(firstWithoutEpsilon);

            if (i + 1 == symbols.size() && first.contains(Symbol.EPSILON)) {
                firstResult.add(Symbol.EPSILON);
            }

            if (!first.contains(Symbol.EPSILON)) {
                break;
            }
        }
        return firstResult;
    }

    public Set<Symbol> calcFollow(Symbol X) {
        return calcFollow(X, null);
    }

    private Set<Symbol> calcFollow(Symbol X, Set<Symbol> visitedInRecursion) {
        if (mapSymbolToFollowSet.containsKey(X)) {
            return mapSymbolToFollowSet.get(X);
        }

        if (visitedInRecursion != null) {
            if (visitedInRecursion.contains(X)) {
                return new HashSet<>();
            }

            visitedInRecursion.add(X);
        }

        Set<Symbol> resultFollow = new HashSet<>();

        if (grammar.getStartSymbol().equals(X)) {
            resultFollow.add(Symbol.createEndTerminal());
        }

        List<Production> productions = getProductionsWhereThereIsSymbolOnTheRightSide(X);
        for (Production production : productions) {
            int xSymbolIndex = production.getRSymbols().indexOf(X);

            Set<Symbol> firstForRightTail = null;
            if (xSymbolIndex + 1 != production.getRSymbols().size()) {
                firstForRightTail = calcFirstForSymbolList(production.getRSymbols().subList(xSymbolIndex + 1, production.getRSymbols().size()));
                resultFollow.addAll(firstForRightTail.stream().filter(s -> !s.equals(Symbol.EPSILON)).collect(Collectors.toSet()));
            }

            if ((firstForRightTail == null || firstForRightTail.contains(Symbol.EPSILON))) {
                if (visitedInRecursion == null) {
                    visitedInRecursion = new HashSet<>(List.of(X));
                    followRootOfRecursion = X;
                }

                if (!production.getLNotTerminal().equals(followRootOfRecursion)) {
                    resultFollow.addAll(calcFollow(production.getLNotTerminal(), visitedInRecursion));
                }

                if (followRootOfRecursion.equals(X)) {
                    visitedInRecursion = null;
                    followRootOfRecursion = null;
                }
            }
        }

        if (followRootOfRecursion == null) {
            mapSymbolToFollowSet.put(X, resultFollow);
        }
        return resultFollow;
    }

    private List<Production> getProductionsWhereThereIsSymbolOnTheRightSide(Symbol X) {
        return grammar.getProductions().stream().filter(production -> production.getRSymbols().contains(X)).collect(Collectors.toList());
    }
}
