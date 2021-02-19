import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Node {
    private List<Node> children = new ArrayList<>();
    private String nonTerminalName;
    // Инициализируются только у терминалов
    private String lexemeName;
    private String value;

    public Node(String lexemeName, String value) {
        this.lexemeName = lexemeName;
        this.value = value;
    }

    public Node(String nonTerminalName) {
        this.nonTerminalName = nonTerminalName;
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

    public String getLexemeName() {
        return lexemeName;
    }

    public String getValue() {
        return value;
    }

    public String getNonTerminalName() {
        return nonTerminalName;
    }
}
