import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Production implements Serializable {
    private final Symbol lNotTerminal;
    private final List<Symbol> rSymbols;

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