package models;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

public class DatabaseConnection {
    private final SessionFactory factory;

    private DatabaseConnection() {
        final StandardServiceRegistry registry = new StandardServiceRegistryBuilder().configure().build();
        factory = new MetadataSources(registry).buildMetadata().buildSessionFactory();
    }

    private static final class InstanceHolder {
        private static final DatabaseConnection instance = new DatabaseConnection();
    }

    public static DatabaseConnection getInstance() {
        return InstanceHolder.instance;
    }

    public void insert(Object object) {
        try (Session session = factory.openSession()) {
            session.beginTransaction();
            session.persist(object);
            session.getTransaction().commit();
            System.out.println("[LOG]: Objeto inserido/atualizado.");
        } catch (Exception e) {
            System.out.println("[LOG]: Erro na inserção do objeto.");
        }
    }

    public void update(Object object) {
        try (Session session = factory.openSession()) {
            session.beginTransaction();
            session.update(object);
            session.getTransaction().commit();
            System.out.println("[LOG]: Objeto inserido/atualizado.");
        } catch (Exception e) {
            System.out.println("[LOG]: Erro na inserção do objeto.");
        }
    }

    public <T> T select(int id, Class<T> returnClass){
        try (Session session = factory.openSession()) {
            return session.find(returnClass, id);
        } catch (Exception e) {
            System.out.println("[LOG]: Erro na inserção do objeto.");
        }
        return null;
    }

    public <T> void delete(int id, Class<T> entityClass) {
        try (Session session = factory.openSession()) {
            Transaction transaction = session.beginTransaction();
            T objectToDelete = session.get(entityClass, id);
            if (objectToDelete != null) {
                session.delete(objectToDelete);
                transaction.commit();
            } else {
                System.out.println("[LOG]: Objeto não encontrado com o ID fornecido.");
            }
        } catch (Exception e) {
            System.out.println("[LOG]: Erro na exclusão do objeto.");
        }
    }

    public Candidate verifyLogin(String email, String password) {
        Session session = factory.openSession();
            return session.createQuery("FROM Candidate WHERE email = :email AND password = :password", Candidate.class)
                    .setParameter("email", email)
                    .setParameter("password", password)
                    .uniqueResult();
    }
}
