package controllers.crawlers;

import play.mvc.Result;

import java.io.IOException;

import static play.mvc.Results.ok;

public class Facebook {
    private crawlers.facebook.Facebook crawler;

    public Facebook() throws IOException {
        crawler = new crawlers.facebook.Facebook();
    }

    public Result getStatus() {
        return ok();
    }

    public Result fetchIndex() throws IOException {
        return ok(crawler.testFetchPage("https://mbasic.facebook.com/home.php")).as("text/html");
    }
}
