package org.example.lb295.daos;

import org.example.lb295.configs.HibernateUtil;
import org.example.lb295.models.Category;
import org.example.lb295.models.Recipe;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class RecipeDAO {

    private final SessionFactory sessionFactory = HibernateUtil.getSessionFactory();

    public List<Recipe> getAll() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM Recipe", Recipe.class).list();
        }
    }

    public Recipe getById(int id) {
        try (Session session = sessionFactory.openSession()) {
            return session.get(Recipe.class, id);
        }
    }

    public List<Recipe> getByVegetarisch(boolean vegetarisch) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM Recipe WHERE vegetarisch = :v", Recipe.class)
                    .setParameter("v", vegetarisch)
                    .list();
        }
    }

    public List<Recipe> getByMinBewertung(BigDecimal minBewertung) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM Recipe WHERE bewertung >= :b", Recipe.class)
                    .setParameter("b", minBewertung)
                    .list();
        }
    }

    public long count() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("SELECT COUNT(r) FROM Recipe r", Long.class).uniqueResult();
        }
    }

    public Recipe create(Recipe recipe) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            if (recipe.getKategorie() != null && recipe.getKategorie().getKategorieId() != 0) {
                Category managed = session.get(Category.class, recipe.getKategorie().getKategorieId());
                recipe.setKategorie(managed);
            }
            session.persist(recipe);
            session.getTransaction().commit();
            return recipe;
        }
    }

    public List<Recipe> createBatch(List<Recipe> rezepte) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            for (Recipe recipe : rezepte) {
                if (recipe.getKategorie() != null && recipe.getKategorie().getKategorieId() != 0) {
                    Category managed = session.get(Category.class, recipe.getKategorie().getKategorieId());
                    recipe.setKategorie(managed);
                }
                session.persist(recipe);
            }
            session.getTransaction().commit();
            return rezepte;
        }
    }

    public Recipe update(int id, Recipe neu) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            Recipe recipe = session.get(Recipe.class, id);
            if (recipe == null) {
                session.getTransaction().rollback();
                return null;
            }
            recipe.setName(neu.getName());
            recipe.setBeschreibung(neu.getBeschreibung());
            recipe.setZubereitungszeit(neu.getZubereitungszeit());
            recipe.setKosten(neu.getKosten());
            recipe.setErstelltAm(neu.getErstelltAm());
            recipe.setVegetarisch(neu.isVegetarisch());
            recipe.setBewertung(neu.getBewertung());
            if (neu.getKategorie() != null && neu.getKategorie().getKategorieId() != 0) {
                Category managed = session.get(Category.class, neu.getKategorie().getKategorieId());
                recipe.setKategorie(managed);
            }
            session.merge(recipe);
            session.getTransaction().commit();
            return recipe;
        }
    }

    public boolean delete(int id) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            Recipe recipe = session.get(Recipe.class, id);
            if (recipe == null) {
                session.getTransaction().rollback();
                return false;
            }
            session.remove(recipe);
            session.getTransaction().commit();
            return true;
        }
    }

    public int deleteBeforeDate(LocalDateTime datum) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            int anzahl = session.createMutationQuery("DELETE FROM Recipe WHERE erstelltAm < :datum")
                    .setParameter("datum", datum)
                    .executeUpdate();
            session.getTransaction().commit();
            return anzahl;
        }
    }

    public void createSeedData() {
        CategoryDAO categoryDAO = new CategoryDAO();

        Category k1 = new Category();
        k1.setName("Italienisch");
        Category k2 = new Category();
        k2.setName("Asiatisch");
        k1 = categoryDAO.create(k1);
        k2 = categoryDAO.create(k2);

        Recipe r1 = new Recipe();
        r1.setName("Spaghetti Carbonara");
        r1.setBeschreibung("Classic Italian pasta dish");
        r1.setZubereitungszeit(30);
        r1.setKosten(new BigDecimal("12.50"));
        r1.setErstelltAm(LocalDateTime.now().minusDays(10));
        r1.setVegetarisch(false);
        r1.setBewertung(new BigDecimal("4.5"));
        r1.setKategorie(k1);

        Recipe r2 = new Recipe();
        r2.setName("Pad Thai");
        r2.setBeschreibung("Fried rice noodles with peanuts");
        r2.setZubereitungszeit(25);
        r2.setKosten(new BigDecimal("10.00"));
        r2.setErstelltAm(LocalDateTime.now().minusDays(5));
        r2.setVegetarisch(true);
        r2.setBewertung(new BigDecimal("4.0"));
        r2.setKategorie(k2);

        create(r1);
        create(r2);
    }
}
