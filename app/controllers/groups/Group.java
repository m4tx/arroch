package controllers.groups;

import play.db.jpa.JPA;
import play.db.jpa.Transactional;
import play.mvc.Result;
import views.html.group;

import javax.persistence.EntityManager;

import static play.mvc.Results.ok;

public class Group {
    @Transactional
    public Result get(Long id) {
        EntityManager em = JPA.em();
        models.Group p = em.find(models.Group.class, id);
        return ok(group.render(p));
    }
}
