package RestController_calculator;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


//REST API
@RestController
class SummatorController {
    
    @GetMapping("/make")
    public String arithmeticExpression(String expression) {
        System.out.println("Полученно выражение: " + expression);
        return fun(expression);
    }

    public static String fun(String expression){
        int result = 0;
        int lastOpen = expression.lastIndexOf('(');
        int firstClose = expression.indexOf(')');
        String newExpression = "";
        while (lastOpen > firstClose) {
            String expression1 = expression.substring(expression.indexOf('('), lastOpen - 1);
            String expression2 = expression.substring(lastOpen);
            System.out.println(expression1);
            System.out.println(expression2);
            char operation = expression.charAt(lastOpen - 1);
            lastOpen = expression1.lastIndexOf('(');
            firstClose = expression1.indexOf(')');
            if (lastOpen < firstClose) {
                newExpression = calculateExpressionInEnclosure(expression1) + operation + calculateExpressionInEnclosure(expression2) + newExpression;
            } else {
                newExpression = operation + calculateExpressionInEnclosure(expression2) + newExpression;
            }
            System.out.println("new: " + newExpression);
        }
        if (newExpression.equals("")) newExpression = calculateExpressionInEnclosure(expression);
        result = calculateExpression(newExpression);
        return result + "";
    }

    public static String calculateExpressionInEnclosure(String expression) {
        int lastOpen = expression.lastIndexOf('(');
        int firstClose = expression.indexOf(')');
        if ((lastOpen == -1) != (firstClose == -1)) throw new IllegalArgumentException();
        if ((lastOpen == -1) && (firstClose == -1)) return calculateExpression(expression) + "";
        String newExpression = expression.substring(lastOpen + 1, firstClose);
        System.out.println(newExpression);

        int intermediateResult = calculateExpression(newExpression);

        String newExpression2 = expression.substring(0, lastOpen) + intermediateResult + expression.substring(firstClose + 1);
        System.out.println(newExpression2);

        return calculateExpressionInEnclosure(newExpression2);
    }

    public static int calculateExpression(String expression) {
        List<String> numbers = new ArrayList<>(List.of(expression.split("[+\\-*/]")));
        List<String> operation = new ArrayList<>(List.of(expression.split("[0-9]")));
        while (operation.remove("")) {}
        while (numbers.remove("")) {}
        detectionNegativeNumber(numbers, operation);
        if (operation.size() == 0) {
            operation.add(0, "null");
            return Integer.parseInt(numbers.get(0));
        }
        System.out.println(numbers); System.out.println(operation);
        for (int i = 0; i < operation.size(); i++) {
            boolean flag = false;
            if (calculateAndUpdateNumbersAndOperation(numbers, operation, "*", i)) {
                if (i >= operation.size()) i--;
                flag = true;
            }
            if (calculateAndUpdateNumbersAndOperation(numbers, operation, "/", i)) flag = true;
            if (flag && !operation.get(0).equals("null")) i--;
        }
        for (int i = 0; i < operation.size(); i++) {
            boolean flag = false;
            if (calculateAndUpdateNumbersAndOperation(numbers, operation, "+", i)) {
                if (i >= operation.size()) i--;
                flag = true;
            }
            if (calculateAndUpdateNumbersAndOperation(numbers, operation, "-", i)) flag = true;
            if (flag && !operation.get(0).equals("null")) i--;
        }

        System.out.println("numbers: " + numbers);
        System.out.println("operation: " + operation);
        return Integer.parseInt(numbers.get(0));
    }

    public static void detectionNegativeNumber(List<String> numbers, List<String> operation){
        for (int i = 0; i < operation.size(); i++) {
            if (operation.get(i).equals("*-")) {
                operation.set(i, "*");
                numbers.set(i + 1, "-" + numbers.get(i + 1));
            }
            if (operation.get(i).equals("/-")) {
                operation.set(i, "/");
                numbers.set(i + 1, "-" + numbers.get(i + 1));
            }
            if (operation.get(i).equals("--")) {
                operation.set(i, "-");
                numbers.set(i + 1, "-" + numbers.get(i + 1));
            }
            if (operation.get(i).equals("+-")) {
                operation.set(i, "+");
                numbers.set(i + 1, "-" + numbers.get(i + 1));
            }
            if (operation.get(0).equals("-") && (operation.size() >= numbers.size())) {
                operation.remove(0);
                numbers.set(0, "-" + numbers.get(0));
                i--;
            }
        }
    }

    public static boolean calculateAndUpdateNumbersAndOperation(List<String> numbers, List<String> operations, String operation, int currentIndex) {
        if (operations.get(currentIndex).equals(operation)) {
            int result = calculationOperation(Integer.parseInt(numbers.get(currentIndex)), Integer.parseInt(numbers.get(currentIndex + 1)), operation);
            numbers.remove(currentIndex + 1);
            numbers.remove(currentIndex);
            numbers.add(currentIndex, String.valueOf(result));
            operations.remove(currentIndex);
            if (operations.size() == 0) {
                operations.add(0, "null");
            }
            System.out.println("numbers: " + numbers);
            System.out.println("operation: " + operations);
            return true;
        }
        return false;
    }

    public static int calculationOperation(int a, int b, String operation) {
        if (operation.equals("*")) return a * b;
        if (operation.equals("/")) return a / b;
        if (operation.equals("-")) return a - b;
        if (operation.equals("+")) return a + b;
        return 0;
    }

}
