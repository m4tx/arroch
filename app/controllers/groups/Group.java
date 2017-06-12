package controllers.groups;

import models.Message;
import models.Post;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;
import play.mvc.Result;
import utils.SimpleQuery;
import views.html.pages.group.*;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;

import java.util.List;

import static play.mvc.Results.ok;

public class Group {
    @Transactional
    public Result get(Long id) {
        return getActivity(id);
    }

    @Transactional
    public Result getActivity(Long id) {
        return getActivityPage(id, 1);
    }

    @Transactional
    public Result getActivityPage(Long id, int page) {
        EntityManager em = JPA.em();
        models.Group g = em.find(models.Group.class, id);

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Post> query = cb.createQuery(Post.class);
        Root<Post> posts = query.from(Post.class);
        Join<Post, Message> messages = posts.join("thread");
        query.where(cb.equal(posts.get("group"), g));
        query.orderBy(cb.desc(messages.get("timestamp")));
        List<Post> p = em.createQuery(query).getResultList();
        return ok(groupActivity.render(g, p, g instanceof models.ConversationGroup ? 25 : 5, page));
    }

    @Transactional
    public Result getMembers(Long id) {
        return getMembersPage(id, 1);
    }

    @Transactional
    public Result getMembersPage(Long id, int page) {
        return ok(groupMembers.render(JPA.em().find(models.Group.class, id), 25, page));
    }

    @Transactional
    public Result getFiles(Long id) {
        return getFilesPage(id, 1);
    }

    @Transactional
    public Result getFilesPage(Long id, int page) {
        return ok(groupFiles.render(JPA.em().find(models.Group.class, id), 25, page));
    }
}
