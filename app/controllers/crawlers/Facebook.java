package controllers.crawlers;

import play.db.jpa.Transactional;
import play.mvc.Result;

import java.io.IOException;

import static play.mvc.Controller.flash;
import static play.mvc.Results.redirect;

public class Facebook {
    private crawlers.facebook.Facebook crawler;

    public Facebook() throws IOException {
        crawler = new crawlers.facebook.Facebook();
    }

    @Transactional
    public Result fetchPeople() throws IOException {
        flash("success", "Processed " + crawler.processPeople());
        return redirect(controllers.people.routes.People.get());
    }

}
