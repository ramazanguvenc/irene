package com.dao;

import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

import org.hibernate.Session;
import com.entity.Subscription;

import config.HibernateConfig;

public class SubscriptionDao {
    public void subscribe(Subscription subscription) {
        Transaction transaction = null;
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            // start a transaction
            transaction = session.beginTransaction();
            // save the student object
            session.persist(subscription);
            // commit transaction
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    public List <Subscription> getSubscriptions() {
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            return session.createQuery("from Subscription", Subscription.class).list();
        }
    }

    public List<Subscription> getSpecificSubscriptions(String specificType) {
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            Query<Subscription> query = session.createQuery(
                "from Subscription where type = :specificType", Subscription.class
            );
            query.setParameter("specificType", specificType);
            return query.list();
        }
    }
    
}
