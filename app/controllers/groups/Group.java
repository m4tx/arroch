package controllers.groups;

import play.db.jpa.JPA;
import play.db.jpa.Transactional;
import play.mvc.Result;
import views.html.pages.group.*;

import javax.persistence.EntityManager;

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
        return ok(groupActivity.render(JPA.em().find(models.Group.class, id), 5, page));
    }

    @Transactional
    public Result getMembers(Long id) {
        return getMembersPage(id, 1);
    }

    @Transactional
    public Result getMembersPage(Long id, int page) {
        return ok(groupMembers.render(JPA.em().find(models.Group.class, id), 25, page));
    }
}
