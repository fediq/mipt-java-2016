package ru.mipt.java2016.homework.g596.kozlova.task4;

public class BillingUser {

    private final String userName;
    private final String password;
    private final boolean enabled;

    public BillingUser(String u, String p, boolean e) {
        if (u.equals(null) || p.equals(null)) {
            throw new IllegalArgumentException("Incorrect data");
        }
        userName = u;
        password = p;
        enabled = e;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public String toString() {
        return "BillingUser{ userName='" + userName + "\', password='" + password + '\'' + ", enabled=" + enabled + "}";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        BillingUser that = (BillingUser) obj;
        if (!userName.equals(that.userName) || !password.equals(that.password) || enabled != that.enabled) {
            return false;
        }
        return true;
    }
 }