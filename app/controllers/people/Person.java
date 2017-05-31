package controllers.people;

import play.db.jpa.JPA;
import play.db.jpa.Transactional;
import play.mvc.Result;
import utils.SimpleQuery;
import views.html.*;

import java.util.List;

import static play.mvc.Results.ok;

public class Person {
    @Transactional
    public Result get(Long id) {
        return getInfo(id);
    }

    @Transactional
    public Result getInfo(Long id) {
        return ok(personInfo.render(JPA.em().find(models.Person.class, id)));
    }

    @Transactional
    public Result getAccounts(Long id) {
        return ok(personAccounts.render(JPA.em().find(models.Person.class, id)));
    }

    @Transactional
    public Result getFriends(Long id) {
        return getFriendsPage(id, 1);
    }

    @Transactional
    public Result getFriendsPage(Long id, int page) {
        return ok(personFriends.render(JPA.em().find(models.Person.class, id), 25, page));
    }

    @Transactional
    public Result getFriendOf(Long id) {
        return getFriendOfPage(id, 1);
    }

    @Transactional
    public Result getFriendOfPage(Long id, int page) {
        return ok(personFriendOf.render(JPA.em().find(models.Person.class, id), 25, page));
    }

    @Transactional
    public Result getFiles(Long id) {
        return ok(personFiles.render(JPA.em().find(models.Person.class, id)));
    }

    @Transactional
    public Result getActivity(Long id) {
        return ok(personActivity.render(JPA.em().find(models.Person.class, id)));
    }

    @Transactional
    public Result getConversations(Long id) {
        return ok(personConversations.render(JPA.em().find(models.Person.class, id)));
    }

    @Transactional
    public Result getGroups(Long id) {
        return getGroupsPage(id, 1);
    }

    @Transactional
    public Result getGroupsPage(Long id, int page) {
        models.Person person = JPA.em().find(models.Person.class, id);
        List<models.Group> groups = (new SimpleQuery<>(JPA.em(), models.Group.class))
                .where("type", models.GroupType.GroupTypeList.social)
                .getResultList();
        return ok(personGroups.render(person, groups, 25, page));
    }
}
