package util;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import com.auca.librarymanagement.model.*;
import java.util.Properties;

public class HibernateUtil {
    private static SessionFactory sessionFactory = null;

    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            try {
                Configuration conf = new Configuration();
                Properties settings = new Properties();

                // Database connection settings
                settings.setProperty(Environment.DRIVER, "org.postgresql.Driver");
                settings.setProperty(Environment.URL, "jdbc:postgresql://localhost:5432/auca_library_db");
                settings.setProperty(Environment.USER, "postgres");
                settings.setProperty(Environment.PASS, "prince123");

                // Hibernate settings
                settings.setProperty(Environment.SHOW_SQL, "true");
                settings.setProperty(Environment.FORMAT_SQL, "true");
                settings.setProperty(Environment.HBM2DDL_AUTO, "update");
                settings.setProperty(Environment.DIALECT, "org.hibernate.dialect.PostgreSQLDialect");
                settings.setProperty(Environment.CURRENT_SESSION_CONTEXT_CLASS, "thread");

                // Performance optimization
                settings.setProperty(Environment.STATEMENT_BATCH_SIZE, "100");
                settings.setProperty(Environment.ORDER_UPDATES, "true");
                settings.setProperty(Environment.ORDER_INSERTS, "true");
                settings.setProperty(Environment.USE_NEW_ID_GENERATOR_MAPPINGS, "true");

                conf.setProperties(settings);

                // Register entity classes
                conf.addAnnotatedClass(User.class);
                conf.addAnnotatedClass(Book.class);
                conf.addAnnotatedClass(Borrower.class);
                conf.addAnnotatedClass(Location.class);
                conf.addAnnotatedClass(Membership.class);
                conf.addAnnotatedClass(MembershipType.class);
                conf.addAnnotatedClass(Room.class);
                conf.addAnnotatedClass(Shelf.class);

                sessionFactory = conf.buildSessionFactory();
            } catch (Throwable ex) {
                System.err.println("Initial SessionFactory creation failed: " + ex);
                ex.printStackTrace();
                throw new ExceptionInInitializerError(ex);
            }
        }
        return sessionFactory;
    }

    public static Session openSession() {
        return getSessionFactory().openSession();
    }

    public static void closeSession(Session session) {
        if (session != null && session.isOpen()) {
            session.close();
        }
    }

    public static void shutdown() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }
}