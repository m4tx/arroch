package controllers.groups;

import models.SocialGroup;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;
import play.mvc.Result;
import utils.SimpleQuery;
import views.html.pages.group.groupList;

import java.util.List;

import static play.mvc.Results.ok;

public class Groups {
    @Transactional
    public Result get() {
        return getPage(1);
    }

    @Transactional
    public Result getPage(int page) {
        List<? extends SocialGroup> groups = (new SimpleQuery<>(JPA.em(), SocialGroup.class)
                .orderByAsc("name")
        ).getResultList();
        return ok(groupList.render(groups, 25, page));
    }
}
