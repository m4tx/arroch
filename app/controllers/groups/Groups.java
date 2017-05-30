package controllers.groups;

import models.Group;
import models.GroupType;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;
import play.mvc.Result;
import utils.SimpleQuery;
import views.html.groupList;

import java.util.List;

import static play.mvc.Results.ok;

public class Groups {
    @Transactional
    public Result get() {
        return getPage(1);
    }

    @Transactional
    public Result getPage(int page) {
        List<Group> groups = (new SimpleQuery<>(JPA.em(), Group.class)
                .where("type", GroupType.GroupTypeList.social)
                .orderByAsc("name")
        ).getResultList();
        return ok(groupList.render(groups, 25, page));
    }
}
