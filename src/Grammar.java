import java.util.*;
import java.util.stream.Collectors;

public class Grammar {
    private Symbol startSymbol;
    private List<Production> productions = new ArrayList<>();
    private Map<Symbol, List<Production>> notTerminalToProductions = new HashMap<>();
    private Set<Symbol> declarations;

    public Grammar() {
    }

    public Grammar(Grammar grammar) {
        this.startSymbol = grammar.startSymbol;
        this.productions.addAll(grammar.getProductions());
        this.notTerminalToProductions = new HashMap<>(grammar.notTerminalToProductions);
        this.declarations = grammar.declarations;
    }

    public Production getStartProduction() {
        return getProductionsByNotTerminal(getStartSymbol()).get(0);
    }

    public Integer getProductionIndex(Production production) {
        return productions.indexOf(production);
    }

    public List<Production> getProductionsByNotTerminal(Symbol notTerminal) {
        if (notTerminalToProductions.get(notTerminal) != null) {
            return notTerminalToProductions.get(notTerminal);
        }

        List<Production> result = productions.stream().filter(production -> production.getLNotTerminal().equals(notTerminal)).collect(Collectors.toList());
        notTerminalToProductions.put(notTerminal, result);
        return result;
    }


    public void calculateDeclarations() {
        declarations = new HashSet<>();
        for (Production production : productions) {
            declarations.add(production.getLNotTerminal());
            declarations.addAll(production.getRSymbols().stream().filter(symbol -> !symbol.isEpsilon()).collect(Collectors.toList()));
        }
    }

    public Set<Symbol> getDeclarations() {
        if (declarations == null) {
            calculateDeclarations();
        }
        return declarations;
    }

    public List<Production> getProductions() {
        return productions;
    }

    public void setProductions(List<Production> productions) {
        this.productions = productions;
    }

    public Symbol getStartSymbol() {
        return startSymbol;
    }

    public void setStartSymbol(Symbol startSymbol) {
        this.startSymbol = startSymbol;
    }

    public void addProduction(Production production) {
        productions.add(production);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Grammar grammar = (Grammar) o;
        return Objects.equals(startSymbol, grammar.startSymbol) && Objects.equals(productions, grammar.productions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(startSymbol, productions);
    }
}

class Production {
    private Symbol lNotTerminal;
    private List<Symbol> rSymbols;

    public Production(Symbol lNotTerminal, List<Symbol> rSymbols) {
        this.lNotTerminal = lNotTerminal;
        this.rSymbols = rSymbols;
    }

    public List<Symbol> getRSymbols() {
        return rSymbols;
    }

    public Symbol getLNotTerminal() {
        return lNotTerminal;
    }

    @Override
    public String toString() {
        return lNotTerminal.toPrettyString() + " -> " + rSymbols.stream().map(Symbol::toPrettyString).collect(Collectors.joining(" "));
    }

    public boolean isEpsilonProduction() {
        return rSymbols.size() == 1 && rSymbols.get(0).isEpsilon();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Production that = (Production) o;
        return Objects.equals(lNotTerminal, that.lNotTerminal) && Objects.equals(rSymbols, that.rSymbols);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lNotTerminal, rSymbols);
    }
}
