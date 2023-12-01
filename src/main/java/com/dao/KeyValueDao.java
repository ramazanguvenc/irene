package com.dao;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import com.entity.KeyValue;
import config.HibernateConfig;

public class KeyValueDao {
    private static final Logger logger = LogManager.getLogger(KeyValueDao.class);


    public void set(KeyValue keyValue){
        Transaction transaction = null;
        try(Session session = HibernateConfig.getSessionFactory().openSession()){
            transaction = session.beginTransaction();
            session.persist(keyValue);
            transaction.commit();
        }catch(Exception e){
            if(transaction != null){
                transaction.rollback();
            }
            logger.error(e.getMessage());
        }
    }

    public String get(String key){
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            Query<KeyValue> query = session.createQuery(
                "from KeyValue where `key` = :key",  KeyValue.class
            );
            query.setParameter("key", key);
            return query.getSingleResult().getValue();
        }catch(Exception e){
            logger.error(e.getMessage());
            return null;
        }
    }
}