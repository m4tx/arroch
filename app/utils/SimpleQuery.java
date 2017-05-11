package utils;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

public class SimpleQuery<T> {
    private EntityManager em;
    private CriteriaBuilder builder;
    private CriteriaQuery<T> query;
    private Root<T> from;

    public SimpleQuery(EntityManager em, Class<T> model) {
        this.em = em;
        builder = em.getCriteriaBuilder();
        query = builder.createQuery(model);
        from = query.from(model);

        query = query.select(from);
    }

    public SimpleQuery<T> orderByAsc(String field) {
        query = query.orderBy(builder.asc(from.get(field)));
        return this;
    }

    public SimpleQuery<T> orderByDesc(String field) {
        query = query.orderBy(builder.desc(from.get(field)));
        return this;
    }

    public List<T> getResultList() {
        return em.createQuery(query).getResultList();
    }
}
