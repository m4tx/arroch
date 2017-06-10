package crawlers.facebook;

import org.apache.commons.io.IOUtils;

import java.io.*;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import play.Logger;

class FacebookSession {
    private static final String COOKIES_FILENAME = "/facebook_cookies";

    private String cookies;

    FacebookSession() {
        InputStream secretsStream = getClass().getResourceAsStream(COOKIES_FILENAME);

        try {
            cookies = IOUtils.toString(secretsStream, "utf8").trim();
        } catch (IOException e) {
            Logger.warn("Cannot open Facebook cookies file " + COOKIES_FILENAME);
        }
    }

    Connection getConnection(String uri) {
        Connection c = Jsoup.connect(uri);
        c.header("cookie", cookies);
        c.header("user-agent", "Mozilla/5.0 (Windows NT 6.3; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36");
        return c;
    }

    Document getDocument(String uri) throws IOException {
        return getConnection(uri).execute().parse();
    }
}
