package com.dao;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.entity.Reminder;

import config.HibernateConfig;

public class ReminderDao {
    private static final Logger logger = LogManager.getLogger(ReminderDao.class);


    public List<Reminder> getAllReminders() {
        try(Session session = HibernateConfig.getSessionFactory().openSession()){
            return session.createQuery("FROM Reminder", Reminder.class).getResultList();
        }catch(Exception e){
            logger.error("ReminderDao::getAllReminders -> " + e.getMessage());    
        }
        return null;
        
    }

    public void saveReminder(Reminder reminder) {
        Transaction transaction = null;
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.persist(reminder);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            logger.error("ReminderDao::saveReminder -> " + e.getMessage());
        }
    }

}
