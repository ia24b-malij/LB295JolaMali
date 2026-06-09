package org.example.lb295.daos;

import org.example.lb295.configs.HibernateUtil;
import org.example.lb295.models.Category;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.List;

public class CategoryDAO {

    private final SessionFactory sessionFactory = HibernateUtil.getSessionFactory();

    public List<Category> getAll() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM Category", Category.class).list();
        }
    }

    public Category getById(int id) {
        try (Session session = sessionFactory.openSession()) {
            return session.get(Category.class, id);
        }
    }

    public Category create(Category category) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.persist(category);
            session.getTransaction().commit();
            return category;
        }
    }

    public Category update(int id, Category neu) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            Category category = session.get(Category.class, id);
            if (category == null) {
                session.getTransaction().rollback();
                return null;
            }
            category.setName(neu.getName());
            session.merge(category);
            session.getTransaction().commit();
            return category;
        }
    }

    public boolean delete(int id) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            Category category = session.get(Category.class, id);
            if (category == null) {
                session.getTransaction().rollback();
                return false;
            }
            Long count = session.createQuery(
                    "SELECT COUNT(r) FROM Recipe r WHERE r.category.kategorieId = :id", Long.class)
                    .setParameter("id", id)
                    .uniqueResult();
            if (count != null && count > 0) {
                session.getTransaction().rollback();
                return false;
            }
            session.remove(category);
            session.getTransaction().commit();
            return true;
        }
    }
}
