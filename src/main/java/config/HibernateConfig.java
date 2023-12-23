package config;

import java.util.Properties;

import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Environment;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

import com.entity.KeyValue;
import com.entity.Reminder;
import com.entity.Subscription;
import com.entity.VideoLog;

public class HibernateConfig {
    private static SessionFactory sessionFactory;

    public static SessionFactory getSessionFactory() {
        if(sessionFactory == null){
                Configuration configuration = new Configuration();
                Config config = Config.getInstance();
                String username = config.get("db_username");
               
                String password = config.get("db_password");
                String url = config.get("db_url");
                // Hibernate settings equivalent to hibernate.cfg.xml's properties
                Properties settings = new Properties();


                settings.put(Environment.DRIVER, "com.mysql.cj.jdbc.Driver");
                settings.put(Environment.URL, url);
                settings.put(Environment.USER, username);
                settings.put(Environment.PASS, password);
                settings.put(Environment.SHOW_SQL, "true");

                settings.put(Environment.CURRENT_SESSION_CONTEXT_CLASS, "thread");

                
                if(config.get("env").equalsIgnoreCase("test"))
                    settings.put(Environment.HBM2DDL_AUTO, "create-drop");
                else
                    settings.put(Environment.HBM2DDL_AUTO, "update");
                
                configuration.setProperties(settings);

                configuration.addAnnotatedClass(Subscription.class);
                configuration.addAnnotatedClass(VideoLog.class);
                configuration.addAnnotatedClass(KeyValue.class);
                //configuration.addAnnotatedClass(Reminder.class);

                ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                    .applySettings(configuration.getProperties()).build();

                sessionFactory = configuration.buildSessionFactory(serviceRegistry);
        }
        return sessionFactory;
    }

    public static void setSessionFactory(SessionFactory sessionFactory) {
        HibernateConfig.sessionFactory = sessionFactory;
    }
    


}
