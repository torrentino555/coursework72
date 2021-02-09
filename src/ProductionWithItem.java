import java.util.Objects;

public class ProductionWithItem {
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
        stringBuilder.append(production.getLNotTerminal().toPrettyString());
        stringBuilder.append(" -> ");
        for (int i = 0; i < production.getRSymbols().size(); i++) {
            if (item == i) {
                stringBuilder.append(" .");
            }
            stringBuilder.append(" ");
            stringBuilder.append(production.getRSymbols().get(i).toPrettyString());
        }
        if (item == production.getRSymbols().size()) {
            stringBuilder.append(" .");
        }
        return stringBuilder.toString();
    }
}