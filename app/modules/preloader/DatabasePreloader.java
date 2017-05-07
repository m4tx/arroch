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
import java.util.PriorityQueue;

@Singleton
public class DatabasePreloader {
    private static DatabasePreloader singleton = null;

    private final static Config conf = ConfigFactory.load();
    private static Boolean enabled =
            conf.hasPath("databasePreloade.enable") && conf.getBoolean("databasePreloade.enable");
    private static Boolean testEnabled =
            conf.hasPath("databasePreloade.addTestData") && conf.getBoolean("databasePreloade.addTestData");

    private static PriorityQueue<Entry<Integer, Preloadable>> defaultTasks = new PriorityQueue<>(Comparator.comparingInt(Entry::getKey));
    private static PriorityQueue<Entry<Integer, Preloadable>> testTasks = new PriorityQueue<>(Comparator.comparingInt(Entry::getKey));

    public static void addDefault(Preloadable task, int priority) {
        synchronized (conf) {
            if (singleton != null)
                throw new RuntimeException("Cannot register register new default db preload after database initialization!");
            if (!enabled) return;
            defaultTasks.add(new AbstractMap.SimpleImmutableEntry<>(priority, task));
        }
    }

    public static void addTest(Preloadable task, int priority) {
        synchronized (conf) {
            if (!enabled || !testEnabled) return;
            if (singleton == null) testTasks.add(new AbstractMap.SimpleImmutableEntry<>(priority, task));
            else singleton.loadTest(task);
        }
    }

    private JPAApi jpa;

    private void load(Preloadable task) {
        jpa.withTransaction(() -> task.run(jpa.em()));
    }

    private void loadDefault(Preloadable task) {
        if (enabled) load(task);
    }

    private void loadTest(Preloadable task) {
        if (enabled && testEnabled) load(task);
    }

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
                while (!defaultTasks.isEmpty()) {
                    loadDefault(defaultTasks.poll().getValue());
                }
                while (!testTasks.isEmpty()) {
                    loadDefault(testTasks.poll().getValue());
                }
            });
        }
    }
}
