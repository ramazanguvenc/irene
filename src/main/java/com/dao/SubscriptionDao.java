package com.dao;

import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import com.entity.Subscription;
import config.HibernateConfig;

public class SubscriptionDao {

    private static final Logger logger = LogManager.getLogger(SubscriptionDao.class);


    public void subscribe(Subscription subscription) {
        Transaction transaction = null;
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.persist(subscription);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            logger.error(e.getMessage());
        }
    }

    public List <Subscription> getSubscriptions() {
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            return session.createQuery("from Subscription", Subscription.class).list();
        }
    }

    public List<Subscription> getSpecificSubscriptionType(String specificType) {
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            Query<Subscription> query = session.createQuery(
                "from Subscription where type = :specificType", Subscription.class
            );
            query.setParameter("specificType", specificType);
            return query.list();
        }catch(Exception e){
            logger.error(e.getMessage());
            return null;
        }
    }
    
}
