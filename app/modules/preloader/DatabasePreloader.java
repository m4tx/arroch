package modules.preloader;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import models.DataSource;
import play.Logger;
import play.api.inject.ApplicationLifecycle;
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
import java.util.concurrent.CompletableFuture;

@Singleton
public class DatabasePreloader {
    private static DatabasePreloader singleton = null;
    private static Boolean enabled = false;
    private static Boolean testEnabled = false;

    private static ArrayList<Entry<Integer, Preloadable>> defaultTasks = new ArrayList<>();
    private static ArrayList<Entry<Integer, Preloadable>> testTasks = new ArrayList<>();
    private static ArrayList<Entry<Integer, Preloadable>> testCleanupTasks = new ArrayList<>();
    private JPAApi jpa;

    @Inject
    DatabasePreloader(JPAApi jpa, ApplicationLifecycle lifecycle) {
        synchronized (DatabasePreloader.class) {
            if (singleton != null) throw new IllegalStateException("Only one instance of a singleton allowed");
            this.jpa = jpa;
            singleton = this;

            lifecycle.addStopHook(() -> {
                singleton = null;
                if (enabled && testEnabled) {
                    jpa.withTransaction(() -> {
                        EntityManager em = jpa.em();
                        for (Entry<Integer, Preloadable> a : testCleanupTasks) {
                            a.getValue().run(em);
                        }
                    });
                }
                return CompletableFuture.completedFuture(null);
            });

            Config conf = ConfigFactory.load();
            enabled = conf.hasPath("databasePreloader.enable") && conf.getBoolean("databasePreloader.enable");
            testEnabled = conf.hasPath("databasePreloader.addTestData") && conf.getBoolean("databasePreloader.addTestData");

            if (!enabled) return;

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

    public static synchronized void addDefault(Preloadable task, int priority) {
        if (singleton != null)
            throw new RuntimeException("Cannot register register new default db preload after database initialization!");
        defaultTasks.add(new AbstractMap.SimpleImmutableEntry<>(priority, task));

    }

    public static synchronized void addTest(Preloadable task, int priority) {
        testTasks.add(new AbstractMap.SimpleImmutableEntry<>(priority, task));
        if (singleton != null && enabled && testEnabled) singleton.loadTest(task);
    }

    public static synchronized void addTestCleanup(Preloadable task, int priority) {
        testCleanupTasks.add(new AbstractMap.SimpleImmutableEntry<>(priority, task));
    }

    private void load(Preloadable task) {
        jpa.withTransaction(() -> task.run(jpa.em()));
    }

    private void loadTest(Preloadable task) {
        if (enabled && testEnabled) load(task);
    }
}
