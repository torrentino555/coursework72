import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Node {
    private List<Node> children = new ArrayList<>();
    private String notTerminalName;
    // Инициализируются только у терминалов
    private String tagName;
    private String value;

    public Node() {
    }

    public Node(String tagName, String value) {
        this.tagName = tagName;
        this.value = value;
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

    public String getTagName() {
        return tagName;
    }

    public String getValue() {
        return value;
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
}
