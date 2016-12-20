package ru.mipt.java2016.homework.g595.romanenko.task4;

/**
 * ru.mipt.java2016.homework.g595.romanenko.task4
 *
 * @author Ilya I. Romanenko
 * @since 16.12.16
 **/
public class RestUser {

    private final Integer id;
    private final String username;
    private final String password;
    private final boolean enabled;

    public RestUser(Integer id, String username, String password, boolean enabled) {
        if (id == null) {
            throw new IllegalArgumentException("Id can't be null");
        }
        if (username == null) {
            throw new IllegalArgumentException("Null username is not allowed");
        }
        if (password == null) {
            throw new IllegalArgumentException("Null password is not allowed");
        }
        this.id = id;
        this.username = username;
        this.password = password;
        this.enabled = enabled;
    }

    public Integer getId() {
        return id;
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
        return "BillingUser{" +
            "id=" + id + '\'' +
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

        RestUser that = (RestUser) o;

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
