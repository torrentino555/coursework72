class TableElementType {
    private static final String SHIFT_TYPE = "shift";
    private static final String REDUCE_TYPE = "reduce";
    private static final String ACCEPT_TYPE = "accept";

    private String type;
    private Integer state;

    public TableElementType(String type) {
        this.type = type;
    }

    private TableElementType(String type, Integer state) {
        this.type = type;
        this.state = state;
    }

    public static TableElementType createShift(Integer state) {
        return new TableElementType(SHIFT_TYPE, state);
    }

    public static TableElementType createReduce(Integer state) {
        return new TableElementType(REDUCE_TYPE, state);
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

    @Override
    public String toString() {
        String r = state == null ? "?" : state.toString();
//        // TODO: удалить, это для отладки
//        List<Integer> map = new ArrayList<>(List.of(0, 4, 5, 2, 1, 3, 8, 7, 6, 11, 10, 9));
//        String r = state == null ? "?" : (type.equals(REDUCE_TYPE) ? state : map.get(state)).toString();
//        //
        return switch (type) {
            case SHIFT_TYPE -> "s" + r;
            case REDUCE_TYPE -> "r" + r;
            case ACCEPT_TYPE -> "ac";
            default -> "";
        };
    }
}
