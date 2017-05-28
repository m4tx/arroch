package modules.preloader;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import play.Logger;
import play.api.inject.ApplicationLifecycle;
import play.db.jpa.JPAApi;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.EntityManager;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.AbstractMap;
import java.util.Comparator;
import java.util.Map.Entry;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

@Singleton
public class DatabasePreloader {
    private static DatabasePreloader singleton = null;
    private static Boolean testEnabled = false;

    private static ArrayList<Entry<Integer, Class<?>>> defaultTasks = new ArrayList<>();
    private static ArrayList<Entry<Integer, Preloadable>> testTasks = new ArrayList<>();
    private JPAApi jpa;

    @Inject
    DatabasePreloader(JPAApi jpa, ApplicationLifecycle lifecycle) {
        synchronized (DatabasePreloader.class) {
            if (singleton != null) throw new IllegalStateException("Only one instance of a singleton allowed");
            this.jpa = jpa;
            singleton = this;

            lifecycle.addStopHook(() -> {
                singleton = null;
                return CompletableFuture.completedFuture(null);
            });

            Config conf = ConfigFactory.load();
            testEnabled = conf.hasPath("databasePreloader.addTestData") && conf.getBoolean("databasePreloader.addTestData");

            jpa.withTransaction(() -> {
                EntityManager em = jpa.em();


                defaultTasks.sort(Comparator.comparingInt(Entry::getKey));
                for (Entry<Integer, Class<?>> a : defaultTasks) {
                    loadDefault(a.getValue(), em);
                }
                if (testEnabled) {
                    Logger.info("Loading test data");
                    testTasks.sort(Comparator.comparingInt(Entry::getKey));
                    for (Entry<Integer, Preloadable> a : testTasks) {
                        a.getValue().run(em);
                    }
                }
            });
        }
    }

    public static synchronized void addDefault(int priority, Class<?> valueMap) {
        if (singleton != null)
            throw new RuntimeException("Cannot register register new default db preload after database initialization!");
        defaultTasks.add(new AbstractMap.SimpleImmutableEntry<>(priority, valueMap));

    }

    public static synchronized void addTest(Preloadable task, int priority) {
        testTasks.add(new AbstractMap.SimpleImmutableEntry<>(priority, task));
        if (singleton != null && testEnabled) singleton.loadTest(task);
    }

    private void loadDefault(Class<?> valueMap, EntityManager em) {
        Field[] declaredFields = valueMap.getDeclaredFields();
        for (Field field : declaredFields) {
            if (Modifier.isStatic(field.getModifiers()) && field.isAnnotationPresent(DefaultValue.class)) {
                try {
                    Object c = field.get(null);
                    em.persist(c);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("Cannot Access default model value field\n" + e.toString());
                }
            }
        }
    }

    private void loadTest(Preloadable task) {
        if (testEnabled) jpa.withTransaction(() -> task.run(jpa.em()));
    }
}
