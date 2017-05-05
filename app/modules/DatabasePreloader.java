package modules;

import models.DataSource;
import play.Logger;
import play.db.jpa.JPAApi;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;


@Singleton
class DatabasePreloader {

    @Inject
    public DatabasePreloader(JPAApi jpa) {
        jpa.withTransaction(()->{
            EntityManager em = jpa.em();
            CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
            CriteriaQuery<Long> query = criteriaBuilder.createQuery(Long.class);
            query.select(criteriaBuilder.count(query.from(DataSource.class)));
            if(em.createQuery(query).getSingleResult() != 0) {
                Logger.info("Skipping database preload");
                return;
            }

            Logger.info("Loading default database values...");

            //Place database preload code here
        });
    }
}
