package com.dao;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.entity.VideoLog;

import config.HibernateConfig;

public class VideoLogDao {
        private static final Logger logger = LogManager.getLogger(VideoLogDao.class);


        public void add(VideoLog log){
            Transaction transaction = null;
            try(Session session = HibernateConfig.getSessionFactory().openSession()){
                transaction = session.beginTransaction();
                session.persist(log);
                transaction.commit();
            }catch(Exception e){
                if(transaction != null){
                    transaction.rollback();
                }
                logger.error(e.getMessage());
            }
        }



}
