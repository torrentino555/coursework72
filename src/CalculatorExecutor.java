public class CalculatorExecutor {
    public static double calculate(Node root) {
        return parseE(root);
    }

    private static double parseE(Node node) {
        if (node.getChildren().size() == 3) {
            double leftOperand = parseE(node.getChildren().get(0));
            double rightOperand = parseT(node.getChildren().get(2));

            if ("+".equals(node.getChildren().get(1).getLexemeName())) {
                return leftOperand + rightOperand;
            } else if ("-".equals(node.getChildren().get(1).getLexemeName())) {
                return leftOperand - rightOperand;
            }

            throw new Error("Ошибка работы калькулятора!");
        } else {
            return parseT(node.getChildren().get(0));
        }
    }

    private static double parseT(Node node) {
        if (node.getChildren().size() == 3) {
            double leftOperand = parseT(node.getChildren().get(0));
            double rightOperand = parseF(node.getChildren().get(2));

            if ("*".equals(node.getChildren().get(1).getLexemeName())) {
                return leftOperand * rightOperand;
            } else if ("/".equals(node.getChildren().get(1).getLexemeName())) {
                return leftOperand / rightOperand;
            }

            throw new Error("Ошибка работы калькулятора!");
        } else {
            return parseF(node.getChildren().get(0));
        }
    }

    private static double parseF(Node node) {
        if (node.getChildren().size() == 3) {
            return parseE(node.getChildren().get(1));
        } else {
            return Integer.parseInt(node.getChildren().get(0).getValue());
        }
    }
}
