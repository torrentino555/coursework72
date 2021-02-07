import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Node {
    private List<Node> children = new ArrayList<>();
    private String notTerminalName;
    private Token terminal;

    public Node() {
    }

    public Node(Token terminal) {
        this.terminal = terminal;
    }

    public Node(String notTerminalName) {
        this.notTerminalName = notTerminalName;
    }

    public void addChildren(Node node) {
        children.add(node);
    }

    public List<Node> getChildren() {
        return children;
    }

    public void reverseChildren() {
        Collections.reverse(this.children);
    }

    public Node setChildren(List<Node> children) {
        this.children = children;
        return this;
    }

    public String getNotTerminalName() {
        return notTerminalName;
    }

    public Node setNotTerminalName(String notTerminalName) {
        this.notTerminalName = notTerminalName;
        return this;
    }

    public Token getTerminal() {
        return terminal;
    }

    public Node setTerminal(Token terminal) {
        this.terminal = terminal;
        return this;
    }
}
