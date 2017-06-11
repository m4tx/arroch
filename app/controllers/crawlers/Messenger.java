package controllers.crawlers;

import play.db.jpa.Transactional;
import play.mvc.Result;

import java.io.IOException;
import java.net.URISyntaxException;

import static play.mvc.Controller.flash;
import static play.mvc.Results.redirect;

public class Messenger {
    private crawlers.messenger.Messenger crawler;

    public Messenger() throws IOException {
        crawler = new crawlers.messenger.Messenger();
    }

    @Transactional
    public Result fetchThreads() throws IOException, URISyntaxException {
        crawler.process(10);
        flash("success", "Processed 10 threads");
        return redirect(controllers.groups.routes.Groups.get());
    }
}
