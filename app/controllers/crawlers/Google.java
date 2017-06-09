package controllers.crawlers;

import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.Result;

import java.io.IOException;
import java.security.GeneralSecurityException;


public class Google extends Controller {
    private crawlers.google.Google crawler;

    public Google() throws GeneralSecurityException, IOException {
        crawler = new crawlers.google.Google();
    }

    public Result redirectToGoogle() {
        return redirect(crawler.getAuthorizationUrl());
    }

    @Transactional
    public Result authenticated() throws IOException {
        flash("success", "Processed " + crawler.processPeople(request().getQueryString("code")) + " people");
        return redirect(controllers.people.routes.People.get());
    }
}
