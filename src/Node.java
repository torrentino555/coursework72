import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Node {
    private List<Node> children = new ArrayList<>();
    private String nonTerminalName;
    // Инициализируются только у терминалов
    private String tagName;
    private String value;

    public Node() {
    }

    public Node(String tagName, String value) {
        this.tagName = tagName;
        this.value = value;
    }

    public Node(String nonTerminalName) {
        this.nonTerminalName = nonTerminalName;
    }

    public boolean isTerminal() {
        return tagName.equals(DomainTagGrammar.TerminalVal.name());
    }

    public void addChildren(Node node) {
        children.add(node);
    }

    public List<Node> getChildren() {
        return children;
    }

    public Node getChild(int i) {
        return children.get(i);
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

    public String getNonTerminalName() {
        return nonTerminalName;
    }

    public Node setNonTerminalName(String nonTerminalName) {
        this.nonTerminalName = nonTerminalName;
        return this;
    }
}
