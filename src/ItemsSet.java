import java.util.*;

public class ItemsSet {
    private Set<ProductionWithItem> set;

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