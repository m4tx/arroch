package controllers.posts;

import play.db.jpa.JPA;
import play.db.jpa.Transactional;
import play.mvc.Result;
import views.html.pages.post.upvotesList;
import views.html.pages.post.tagsList;

import static play.mvc.Results.ok;

public class Message {
    @Transactional
    public Result getUpvotes(Long id) {
        return getUpvotesPage(id, 1);
    }

    @Transactional
    public Result getUpvotesPage(Long id, int page) {
        return ok(upvotesList.render(JPA.em().find(models.Message.class, id), 25, page));
    }

    @Transactional
    public Result getTags(Long id) {
        return getTagsPage(id, 1);
    }

    @Transactional
    public Result getTagsPage(Long id, int page) {
        return ok(tagsList.render(JPA.em().find(models.Message.class, id), 25, page));
    }
}
