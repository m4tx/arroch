package controllers.posts;

import play.db.jpa.JPA;
import play.db.jpa.Transactional;
import play.mvc.Result;
import views.html.pages.post;

import static play.mvc.Results.ok;

public class Post {
    @Transactional
    public Result get(Long id) {
        return ok(post.render(JPA.em().find(models.Post.class, id)));
    }
}
