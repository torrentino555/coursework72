import java.io.Serializable;
import java.util.Objects;

class TableElementType implements Serializable {
    private static final String SHIFT_TYPE = "shift";
    private static final String REDUCE_TYPE = "reduce";
    private static final String ACCEPT_TYPE = "accept";

    private final String type;
    private Integer state;
    private Production production;

    public TableElementType(String type) {
        this.type = type;
    }

    private TableElementType(String type, Integer state) {
        this.type = type;
        this.state = state;
    }

    public TableElementType(String type, Integer state, Production production) {
        this.type = type;
        this.state = state;
        this.production = production;
    }

    public static TableElementType createShift(Integer state) {
        return new TableElementType(SHIFT_TYPE, state);
    }

    public static TableElementType createReduce(Integer state, Production production) {
        return new TableElementType(REDUCE_TYPE, state, production);
    }

    public static TableElementType createAccept() {
        return new TableElementType(ACCEPT_TYPE);
    }

    public boolean isShift() {
        return type.equals(SHIFT_TYPE);
    }

    public boolean isReducer() {
        return type.equals(REDUCE_TYPE);
    }

    public boolean isAccept() {
        return type.equals(ACCEPT_TYPE);
    }

    public Integer getState() {
        return state;
    }

    public Production getProduction() {
        return production;
    }

    @Override
    public String toString() {
        String r = state == null ? "?" : state.toString();
        return switch (type) {
            case SHIFT_TYPE -> "s" + r;
            case REDUCE_TYPE -> "r" + r;
            case ACCEPT_TYPE -> "ac";
            default -> "";
        };
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TableElementType that = (TableElementType) o;
        return Objects.equals(type, that.type) && Objects.equals(state, that.state) && Objects.equals(production, that.production);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, state, production);
    }
}
