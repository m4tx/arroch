package crawlers.facebook;

import java.io.IOException;

public class Facebook {
    private FacebookSession session = new FacebookSession();

    public String testFetchPage(String uri) throws IOException {
        return session.fetchPage(uri);
    }
}
