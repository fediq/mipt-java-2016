package ru.mipt.java2016.homework.g596.litvinov.task4;

/**
 * Created by
 *
 * @author Stanislav A. Litvinov
 * @since 19.12.16.
 */
public class BillingUser {
    private final String username;
    private final String password;
    private final boolean enabled;

    public BillingUser(String username, String password, boolean enabled) {
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

    @Override
    public int hashCode() {
        int result = getUsername().hashCode();
        result = 31 * result + getPassword().hashCode();
        result = 31 * result + (isEnabled() ? 1 : 0);
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BillingUser)) {
            return false;
        }

        BillingUser that = (BillingUser) o;

        if (isEnabled() != that.isEnabled()) {
            return false;
        }
        if (!getUsername().equals(that.getUsername())) {
            return false;
        }
        return getPassword().equals(that.getPassword());

    }

    @Override
    public String toString() {
        return "BillingUser{" + "username='" + username + '\'' + ", password='" + password + '\''
                + ", enabled=" + enabled + '}';
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
}
