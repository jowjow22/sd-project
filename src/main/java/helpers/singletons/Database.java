package helpers.singletons;

import jakarta.persistence.TypedQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

public class Database {
    final StandardServiceRegistry registry = new StandardServiceRegistryBuilder().configure().build();
    final SessionFactory sessionFactory = new MetadataSources(registry).buildMetadata().buildSessionFactory();
    Session session = sessionFactory.openSession();
    private static Database instance = null;


    private Database() {
    }

    public static Database getInstance() {
        if (instance == null) {
            instance = new Database();
        }
        return instance;
    }

    public void connect() {
        System.out.println("Connected to database");
    }

    public <T> T selectByPK(Class<T> entityClass, int id) {
        return session.find(entityClass, id);
    }

    public <T> T getOneByQuery(String query, Class<T> entityClass) {
        TypedQuery<T> typedQuery = session.createQuery(query, entityClass);
        return typedQuery.getSingleResult();
    }

    public void insert(Object entity)  {
        Transaction transaction = session.beginTransaction();
        session.persist(entity);
        transaction.commit();
    }

    public void update(Object entity) {
        Transaction transaction = session.beginTransaction();
        session.update(entity);
        transaction.commit();
    }

    public void delete(Object entity) {
        Transaction transaction = session.beginTransaction();
        session.delete(entity);
        transaction.commit();
    }

    public void disconnect() {
        session.close();
        sessionFactory.close();
        System.out.println("Disconnected from database");
    }
}
