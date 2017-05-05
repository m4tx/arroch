package modules;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import models.DataSource;
import play.Logger;
import play.api.Play;
import play.db.jpa.JPAApi;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;


@Singleton
class DatabasePreloader {
    private void loadDefault(EntityManager em) {
        //Place database preload code here
    }

    private void loadRandom(EntityManager em) {
        //Place database preload code here
    }


    @Inject
    public DatabasePreloader(JPAApi jpa) {
        Config conf = ConfigFactory.load();

        if ((!conf.hasPath("databasePreloade.enable")) || (!conf.getBoolean("databasePreloade.enable"))) return;

        jpa.withTransaction(() -> {
            EntityManager em = jpa.em();
            CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
            CriteriaQuery<Long> query = criteriaBuilder.createQuery(Long.class);
            query.select(criteriaBuilder.count(query.from(DataSource.class)));
            if (em.createQuery(query).getSingleResult() != 0) {
                Logger.info("Skipping database preload");
                return;
            }

            Logger.info("Loading default database values...");
            loadDefault(em);
            Logger.info("Default values loaded");

            if (conf.hasPath("databasePreloade.addRandomData") &&
                    conf.getBoolean("databasePreloade.addRandomData")) {
                Logger.info("Adding random database values...");
                loadRandom(em);
                Logger.info("Random values added");
            }

        });
    }
}
