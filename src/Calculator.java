public class Calculator {
    public static double calculate(Node root) {
        return parseE(root);
    }

    private static double parseE(Node node) {
        if (node.getChildren().size() == 3) {
            double leftOperand = parseE(node.getChildren().get(2));
            double rightOperand = parseT(node.getChildren().get(0));
            String plusName = DomainTagCalculator.OpAdd.name();
            String minusName = DomainTagCalculator.OpSub.name();

            if (plusName.equals(node.getChildren().get(1).getTerminal().getDomainTag().name())) {
                return leftOperand + rightOperand;
            } else if (minusName.equals(node.getChildren().get(1).getTerminal().getDomainTag().name())) {
                return leftOperand - rightOperand;
            }

            throw new Error("Ошибка работы калькулятора!");
        } else {
            return parseT(node.getChildren().get(0));
        }
    }

    private static double parseT(Node node) {
        if (node.getChildren().size() == 3) {
            double leftOperand = parseT(node.getChildren().get(2));
            double rightOperand = parseF(node.getChildren().get(0));
            String mulName = DomainTagCalculator.OpMul.name();
            String divName = DomainTagCalculator.OpDiv.name();

            if (mulName.equals(node.getChildren().get(1).getTerminal().getDomainTag().name())) {
                return leftOperand * rightOperand;
            } else if (divName.equals(node.getChildren().get(1).getTerminal().getDomainTag().name())) {
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
            return Integer.parseInt(node.getChildren().get(0).getTerminal().getValue());
        }
    }
}
