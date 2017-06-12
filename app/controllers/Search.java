package controllers;

import models.Message;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.query.dsl.QueryBuilder;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.pages.search;

import javax.persistence.EntityManager;
import java.util.List;

public class Search extends Controller {
    @Transactional
    public Result search() {
        String queryString = request().getQueryString("q");
        if (queryString.length() < 2) {
            flash("error", "Please enter at least 2 characters");
            return redirect(routes.Application.index());
        }

        EntityManager em = JPA.em();
        FullTextEntityManager fullTextEntityManager =
                org.hibernate.search.jpa.Search.getFullTextEntityManager(em);

        QueryBuilder qb = fullTextEntityManager.getSearchFactory()
                .buildQueryBuilder().forEntity(Message.class).get();
        org.apache.lucene.search.Query luceneQuery = qb
                .keyword()
                .onFields("body")
                .matching(queryString)
                .createQuery();

        javax.persistence.Query jpaQuery =
                fullTextEntityManager.createFullTextQuery(luceneQuery, Message.class);

        @SuppressWarnings("unchecked")
        List<Message> results = (List<Message>) jpaQuery.getResultList();
        return ok(search.render(results, queryString));
    }
}
