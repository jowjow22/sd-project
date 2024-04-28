package helpers.singletons;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;

public class Database {
    private static Database instance = null;
    private static final EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("databaseDistribution");
    private static final EntityManager entityManager = entityManagerFactory.createEntityManager();

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
        return entityManager.find(entityClass, id);
    }

    public <T> T getOneByQuery(String query, Class<T> entityClass) {
        TypedQuery<T> typedQuery = entityManager.createQuery(query, entityClass);
        return typedQuery.getSingleResult();
    }

    public void insert(Object entity) {
        entityManager.getTransaction().begin();
        entityManager.persist(entity);
        entityManager.getTransaction().commit();
    }

    public void update(Object entity) {
        entityManager.getTransaction().begin();
        entityManager.merge(entity);
        entityManager.getTransaction().commit();
    }

    public void delete(Object entity) {
        entityManager.getTransaction().begin();
        entityManager.remove(entity);
        entityManager.getTransaction().commit();
    }

    public void disconnect() {
        entityManager.close();
        entityManagerFactory.close();
        System.out.println("Disconnected from database");
    }
}
