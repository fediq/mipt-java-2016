package ru.mipt.java2016.homework.g594.islamov.task1;

import ru.mipt.java2016.homework.base.task1.ParsingException;
import ru.mipt.java2016.homework.base.task1.Calculator;

/**
 * Created by Iskander Islamov on 11.10.2016.
 */

public class EvalCalculator implements Calculator {

    @Override
    public double calculate(String expression) throws ParsingException {
        if (expression == null) {
            throw new ParsingException("Null expression");
        }
        expression = expression.replaceAll("\\s", "");
        pair test_pair = new pair();
        test_pair.first = 0;
        test_pair.second = 0;
        if( IsValid( expression ) ) {
            test_pair = EvaluateExpression( expression, 0, expression.length() );
        }
        return test_pair.first;
    }

    private boolean IsNumber( char symbol ) {
        if( ( '0' <= symbol) && (symbol <= '9' ) ) {
            return true;
        } else{
            return false;
        }
    }

    private int IsWrong( char symbol, int bracket_balance ) throws ParsingException {
        if( bracket_balance < 0 ) {
            throw new ParsingException("Not a valid expression");
        }
        boolean flag = false;
        if( symbol == '(' ) {
            ++bracket_balance;
        }
        if( symbol == ')' ) {
            --bracket_balance;
        }
        if( !( ( '0' <= symbol ) && ( symbol <= '9' ) || ( symbol == '+' )
                || ( symbol == '-' ) || ( symbol == '*' ) || ( symbol == '.' )
                || ( symbol == '/' ) || ( symbol == ' ' )
                || ( symbol == '(' ) || ( symbol == ')' ) ) ) {
            throw new ParsingException("Not a valid expression");
        }
        return bracket_balance;
    }

    private boolean IsValid( String expression ) throws ParsingException {
        int bracket_balance = 0;
        for( int i = 0; i < expression.length(); ++i) {
            bracket_balance = IsWrong( expression.charAt( i ), bracket_balance );
        }
        if( bracket_balance != 0 ) {
            throw new ParsingException("Not a valid expression");
        }
        return true;
    }

    private int SkipSpaces( String expression, int cur_pos, int end_pos ) {
        while( cur_pos < end_pos && expression.charAt(cur_pos) == ' ' ) {
            ++cur_pos;
        }
        return cur_pos;
    }

    private pair EvaluateExpression( String expression, int cur_pos, int end_pos ) throws ParsingException {
        pair result_pair;
        double result = 0;
        result_pair = Multiplier( expression, cur_pos, end_pos );
        result = result_pair.first;
        cur_pos = result_pair.second;
        while( cur_pos < end_pos && expression.charAt( cur_pos ) != ')' ) {
            if( IsNumber( expression.charAt( cur_pos ) ) || expression.charAt( cur_pos ) == '('
                        || expression.charAt( cur_pos ) == '.' ) {
                throw new ParsingException("Not a valid expression");
            }
            if( expression.charAt( cur_pos ) == '+' ) {
                result_pair = Multiplier( expression, cur_pos + 1, end_pos );
                result += result_pair.first;
                cur_pos = result_pair.second;
                continue;
            }
            if ( expression.charAt( cur_pos ) == '-' ) {
                result_pair = Multiplier( expression, cur_pos + 1, end_pos );
                result -= result_pair.first;
                cur_pos = result_pair.second;
                continue;
            }
            if ( expression.charAt( cur_pos ) == '/' ) {
                result_pair = Multiplier( expression, cur_pos + 1, end_pos );
                result /= result_pair.first;
                cur_pos = result_pair.second;
                continue;
            }
        }
        result_pair.first = result;
        result_pair.second = cur_pos;
        return result_pair;
    }

    private pair GetNextLexem( String expression, int cur_pos, int end_pos ) throws ParsingException {
        int sign = 1;
        pair result_pair = new pair();
        double result = 0;
        double fractional_part = 0;
        int order = 0;
        if( cur_pos < end_pos && expression.charAt( cur_pos ) == '-') {
            cur_pos += 1;
            sign = -1;
        }
        while( cur_pos < end_pos && IsNumber( expression.charAt( cur_pos ) ) ) {
            result *= 10;
            result += expression.charAt(cur_pos) - ( int )'0';
            cur_pos += 1;
        }
        if( cur_pos < end_pos && expression.charAt( cur_pos ) == '.' ) {
            cur_pos += 1;
            if( cur_pos == end_pos || !IsNumber( expression.charAt( cur_pos ) ) ) {
                throw new ParsingException("Not a valid expression");
            }
            while( cur_pos < end_pos && IsNumber( expression.charAt( cur_pos ) ) ) {
                order += 1;
                fractional_part *= 10;
                fractional_part += expression.charAt( cur_pos ) - ( int )'0';
                cur_pos += 1;
            }
            for( int i = 0; i < order; ++i){
                fractional_part /= 10;
            }
            if( cur_pos < end_pos && expression.charAt( cur_pos ) == '.' ) {
                throw new ParsingException( "Not a valid expression" );
            }
        result += fractional_part;
        }
        result_pair.first = sign * result;
        result_pair.second = cur_pos;
        return result_pair;
    }

    private pair Multiplier( String expression, int cur_pos, int end_pos ) throws ParsingException {
        if( cur_pos == end_pos ) {
            throw new ParsingException("Not a valid expression");
        }
        pair result_pair;
        double result = 0;
        cur_pos = SkipSpaces( expression, cur_pos, end_pos );
        if( expression.charAt( cur_pos ) == '/' || expression.charAt( cur_pos ) == ')' ) {
            throw new ParsingException("Not a valid expression");
        }
        int sign = 1;
        if( expression.charAt( cur_pos ) == '+' ) {
            sign = 1;
            cur_pos += 1;
        } else {
            if( expression.charAt(cur_pos) == '-') {
                sign = -1;
                cur_pos += 1;
            }
        }
        if( IsNumber(expression.charAt( cur_pos ) ) ) {
            result_pair = GetNextLexem( expression, cur_pos, end_pos );
            result = result_pair.first;
            cur_pos = result_pair.second;
        } else {
            if( expression.charAt( cur_pos ) == '(' ) {
                result_pair = EvaluateExpression( expression, cur_pos + 1, end_pos );
                result = result_pair.first;
                cur_pos = result_pair.second;
                if( expression.charAt( cur_pos ) == ')' ) {
                    ++cur_pos;
                } else {
                    throw new ParsingException("Not a valid expression");
                }
            } else {
                throw new ParsingException("Not a valid expression");
            }
        }
        result *= sign;
        cur_pos = SkipSpaces( expression, cur_pos, end_pos );
        while( cur_pos < end_pos && ( expression.charAt( cur_pos ) == '*' || expression.charAt( cur_pos ) == '/' ) ) {
            char operation = expression.charAt( cur_pos );
            cur_pos = SkipSpaces( expression, cur_pos + 1, end_pos );
            if( operation == '*') {
                result_pair = Multiplier( expression, cur_pos, end_pos );
                result *= result_pair.first;
            } else {
                result_pair = GetNextLexem( expression, cur_pos, end_pos );
                result /= result_pair.first;
            }
            cur_pos = result_pair.second;
            cur_pos = SkipSpaces( expression, cur_pos, end_pos );
        }
        result_pair.first = result;
        result_pair.second = cur_pos;
        return result_pair;
    }

    private class pair{
        double first;
        int second;
    }
}