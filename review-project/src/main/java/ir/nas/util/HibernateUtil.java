package ir.nas.util;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;

import java.util.function.Consumer;
import java.util.function.Function;

public class HibernateUtil
{
    private static final String PERSISTENCE_UNIT = "postgresql-unit";

    private static EntityManagerFactory emf;

    public static synchronized EntityManagerFactory emf()
    {
        if (emf == null) {
            emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT);
        }
        return emf;
    }

    public static EntityManager em()
    {
        return emf().createEntityManager();
    }

    public static <T> T read(Function<EntityManager, T> operation)
    {
        EntityManager em = em();
        try {
            return operation.apply(em);
        } finally {
            em.close();
        }
    }

    public static void inTx(Consumer<EntityManager> operation)
    {
        inTxReturn(em -> {
            operation.accept(em);
            return null;
        });
    }

    public static <T> T inTxReturn(Function<EntityManager, T> operation)
    {
        EntityManager em = em();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();
            T result = operation.apply(em);
            tx.commit();
            return result;
        } catch (RuntimeException e) {
            tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }
}