package utils;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.SingularAttribute;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

    public SimpleQuery<T> where(String attribute, Object value) {
        query = query.where(builder.equal(from.get(attribute), value));
        return this;
    }

    public SimpleQuery<T> whereAllEqual(Map<String, Object> values) {
        ArrayList<Predicate> predicates = new ArrayList<>();
        for(Map.Entry<String, Object> v : values.entrySet()) {
            predicates.add(builder.equal(from.get(v.getKey()), v.getValue()));
        }
        query = query.where(predicates.toArray(new Predicate[0]));
        return this;
    }

    public List<T> getResultList() {
        return em.createQuery(query).getResultList();
    }
}
