package ru.mipt.java2016.homework.g597.moiseev.task4;

public class CalculationUser {
    private final String username;
    private final String password;
    private final boolean enabled;

    public CalculationUser(String username, String password, boolean enabled) {
        if (username == null) {
            throw new IllegalArgumentException("Null username is not allowed");
        }
        if (password == null) {
            throw new IllegalArgumentException("Null password is not allowed");
        }
        this.username = username;
        this.password = password;
        this.enabled = enabled;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public String toString() {
        return "CalculationUser{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", enabled=" + enabled +
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

        CalculationUser that = (CalculationUser) o;

        if (enabled != that.enabled) {
            return false;
        }
        if (!username.equals(that.username)) {
            return false;
        }
        return password.equals(that.password);

    }

    @Override
    public int hashCode() {
        int result = username.hashCode();
        result = 31 * result + password.hashCode();
        result = 31 * result + (enabled ? 1 : 0);
        return result;
    }
}
