package ru.mipt.java2016.homework.g597.vasilyev.task4;

/**
 * Created by mizabrik on 21.12.16.
 */
public class CalculatorUser {
    private final String username;
    private final String password;

    public CalculatorUser(String username, String password) {
        if (username == null) {
            throw new IllegalArgumentException("Null username is not allowed");
        }
        if (password == null) {
            throw new IllegalArgumentException("Null password is not allowed");
        }
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public String toString() {
        return "BillingUser{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CalculatorUser that = (CalculatorUser) o;

        if (!username.equals(that.username)) {
            return false;
        }
        return password.equals(that.password);
    }

    @Override
    public int hashCode() {
        int result = username.hashCode();
        result = 31 * result + password.hashCode();
        return result;
    }
}
