package helpers.singletons;

import jakarta.persistence.TypedQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.exception.ConstraintViolationException;

import java.sql.SQLException;
import exceptions.EmailAlreadyInUseException;

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
        System.out.println(query);
        TypedQuery<T> typedQuery = session.createQuery(query, entityClass);
        return typedQuery.getSingleResult();
    }

    public void insert(Object entity) throws EmailAlreadyInUseException {
        Transaction transaction = session.beginTransaction();
        try {
            session.persist(entity);
        }
        catch (ConstraintViolationException e){
            throw new EmailAlreadyInUseException("Email already in use", e.getSQLException());
        }
        finally {
            transaction.commit();
        }
    }

    public void update(Object entity) throws  EmailAlreadyInUseException{
        Transaction transaction = session.beginTransaction();
        try {
            session.update(entity);
            transaction.commit();
        }
        catch (ConstraintViolationException e){
            System.out.printf("Email already in use: %s%n", e.getSQLException().getMessage());
            throw new EmailAlreadyInUseException("Email already in use", e.getSQLException());
        }
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
