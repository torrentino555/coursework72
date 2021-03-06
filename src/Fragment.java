public class Fragment {
    private final Position start;
    private final Position follow;

    public Fragment(Position start, Position follow) {
        this.start = start;
        this.follow = follow;
    }

    public String getFragmentValue() {
        return start.getTail().substring(0, follow.getIndex() - start.getIndex());
    }

    @Override
    public String toString() {
        return start.toString() + "-" + follow.toString();
    }
}
