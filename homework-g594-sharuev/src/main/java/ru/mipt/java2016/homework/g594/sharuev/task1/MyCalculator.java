package ru.mipt.java2016.homework.g594.sharuev.task1;

import ru.mipt.java2016.homework.base.task1.ParsingException;

import java.io.IOException;
import java.io.StringReader;
import java.util.Stack;

public class MyCalculator implements ru.mipt.java2016.homework.base.task1.Calculator {

  public double calculate(String expression) throws ParsingException {
    if (expression == null) {
      throw new ParsingException("Null expression");
    } else if (expression.equals("")) {
      throw new ParsingException("Empty string");
    }
    StringBuilder ss = new StringBuilder();
    ToReversePolish(new StringReader(expression), ss);
    if (ss.toString().equals("")) {
      throw new ParsingException("String with only whitespaces");
    }

    return CalculateReversePolish(new StringReader(ss.toString()));
  }

  private int readNumber(StringReader is, StringBuilder os, char c) throws IOException {
    int read;
    do {
      os.append(c);
      read = is.read();
      if (read == -1) {
        break;
      } else {
        c = (char) read;
      }
    } while (Character.isDigit(c) || c == '.');
    if (read != -1) {
      read = c;
    }
    return read;
  }

  private boolean ToReversePolish(StringReader is, StringBuilder os) throws ParsingException {
    Stack<Character> oper = new Stack<Character>();
    char c;
    int unary_flag = 1; //If 1, then - or + must be unary. If 0, then binary.
    int read = -1; // Value that came in. If we want value to be passed to the next iteration, we must store it here.
    boolean lastWasUnary = false;
    try {
      while (true) {
        if (read == -1) {
          read = is.read();
          if (read == -1) {
            break;
          }
        }
        c = (char) read;
        read = -1;

        // If read digit, read all the number (including decimal delimiter).
        // Last read character is not a digit, so process it as a character.
        if (Character.isDigit(c)) {
          read = readNumber(is, os, c);
          os.append(' ');
          if (lastWasUnary) {
            lastWasUnary = false;
          }
        } else if ((c == '-' || c == '+') && unary_flag == 1) {
          // Unary + or - in the beginning, after ( or after operator.
          lastWasUnary = true;
          oper.push('m');
          read = -1;
        } else if (c == '(') {
          oper.push('(');
          unary_flag = 2; // Will be 1 at the beginning of the next iteration.
          lastWasUnary = false;
        } else if (c == ')') {
          if (oper.empty()) {
            throw new ParsingException("Closing bracket without opening one");
          }
          while (oper.peek() != '(') {
            os.append(oper.pop());
            if (oper.empty()) {
              throw new ParsingException("Closing bracket without opening one");
            }
          }
          oper.pop(); // Remove ( from stack.
          lastWasUnary = false;
        } else if (Operators.isOperatorChar(c)) {
          if (unary_flag == 1) {
            throw new ParsingException(String.format("Missing operand for %c", c));
          }
          if (lastWasUnary == false) {
            while (!oper.empty() && oper.peek() != '('
                    && ((Operators.associativity(c) == Operators.Associativity.left) ?
                    (Operators.priority(c) <= Operators.priority(oper.peek())) :
                    (Operators.priority(c) < Operators.priority(oper.peek())))) {
              os.append(oper.pop());
            }
          } else {
            if (!(c == '+' || c == '-')) {
              lastWasUnary = false;
            } else {
              throw new ParsingException("Two unary operators in a row");
            }
          }
          oper.push(c);

          unary_flag = 2;
        } else if (Character.isWhitespace(c)) {
          continue;
        } else {
          throw new ParsingException(String.format("Unknown character %c", c));
        }
        if (unary_flag > 0) {
          --unary_flag;
        }
      }
      while (!oper.empty()) {
        if (oper.peek() == '(') {
          throw new ParsingException("No closing bracket");
        }
        os.append(oper.pop());
      }
    } catch (IOException e) {
      throw new ParsingException(String.format("Some weird IO error: %s", e.getMessage()));
    }
    // System.out.println(os.toString());
    return true;
  }

  private double CalculateReversePolish(StringReader is) throws ParsingException {
    StringBuilder sb = new StringBuilder();
    Stack<Double> st = new Stack<Double>();
    char c;
    int read;
    try {
      while (true) {
        read = is.read();
        if (read == -1) {
          break;
        }
        c = (char) read;

        if (Character.isDigit(c)) {
          do {
            sb.append(c);
            c = (char) is.read();
          } while (Character.isDigit(c) || c == '.');
          // Catching double decimal delimiters, for example.
          try {
            st.push(Double.parseDouble(sb.toString()));
          } catch (NumberFormatException nfe) {
            throw new ParsingException(String.format("Wrong decimal literal: %s", sb.toString()));
          }
          sb.setLength(0);
        } else if (c == 'm') {
          double a1 = st.pop();
          st.push(-a1);
        } else {
          double a2 = st.pop();
          double a1 = st.pop();
          st.push(Operators.evaluateBinary(c, a2, a1));
        }
      }
    } catch (IOException e) {
      throw new ParsingException(String.format("Some weird IO error: %s", e.getMessage()));
    }
    return st.pop();
  }

  private static class Operators {
    private static boolean isOperatorChar(char c) {
      switch (c) {
        case '+':
        case '-':
        case '*':
        case '/':
        case '^':
          return true;
        default:
          return false;
      }
    }

    private static Associativity associativity(char c) throws ParsingException {
      switch (c) {
        case '+':
        case '*':
        case '/':
        case '-':
          return Associativity.left;
        case '^':
          return Associativity.right;
        case 'm':
          return Associativity.left;
        default:
          throw new ParsingException(String.format("Unknown operator %c", c));
      }
    }

    private static int priority(char c) throws ParsingException {
      switch (c) {
        case '(':
        case ')':
          return 0;
        case '+':
        case '-':
          return 1;
        case '*':
        case '/':
          return 2;
        case '^':
          return 3;
        case 'm':
          return 4;
        default:
          throw new ParsingException(String.format("Unknown operator %c", c));
      }
    }

    static private double evaluateBinary(char c, double a2, double a1) throws ParsingException {
      switch (c) {
        case '+':
          return a1 + a2;
        case '-':
          return a1 - a2;
        case '*':
          return a1 * a2;
        case '/':
          return a1 / a2;
        case '^':
          return Math.pow(a1, a2);
        default:
          throw new ParsingException("Unknown operator");
      }
    }

    private enum Associativity {
      left, right
    }
  }
}
