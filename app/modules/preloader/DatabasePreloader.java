package modules.preloader;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import models.DataSource;
import play.Logger;
import play.db.jpa.JPAApi;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import java.util.AbstractMap;
import java.util.Comparator;
import java.util.Map.Entry;
import java.util.ArrayList;

@Singleton
public class DatabasePreloader {
    private final static Config conf = ConfigFactory.load();
    private static DatabasePreloader singleton = null;
    private static Boolean enabled =
            conf.hasPath("databasePreloader.enable") && conf.getBoolean("databasePreloader.enable");
    private static Boolean testEnabled =
            conf.hasPath("databasePreloader.addTestData") && conf.getBoolean("databasePreloader.addTestData");

    private static ArrayList<Entry<Integer, Preloadable>> defaultTasks = new ArrayList<>();
    private static ArrayList<Entry<Integer, Preloadable>> testTasks = new ArrayList<>();
    private JPAApi jpa;

    @Inject
    DatabasePreloader(JPAApi jpa) {
        synchronized (conf) {
            this.jpa = jpa;
            singleton = this;

            jpa.withTransaction(() -> {
                EntityManager em = jpa.em();
                CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
                CriteriaQuery<Long> query = criteriaBuilder.createQuery(Long.class);
                query.select(criteriaBuilder.count(query.from(DataSource.class)));
                if (em.createQuery(query).getSingleResult() != 0) {
                    Logger.info("Database not empty, skipping preload");
                    enabled = false;
                    return;
                }

                Logger.info("Preloading database");
                defaultTasks.sort(Comparator.comparingInt(Entry::getKey));
                for (Entry<Integer, Preloadable> a : defaultTasks) {
                    a.getValue().run(em);
                }
                if (testEnabled) {
                    testTasks.sort(Comparator.comparingInt(Entry::getKey));
                    for (Entry<Integer, Preloadable> a : testTasks) {
                        a.getValue().run(em);
                    }
                }
            });
        }
    }

    public static void addDefault(Preloadable task, int priority) {
        synchronized (conf) {
            if (singleton != null)
                throw new RuntimeException("Cannot register register new default db preload after database initialization!");
            defaultTasks.add(new AbstractMap.SimpleImmutableEntry<>(priority, task));
        }
    }

    public static void addTest(Preloadable task, int priority) {
        synchronized (conf) {
            testTasks.add(new AbstractMap.SimpleImmutableEntry<>(priority, task));
            if (singleton != null && enabled && testEnabled) singleton.loadTest(task);
        }
    }

    private void load(Preloadable task) {
        jpa.withTransaction(() -> task.run(jpa.em()));
    }

    private void loadTest(Preloadable task) {
        if (enabled && testEnabled) load(task);
    }
}
