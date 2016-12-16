package ru.mipt.java2016.homework.g595.murzin.task4;

import org.springframework.security.core.context.SecurityContextHolder;
import ru.mipt.java2016.homework.g595.murzin.task1.MyContext;

/**
 * Created by dima on 16.12.16.
 */
public class ContextProvider implements AutoCloseable {
    private final BillingDao billingDao;
    public final MyContext context;

    public ContextProvider(BillingDao billingDao) {
        this.billingDao = billingDao;
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        context = billingDao.getContext(username);
    }

    @Override
    public void close() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        billingDao.putContext(username, context);
    }
}
