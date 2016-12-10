package ru.mipt.java2016.homework.g594.kozlov.task2.serializer;

import ru.mipt.java2016.homework.g594.kozlov.task2.StorageException;
import java.util.Date;

/**
 * Created by Anatoly on 25.10.2016.
 */
public class SerializerUtil {
    public static void checkDoubleDot(String token) throws StorageException {
        if (!token.equals(":")) {
            throw new StorageException("Reading error");
        }
    }

    public static void checkComma(String token) throws StorageException {
        if (!token.equals(",")) {
            throw new StorageException("Reading error");
        }
    }

    public static void checkBracket(String token) throws StorageException {
        if (!(token.equals("{") || token.equals("}"))) {
            throw new StorageException("Reading error");
        }
    }

    public static void checkBracket(char token) throws StorageException {
        if (!(token == '{' || token == '}')) {
            throw new StorageException("Reading error");
        }
    }

    public static int tryParseInt(String token) throws StorageException {
        int result;
        try {
            result = Integer.parseInt(token);
        } catch (NumberFormatException except) {
            throw new StorageException("Reading error");
        }
        return result;
    }

    public static double tryParseDouble(String token) throws StorageException {
        double result;
        try {
            result = Double.parseDouble(token);
        } catch (NumberFormatException except) {
            throw new StorageException("Reading error");
        }
        return result;
    }

    public static Date tryParseDate(String inputStr) throws StorageException {
        Date result;
        try {
            long res = Long.parseLong(inputStr);
            result = new Date(res);
        } catch (NumberFormatException e) {
            throw new StorageException("Reading error");
        }
        return result;
    }

    public static Boolean tryParseBoolean(String token) throws StorageException {
        Boolean result;
        try {
            result = Boolean.parseBoolean(token);
        } catch (IllegalArgumentException except) {
            throw new StorageException("Reading error");
        }
        return result;
    }

    public static int readMemberInt(String memberName, String inputString) throws StorageException {
        String[] tokens = inputString.split("\"");
        if (tokens.length < 4) {
            throw new StorageException("Reading error");
        }
        if (!tokens[1].equals(memberName)) {
            throw new StorageException("Reading error");
        }
        SerializerUtil.checkDoubleDot(tokens[2]);
        return tryParseInt(tokens[3]);
    }

    public static double readMemberDouble(String memberName, String inputString) throws StorageException {
        String[] tokens = inputString.split("\"");
        if (tokens.length < 4) {
            throw new StorageException("Reading error");
        }
        if (!tokens[1].equals(memberName)) {
            throw new StorageException("Reading error");
        }
        SerializerUtil.checkDoubleDot(tokens[2]);
        return tryParseDouble(tokens[3]);
    }

    public static String readMemberString(String memberName, String inputString) throws StorageException {
        String[] tokens = inputString.split("\"");
        if (tokens.length < 4) {
            throw new StorageException("Reading error");
        }
        if (!tokens[1].equals(memberName)) {
            throw new StorageException("Reading error");
        }
        SerializerUtil.checkDoubleDot(tokens[2]);
        return tokens[3];
    }

    public static Date readMemberDate(String memberName, String inputString) throws StorageException {
        String[] tokens = inputString.split("\"");
        if (tokens.length < 4) {
            throw new StorageException("Reading error");
        }
        if (!tokens[1].equals(memberName)) {
            throw new StorageException("Reading error");
        }
        SerializerUtil.checkDoubleDot(tokens[2]);
        return tryParseDate(tokens[3]);
    }

    public static boolean readMemberBoolean(String memberName, String inputString) throws StorageException {
        String[] tokens = inputString.split("\"");
        if (tokens.length < 4) {
            throw new StorageException("Reading error");
        }
        if (!tokens[1].equals(memberName)) {
            throw new StorageException("Reading error");
        }
        SerializerUtil.checkDoubleDot(tokens[2]);
        return tryParseBoolean(tokens[3]);
    }

    public static String writeMemberString(String memberName, String inputString) {
        StringBuilder resultString = new StringBuilder("\"");
        resultString.append(memberName)
                .append("\":\"")
                .append(inputString)
                .append("\"");
        return resultString.toString();
    }

    public static String writeMemberInt(String memberName, int inputInt) {
        StringBuilder resultString = new StringBuilder("\"");
        resultString.append(memberName)
                .append("\":\"")
                .append(Integer.toString(inputInt))
                .append("\"");
        return resultString.toString();
    }

    public static String writeMemberDouble(String memberName, double inputDouble) {
        StringBuilder resultString = new StringBuilder("\"");
        resultString.append(memberName)
                .append("\":\"")
                .append(Double.toString(inputDouble))
                .append("\"");
        return resultString.toString();
    }

    public static String writeMemberDate(String memberName, Date inputDate) {
        StringBuilder resultString = new StringBuilder("\"");
        resultString.append(memberName)
                .append("\":\"")
                .append(inputDate.getTime())
                .append("\"");
        return resultString.toString();
    }

    public static String writeMemberBoolean(String memberName, Boolean inputBoolean) {
        StringBuilder resultString = new StringBuilder("\"");
        resultString.append(memberName)
                .append("\":\"")
                .append(Boolean.toString(inputBoolean))
                .append("\"");
        return resultString.toString();
    }
}
