package ru.tinkoff.qa.dbtests;

import jakarta.persistence.PersistenceException;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.tinkoff.qa.hibernate.BeforeCreator;
import ru.tinkoff.qa.hibernate.HibernateSessionFactoryCreator;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class ZooHibernateTests {
    Session session;

    @BeforeAll
    static void init() {
        BeforeCreator.createData();
    }

    @BeforeEach
    public void createSession() {
        session = HibernateSessionFactoryCreator.createSessionFactory().openSession();
    }

    /**
     * В таблице public.animal ровно 10 записей
     */
    @Test
    public void countRowAnimal() {
        long expectedRows2 = 10;
        String sql = "SELECT count(*) FROM animal";
        Query countQuery = session.createNativeQuery(sql);
        assertEquals(expectedRows2, countQuery.getSingleResult(), "Check number of rows in the Animal table");
    }

    /**
     * В таблицу public.animal нельзя добавить строку с индексом от 1 до 10 включительно
     */
    @Test
    public void insertIndexAnimal() {
        Random random = new Random();
        int id = random.nextInt(10) + 1;
        String sql = "INSERT INTO animal (id, \"name\", age, \"type\", sex, place) VALUES(" + id + ", 'Пчелка', 4, 2, 1, 1);";
        Query insertQuery = session.createNativeQuery(sql);
        assertThrows(PersistenceException.class, () -> {
            session.beginTransaction();
            insertQuery.executeUpdate();
            session.getTransaction().commit();
        });
    }

    /**
     * В таблицу public.workman нельзя добавить строку с name = null
     */
    @Test
    public void insertNullToWorkman() {
        String sql = "INSERT INTO workman (id, \"name\", age, \"position\") VALUES(1, null, 23, 1);";
        Query insertQuery = session.createNativeQuery(sql);
        assertThrows(PersistenceException.class, () -> {
            session.beginTransaction();
            insertQuery.executeUpdate();
            session.getTransaction().commit();
        });
    }

    /**
     * Если в таблицу public.places добавить еще одну строку, то в ней будет 6 строк
     */
    @Test
    public void insertPlacesCountRow() {
        long expectedRows = 5;
        String sqlCount = "SELECT COUNT(*) FROM places";
        assertEquals(expectedRows, session.createNativeQuery(sqlCount).getSingleResult(), "Check number of rows");
    }

    /**
     * В таблице public.zoo всего три записи с name 'Центральный', 'Северный', 'Западный'
     */
    @Test
    public void countRowZoo() {
        List<String> expectedArray = Arrays.asList("Центральный", "Северный", "Западный");
        String sql = "SELECT \"name\" FROM zoo";
        Query selectNames = session.createNativeQuery(sql);
        List<String> listOfNames = selectNames.getResultList();
        assertEquals(3, listOfNames.size(), "Check number of names");
        assertTrue(expectedArray.equals(listOfNames), "Check DB contains every name");
    }

    @AfterEach
    public void closeSession() {
        session.close();
    }
}
