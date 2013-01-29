package com.dp.bigdata.taurus.core.parser;

import java.text.ParseException;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.dp.bigdata.taurus.core.AttemptStatusCheck;

/**
 * Parse the dependency expression into post-suffix expression. Post-suffix expression is represented by a LinkedList.
 * 
 * @author damon.zhu
 * @see ParserNode
 */
public class DependencyParser {

    public static final String SPLIT_PATTERN = "\\[([A-Za-z0-9_\\-\\(\\)\\.]+)\\]\\[(\\d+)\\]\\[(\\d+)\\]";
    private static final String SPLIT_PART = "(\\[[A-Za-z0-9_\\-\\(\\)\\.]+\\]\\[\\d+\\]\\[\\d+\\])";
    public static final String DEPENDENCY_PATTERN = SPLIT_PART + "(" + "([&|\\|])" + SPLIT_PART + ")*";
    private static final Pattern SPLIT_CHECKER = Pattern.compile(SPLIT_PATTERN);
    private static final Pattern DEPENDENCY_CHECKER = Pattern.compile(DEPENDENCY_PATTERN);

    /**
     * Compute the expression.
     * 
     * @param dependencyExpr
     * @param checker
     * @return
     * @throws ILLegalDependencyExprException
     */
    public boolean isDepdencySatisfied(String dependencyExpr, AttemptStatusCheck checker) throws ParseException {
        Stack<Boolean> s_value = new Stack<Boolean>();
        ParserNode current = postExpression(dependencyExpr);

        while (current != null) {
            if (!current.isOperator()) {
                Operation op = current.getOperation();
                s_value.push(checker.isDone(op));
            } else {
                boolean first = s_value.pop();
                boolean second = s_value.pop();
                if (current.getOperator().getOperator() == Operator.AND) {
                    s_value.push(first && second);
                } else {
                    s_value.push(first || second);
                }
            }
            current = current.getNext();
        }

        return s_value.pop();
    }

    /**
     * Convert dependencyExpression into prefix Expression.
     * 
     * @param dependencyExpr
     * @return
     * @throws ParseException
     */
    private static ParserNode prefixExpression(String dependencyExpr) throws ParseException {

        int length = dependencyExpr.length();
        int start = 0;
        ParserNode head = null;
        ParserNode current = null;
        for (int position = 0; position < length; position++) {
            if (dependencyExpr.charAt(position) == Operator.AND || dependencyExpr.charAt(position) == Operator.OR) {
                String split = dependencyExpr.substring(start, position);
                Operation op = getOperation(split);
                ParserNode next = new ParserNode(op);
                if (head == null) {
                    head = next;
                    current = head;
                } else {
                    current.setNext(next);
                }
                Operator operator = new Operator(dependencyExpr.charAt(position));
                ParserNode nextnext = new ParserNode(operator);
                next.setNext(nextnext);
                current = nextnext;
                start = position + 1;
            }
        }

        String lastSplit = dependencyExpr.substring(start, length);
        Operation op = getOperation(lastSplit);
        if (current == null) {
            head = new ParserNode(op);
        } else {
            current.setNext(new ParserNode(op));
        }

        return head;
    }

    private static Operation getOperation(String split) throws ParseException {
        Matcher matcher = SPLIT_CHECKER.matcher(split);
        if (matcher.matches()) {
            //System.out.println(matcher.groupCount());
            String name = matcher.group(1);
            int number = Integer.parseInt(matcher.group(2));
            int value = Integer.parseInt(matcher.group(3));
            return new Operation(name, number, value);
        } else {
            throw new ParseException("illegal dependency expression", 0);
        }
    }

    /**
     * Convert dependency Expression into post-fix Expression.
     * 
     * @param dependencyExpr
     * @return ParserNode head
     * @throws ILLegalDependencyExprException
     */
    public static ParserNode postExpression(String dependencyExpr) throws ParseException {
        if (!isValidateExpression(dependencyExpr)) {
            throw new ParseException("illegal dependency expression", 0);
        }

        ParserNode head = null;
        ParserNode tail = null;
        ParserNode current = prefixExpression(dependencyExpr);
        // Stack<Operation> s_operation = new Stack<Operation>();
        Stack<Operator> s_operator = new Stack<Operator>();

        while (current != null) {
            if (current.isOperator()) {
                Operator operator = current.getOperator();
                if (s_operator.isEmpty()) {
                    s_operator.push(operator);
                } else {
                    while (!s_operator.isEmpty()) {
                        if (operator.getOperator() == Operator.AND && s_operator.peek().getOperator() == Operator.OR) {
                            s_operator.push(operator);
                            break;
                        } else {
                            Operator tmpOperator = s_operator.pop();
                            ParserNode next = new ParserNode(tmpOperator);
                            tail.setNext(next);
                            tail = next;
                            if (s_operator.isEmpty()) {
                                s_operator.push(operator);
                                break;
                            }
                        }
                    }
                }
            } else {
                if (head == null) {
                    head = new ParserNode(current.getOperation());
                    tail = head;
                } else {
                    ParserNode next = new ParserNode(current.getOperation());
                    tail.setNext(next);
                    tail = next;
                }
            }
            current = current.getNext();
        }

        while (!s_operator.isEmpty()) {
            ParserNode next = new ParserNode(s_operator.pop());
            tail.setNext(next);
            tail = next;
        }

        return head;
    }

    /**
     * check if dependencyExpr matches DEPENDENCY_PATTERN
     * 
     * @param dependcyExpr
     * @return true if dependencyExpr marches DEPENDENCY_PATTERN, otherwise return false
     */
    public static boolean isValidateExpression(String dependencyExpr) {
        return DEPENDENCY_CHECKER.matcher(dependencyExpr).matches();
    }

    public static void main(String args[]) {
        /*
         * String input1 = "[wordcount][10][0]&[LogSort][10][0]|[HipSort][10][0]"; String pattern =
         * "(\\[\\w+\\]\\[\\d+\\]\\[\\d+\\])"; String part = "([&|\\|])" + pattern; String depedencyPattern = pattern + "(" + part +
         * ")*"; System.out.println(depedencyPattern); Pattern p1 = Pattern.compile(depedencyPattern); Matcher m =
         * p1.matcher(input1); System.out.println(m.matches()); System.out.println(m.groupCount()); for(int i = 0; i <=
         * m.groupCount(); i++){ System.out.println(m.group(i)); }
         */

        String input = "[wordcount][10][0]";
        Pattern p = Pattern.compile(SPLIT_PATTERN);
        Matcher matcher = p.matcher(input);
        System.out.println(matcher.matches());

        System.out.println(matcher.groupCount());

        System.out.println(matcher.group(1));
        System.out.println(matcher.group(2));
        System.out.println(matcher.group(3));

    }

}
