package ru.tinkoff.qa.hibernate;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateSessionFactoryCreator {
    public static SessionFactory createSessionFactory(){
        return new Configuration()
                .configure("/hibernate.cfg.xml")
                .buildSessionFactory();
    }
}
